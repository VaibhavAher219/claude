package com.sakhi.chat.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakhi.chat.api.ChatMessage
import com.sakhi.chat.api.OpenAIRequest
import com.sakhi.chat.api.OpenAIService
import com.sakhi.chat.model.Bot
import com.sakhi.chat.model.BotType
import com.sakhi.chat.model.Message
import com.sakhi.chat.repository.FirebaseRepository
import kotlinx.coroutines.launch

class ChatViewModel(
    private val firebaseRepository: FirebaseRepository,
    private val apiKeyProvider: suspend () -> String?,
    private val conversationId: String,
    private val botType: BotType
) : ViewModel() {

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private var openAIService: OpenAIService? = null
    private val bot = Bot.getBotByType(botType)

    init {
        loadMessagesFromFirebase()
    }

    private fun loadMessagesFromFirebase() {
        viewModelScope.launch {
            firebaseRepository.getMessagesForConversation(conversationId).collect { firebaseMessages ->
                _messages.clear()
                if (firebaseMessages.isEmpty()) {
                    // Add welcome message if no messages exist
                    val welcomeMessage = Message(
                        text = getWelcomeMessage(),
                        isFromUser = false,
                        conversationId = conversationId,
                        botType = botType
                    )
                    _messages.add(welcomeMessage)
                    firebaseRepository.saveMessage(welcomeMessage, conversationId)
                } else {
                    _messages.addAll(firebaseMessages)
                }
            }
        }
    }

    private fun getWelcomeMessage(): String {
        return when (botType) {
            BotType.RECIPE_MAKER -> "नमस्कार! मी ${bot.nameMarathi} आहे. तुम्हाला कोणत्या पाककृतीबद्दल माहिती हवी आहे?"
            BotType.TRAVEL_GUIDE -> "नमस्कार! मी ${bot.nameMarathi} आहे. तुम्ही कुठे प्रवास करू इच्छिता?"
            BotType.RELATIONSHIP_ADVICE -> "नमस्कार! मी ${bot.nameMarathi} आहे. मी तुम्हाला कसे मदत करू शकते?"
            BotType.ASTRONOMY -> "नमस्कार! मी ${bot.nameMarathi} आहे. अवकाशाबद्दल काय जाणून घ्यायचे आहे?"
            BotType.FITNESS_COACH -> "नमस्कार! मी ${bot.nameMarathi} आहे. तुमची फिटनेस लक्ष्ये काय आहेत?"
            BotType.LANGUAGE_TUTOR -> "नमस्कार! मी ${bot.nameMarathi} आहे. तुम्हाला कोणती भाषा शिकायची आहे?"
            BotType.CAREER_MENTOR -> "नमस्कार! मी ${bot.nameMarathi} आहे. करिअर बद्दल काय चर्चा करायची आहे?"
            BotType.HEALTH_ADVISOR -> "नमस्कार! मी ${bot.nameMarathi} आहे. तुमच्या आरोग्याबद्दल काय जाणून घ्यायचे आहे?"
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            // Add user message
            val userMessage = Message(
                text = text,
                isFromUser = true,
                conversationId = conversationId,
                botType = botType
            )
            _messages.add(userMessage)
            firebaseRepository.saveMessage(userMessage, conversationId)

            // Check if API key is configured
            val apiKey = apiKeyProvider()
            if (apiKey.isNullOrBlank()) {
                val errorMessage = Message(
                    text = "कृपया सेटिंग्जमध्ये जाऊन OpenAI API key प्रविष्ट करा.",
                    isFromUser = false,
                    conversationId = conversationId,
                    botType = botType
                )
                _messages.add(errorMessage)
                firebaseRepository.saveMessage(errorMessage, conversationId)
                return@launch
            }

            // Initialize OpenAI service if needed
            if (openAIService == null) {
                openAIService = OpenAIService(apiKey)
            }

            // Get AI response
            _isLoading.value = true
            try {
                val aiResponseText = getAIResponse(text)
                val aiMessage = Message(
                    text = aiResponseText,
                    isFromUser = false,
                    conversationId = conversationId,
                    botType = botType
                )
                _messages.add(aiMessage)
                firebaseRepository.saveMessage(aiMessage, conversationId)
            } catch (e: Exception) {
                val errorMessage = Message(
                    text = "त्रुटी: ${e.message ?: "अज्ञात समस्या"}",
                    isFromUser = false,
                    conversationId = conversationId,
                    botType = botType
                )
                _messages.add(errorMessage)
                firebaseRepository.saveMessage(errorMessage, conversationId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getAIResponse(userMessage: String): String {
        try {
            // Convert conversation history to OpenAI format
            val messages = mutableListOf<ChatMessage>()

            // System message with bot-specific prompt
            messages.add(
                ChatMessage(
                    role = "system",
                    content = bot.systemPrompt
                )
            )

            // Add conversation history (last 10 messages to avoid token limit)
            _messages.takeLast(10).forEach { msg ->
                messages.add(
                    ChatMessage(
                        role = if (msg.isFromUser) "user" else "assistant",
                        content = msg.text
                    )
                )
            }

            // Add current user message
            messages.add(ChatMessage(role = "user", content = userMessage))

            val request = OpenAIRequest(
                model = "gpt-3.5-turbo",
                messages = messages,
                max_tokens = 500,
                temperature = 0.7
            )

            val response = openAIService!!.api.createChatCompletion(request)
            return response.choices.firstOrNull()?.message?.content
                ?: "माफ करा, मला उत्तर देण्यात समस्या आली."

        } catch (e: Exception) {
            e.printStackTrace()
            return when {
                e.message?.contains("401") == true ->
                    "API key चुकीची आहे. कृपया सेटिंग्जमध्ये योग्य API key प्रविष्ट करा."
                e.message?.contains("429") == true ->
                    "API limit ओलांडली. कृपया थोड्या वेळाने पुन्हा प्रयत्न करा."
                e.message?.contains("timeout") == true ->
                    "नेटवर्क timeout. कृपया आपले इंटरनेट कनेक्शन तपासा."
                else ->
                    "त्रुटी: ${e.message ?: "अज्ञात समस्या"}"
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            firebaseRepository.clearConversation(conversationId)
            _messages.clear()

            val welcomeMessage = Message(
                text = getWelcomeMessage(),
                isFromUser = false,
                conversationId = conversationId,
                botType = botType
            )
            _messages.add(welcomeMessage)
            firebaseRepository.saveMessage(welcomeMessage, conversationId)
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
