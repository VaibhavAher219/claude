package com.sakhi.chat.service

import com.sakhi.chat.api.ChatMessage
import com.sakhi.chat.api.OpenAIRequest
import com.sakhi.chat.api.OpenAIService
import com.sakhi.chat.model.Message

class SummaryService(private val openAIService: OpenAIService) {

    suspend fun summarizeConversation(
        messages: List<Message>,
        summaryType: SummaryType = SummaryType.CONCISE
    ): Result<String> {
        return try {
            val conversationText = messages.joinToString("\n") { msg ->
                val speaker = if (msg.isFromUser) "User" else "AI"
                "$speaker: ${msg.text}"
            }

            val systemPrompt = when (summaryType) {
                SummaryType.CONCISE -> """तू एक तज्ञ सारांश लेखक आहेस. या संभाषणाचा संक्षिप्त सारांश द्या.
                    फक्त मुख्य मुद्दे समाविष्ट करा. 3-5 वाक्यांमध्ये उत्तर द्या."""

                SummaryType.DETAILED -> """तू एक तज्ञ सारांश लेखक आहेस. या संभाषणाचा तपशीलवार सारांश द्या.
                    सर्व महत्वाचे मुद्दे, चर्चा केलेले विषय, आणि निष्कर्ष समाविष्ट करा."""

                SummaryType.BULLET_POINTS -> """तू एक तज्ञ सारांश लेखक आहेस. या संभाषणाचा bullet points मध्ये सारांश द्या.
                    प्रत्येक मुख्य मुद्दा एका bullet point मध्ये लिहा."""

                SummaryType.ACTION_ITEMS -> """तू एक कार्य नियोजक आहेस. या संभाषणातून action items काढा.
                    काय करायचे आहे, कोणी करायचे, आणि कधी करायचे ते स्पष्ट करा."""
            }

            val openAIMessages = listOf(
                ChatMessage(role = "system", content = systemPrompt),
                ChatMessage(role = "user", content = "संभाषण:\n$conversationText\n\nसारांश द्या:")
            )

            val request = OpenAIRequest(
                model = "gpt-3.5-turbo",
                messages = openAIMessages,
                max_tokens = 500,
                temperature = 0.3
            )

            val response = openAIService.api.createChatCompletion(request)
            val summary = response.choices.firstOrNull()?.message?.content
                ?: "सारांश तयार करता आला नाही"

            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateTitle(messages: List<Message>): String {
        return try {
            val firstUserMessage = messages.firstOrNull { it.isFromUser }?.text ?: "New Chat"

            val systemPrompt = """या संभाषणासाठी एक छोटं, आकर्षक शीर्षक द्या.
                फक्त शीर्षक द्या, इतर काही नको. 5-7 शब्दांत."""

            val openAIMessages = listOf(
                ChatMessage(role = "system", content = systemPrompt),
                ChatMessage(role = "user", content = firstUserMessage)
            )

            val request = OpenAIRequest(
                model = "gpt-3.5-turbo",
                messages = openAIMessages,
                max_tokens = 50,
                temperature = 0.7
            )

            val response = openAIService.api.createChatCompletion(request)
            response.choices.firstOrNull()?.message?.content?.trim()?.take(50)
                ?: firstUserMessage.take(50)
        } catch (e: Exception) {
            messages.firstOrNull { it.isFromUser }?.text?.take(50) ?: "New Chat"
        }
    }
}

enum class SummaryType {
    CONCISE,        // संक्षिप्त
    DETAILED,       // तपशीलवार
    BULLET_POINTS,  // मुद्देवार
    ACTION_ITEMS    // कार्य यादी
}
