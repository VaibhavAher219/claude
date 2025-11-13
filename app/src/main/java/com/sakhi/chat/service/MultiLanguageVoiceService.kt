package com.sakhi.chat.service

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class SupportedLanguage(val code: String, val displayName: String, val ttsLocale: String) {
    MARATHI("mr-IN", "मराठी", "mr-IN"),
    HINDI("hi-IN", "हिंदी", "hi-IN"),
    ENGLISH("en-IN", "English", "en-IN"),
    ENGLISH_US("en-US", "English (US)", "en-US")
}

class MultiLanguageVoiceService(private val context: Context) {

    private val voiceServices = mutableMapOf<SupportedLanguage, VoiceInputService>()
    private val ttsServices = mutableMapOf<SupportedLanguage, TextToSpeechService>()

    private var currentLanguage = SupportedLanguage.MARATHI

    fun getCurrentLanguage(): SupportedLanguage = currentLanguage

    fun setLanguage(language: SupportedLanguage) {
        currentLanguage = language
    }

    fun listenInLanguage(language: SupportedLanguage): Flow<VoiceResult> {
        val service = voiceServices.getOrPut(language) {
            VoiceInputService(context)
        }
        return service.startListening(language.code)
    }

    fun speakInLanguage(text: String, language: SupportedLanguage) {
        val service = ttsServices.getOrPut(language) {
            TextToSpeechService(context).apply {
                initialize()
            }
        }
        service.speak(text)
    }

    fun detectLanguage(text: String): SupportedLanguage {
        return when {
            // Marathi detection (Devanagari script specific to Marathi words)
            text.contains(Regex("[आईऊएऐओऔकखगघचछजझटठडढणतथदधनपफबभमयरलवशषसह]")) &&
            text.contains(Regex("(आहे|आहेत|नाही|काय|कसे|कुठे|कधी)")) -> SupportedLanguage.MARATHI

            // Hindi detection
            text.contains(Regex("[आईऊएऐओऔकखगघचछजझटठडढणतथदधनपफबभमयरलवशषसह]")) -> SupportedLanguage.HINDI

            // English detection
            text.matches(Regex("[a-zA-Z\\s.,!?]+")) -> SupportedLanguage.ENGLISH

            else -> currentLanguage // Default to current language
        }
    }

    fun listenWithAutoDetection(): Flow<MultiLanguageResult> {
        return listenInLanguage(currentLanguage).map { result ->
            when (result) {
                is VoiceResult.Success -> {
                    val detectedLanguage = detectLanguage(result.text)
                    if (detectedLanguage != currentLanguage) {
                        currentLanguage = detectedLanguage
                        MultiLanguageResult.LanguageSwitched(detectedLanguage, result.text)
                    } else {
                        MultiLanguageResult.Success(currentLanguage, result.text)
                    }
                }
                is VoiceResult.Partial -> MultiLanguageResult.Partial(currentLanguage, result.text)
                is VoiceResult.Error -> MultiLanguageResult.Error(result.message)
                else -> MultiLanguageResult.Listening
            }
        }
    }

    fun translateAndSpeak(text: String, targetLanguage: SupportedLanguage) {
        // In real implementation, call translation API
        // For now, just speak in target language
        speakInLanguage(text, targetLanguage)
    }

    fun cleanup() {
        ttsServices.values.forEach { it.shutdown() }
        ttsServices.clear()
        voiceServices.clear()
    }
}

sealed class MultiLanguageResult {
    object Listening : MultiLanguageResult()
    data class Partial(val language: SupportedLanguage, val text: String) : MultiLanguageResult()
    data class Success(val language: SupportedLanguage, val text: String) : MultiLanguageResult()
    data class LanguageSwitched(val newLanguage: SupportedLanguage, val text: String) : MultiLanguageResult()
    data class Error(val message: String) : MultiLanguageResult()
}
