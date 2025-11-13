package com.sakhi.chat.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakhi.chat.api.OpenAIService
import com.sakhi.chat.model.Message
import com.sakhi.chat.repository.FirebaseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChatViewModel(
    private val firebaseRepository: FirebaseRepository,
    private val apiKeyProvider: suspend () -> String?
) : ViewModel() {

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private var openAIService: OpenAIService? = null

    init {
        loadMessagesFromFirebase()
    }

    private fun loadMessagesFromFirebase() {
        viewModelScope.launch {
            firebaseRepository.getMessagesFlow().collect { firebaseMessages ->
                _messages.clear()
                if (firebaseMessages.isEmpty()) {
                    // Add welcome message if no messages exist
                    val welcomeMessage = Message(
                        text = "नमस्कार! मी सखी आहे, तुमची AI सहाय्यक. आज मी तुम्हाला कशी मदत करू शकते?",
                        isFromUser = false
                    )
                    _messages.add(welcomeMessage)
                    firebaseRepository.saveMessage(welcomeMessage)
                } else {
                    _messages.addAll(firebaseMessages)
                }
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            // Add user message
            val userMessage = Message(
                text = text,
                isFromUser = true
            )
            _messages.add(userMessage)
            firebaseRepository.saveMessage(userMessage)

            // Check if API key is configured
            val apiKey = apiKeyProvider()
            if (apiKey.isNullOrBlank()) {
                val errorMessage = Message(
                    text = "कृपया सेटिंग्जमध्ये जाऊन OpenAI API key प्रविष्ट करा.",
                    isFromUser = false
                )
                _messages.add(errorMessage)
                firebaseRepository.saveMessage(errorMessage)
                return@launch
            }

            // Initialize OpenAI service if needed
            if (openAIService == null) {
                openAIService = OpenAIService(apiKey)
            }

            // Get AI response
            _isLoading.value = true
            try {
                val aiResponseText = openAIService!!.sendMessage(text, _messages.toList())
                val aiMessage = Message(
                    text = aiResponseText,
                    isFromUser = false
                )
                _messages.add(aiMessage)
                firebaseRepository.saveMessage(aiMessage)
            } catch (e: Exception) {
                val errorMessage = Message(
                    text = "त्रुटी: ${e.message ?: "अज्ञात समस्या"}",
                    isFromUser = false
                )
                _messages.add(errorMessage)
                firebaseRepository.saveMessage(errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            firebaseRepository.clearAllMessages()
            _messages.clear()

            val welcomeMessage = Message(
                text = "नमस्कार! मी सखी आहे, तुमची AI सहाय्यक. आज मी तुम्हाला कशी मदत करू शकते?",
                isFromUser = false
            )
            _messages.add(welcomeMessage)
            firebaseRepository.saveMessage(welcomeMessage)
        }
    }

    fun refreshApiKey() {
        viewModelScope.launch {
            val apiKey = apiKeyProvider()
            if (!apiKey.isNullOrBlank()) {
                openAIService = OpenAIService(apiKey)
            }
        }
    }
}
