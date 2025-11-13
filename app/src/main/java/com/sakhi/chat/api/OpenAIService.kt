package com.sakhi.chat.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

data class OpenAIRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<ChatMessage>,
    val max_tokens: Int = 500,
    val temperature: Double = 0.7
)

data class ChatMessage(
    val role: String, // "system", "user", or "assistant"
    val content: String
)

data class OpenAIResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage?
)

data class Choice(
    val message: ChatMessage,
    val finish_reason: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

interface OpenAIApi {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(@Body request: OpenAIRequest): OpenAIResponse
}

class OpenAIService(private val apiKey: String) {
    private val api: OpenAIApi

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(OpenAIApi::class.java)
    }

    suspend fun sendMessage(
        userMessage: String,
        conversationHistory: List<com.sakhi.chat.model.Message>
    ): String {
        try {
            // Convert conversation history to OpenAI format
            val messages = mutableListOf<ChatMessage>()

            // System message in Marathi
            messages.add(
                ChatMessage(
                    role = "system",
                    content = "तू सखी आहेस, एक मराठी भाषेतील AI सहाय्यक. तू नेहमी मराठीत उत्तर देतोस आणि वापरकर्त्यांना मदत करतोस. तू मैत्रीपूर्ण, सहाय्यक आणि माहितीपूर्ण आहेस."
                )
            )

            // Add conversation history (last 10 messages to avoid token limit)
            conversationHistory.takeLast(10).forEach { msg ->
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

            val response = api.createChatCompletion(request)
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
}
