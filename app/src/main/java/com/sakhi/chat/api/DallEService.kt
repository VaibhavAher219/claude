package com.sakhi.chat.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

data class DallERequest(
    val model: String = "dall-e-3",
    val prompt: String,
    val n: Int = 1,
    val size: String = "1024x1024",
    val quality: String = "standard", // or "hd"
    val style: String = "vivid" // or "natural"
)

data class DallEResponse(
    val created: Long,
    val data: List<ImageData>
)

data class ImageData(
    val url: String,
    val revised_prompt: String?
)

interface DallEApi {
    @POST("v1/images/generations")
    suspend fun generateImage(@Body request: DallERequest): DallEResponse
}

class DallEService(private val apiKey: String) {
    val api: DallEApi

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
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(DallEApi::class.java)
    }

    suspend fun generateImage(prompt: String): Result<String> {
        return try {
            val request = DallERequest(
                prompt = prompt,
                n = 1,
                size = "1024x1024",
                quality = "standard"
            )

            val response = api.generateImage(request)
            val imageUrl = response.data.firstOrNull()?.url

            if (imageUrl != null) {
                Result.success(imageUrl)
            } else {
                Result.failure(Exception("प्रतिमा तयार करता आली नाही"))
            }
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("401") == true -> "API key चुकीची आहे"
                e.message?.contains("429") == true -> "API limit ओलांडली"
                e.message?.contains("timeout") == true -> "नेटवर्क timeout"
                else -> "त्रुटी: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }
}
