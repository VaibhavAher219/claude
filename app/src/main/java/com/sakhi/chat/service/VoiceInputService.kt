package com.sakhi.chat.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class VoiceInputService(private val context: Context) {

    fun startListening(languageCode: String = "mr-IN"): Flow<VoiceResult> = callbackFlow {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageCode)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                trySend(VoiceResult.Listening)
            }

            override fun onBeginningOfSpeech() {
                trySend(VoiceResult.Speaking)
            }

            override fun onRmsChanged(rmsdB: Float) {
                trySend(VoiceResult.RmsChanged(rmsdB))
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "ऑडिओ रेकॉर्डिंग त्रुटी"
                    SpeechRecognizer.ERROR_CLIENT -> "क्लायंट त्रुटी"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "परवानगी नाही"
                    SpeechRecognizer.ERROR_NETWORK -> "नेटवर्क त्रुटी"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "नेटवर्क timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "आवाज समजला नाही"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "व्यस्त आहे"
                    SpeechRecognizer.ERROR_SERVER -> "सर्वर त्रुटी"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "बोलण्याची वेळ संपली"
                    else -> "अज्ञात त्रुटी"
                }
                trySend(VoiceResult.Error(errorMessage))
                close()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotEmpty()) {
                    trySend(VoiceResult.Success(text))
                }
                close()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotEmpty()) {
                    trySend(VoiceResult.Partial(text))
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)

        awaitClose {
            speechRecognizer.destroy()
        }
    }

    companion object {
        fun isAvailable(context: Context): Boolean {
            return SpeechRecognizer.isRecognitionAvailable(context)
        }
    }
}

sealed class VoiceResult {
    object Listening : VoiceResult()
    object Speaking : VoiceResult()
    data class RmsChanged(val value: Float) : VoiceResult()
    data class Partial(val text: String) : VoiceResult()
    data class Success(val text: String) : VoiceResult()
    data class Error(val message: String) : VoiceResult()
}
