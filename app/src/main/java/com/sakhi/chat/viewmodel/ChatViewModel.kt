package com.sakhi.chat.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakhi.chat.model.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    init {
        // Add welcome message in Marathi
        _messages.add(
            Message(
                text = "नमस्कार! मी सखी आहे, तुमची AI सहाय्यक. आज मी तुम्हाला कशी मदत करू शकते?",
                isFromUser = false
            )
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Add user message
        _messages.add(
            Message(
                text = text,
                isFromUser = true
            )
        )

        // Simulate AI response (placeholder - integrate with actual AI API later)
        viewModelScope.launch {
            delay(1000) // Simulate network delay
            val aiResponse = generateAIResponse(text)
            _messages.add(
                Message(
                    text = aiResponse,
                    isFromUser = false
                )
            )
        }
    }

    private fun generateAIResponse(userMessage: String): String {
        // Placeholder AI responses in Marathi
        // TODO: Integrate with actual AI API (OpenAI, Gemini, etc.)
        return when {
            userMessage.contains("नमस्कार") || userMessage.contains("हाय") ||
            userMessage.contains("hello", ignoreCase = true) ||
            userMessage.contains("hi", ignoreCase = true) ->
                "नमस्कार! मी तुम्हाला कशी मदत करू शकते?"

            userMessage.contains("कसे") || userMessage.contains("कशी") ||
            userMessage.contains("how", ignoreCase = true) ->
                "मी तुम्हाला विविध प्रश्नांची उत्तरे देऊ शकते, माहिती प्रदान करू शकते आणि तुमच्या कामात मदत करू शकते. काय करायचे आहे ते मला सांगा!"

            userMessage.contains("धन्यवाद") || userMessage.contains("thank", ignoreCase = true) ->
                "आपले स्वागत आहे! काही अधिक प्रश्न असल्यास नक्की विचारा."

            else ->
                "तुमचा संदेश समजला. मी एक AI सहाय्यक आहे आणि सध्या विकासाधीन आहे. लवकरच मी अधिक चांगल्या प्रकारे उत्तर देऊ शकेन!"
        }
    }

    fun clearChat() {
        _messages.clear()
        _messages.add(
            Message(
                text = "नमस्कार! मी सखी आहे, तुमची AI सहाय्यक. आज मी तुम्हाला कशी मदत करू शकते?",
                isFromUser = false
            )
        )
    }
}
