package com.sakhi.chat.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.sakhi.chat.model.PersonalityFormality
import com.sakhi.chat.model.PersonalitySettings
import com.sakhi.chat.model.PersonalityTone
import com.sakhi.chat.model.PersonalityVerbosity
import com.sakhi.chat.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsManager(private val context: Context) {

    companion object {
        private val VOICE_INPUT_ENABLED = booleanPreferencesKey("voice_input_enabled")
        private val TTS_ENABLED = booleanPreferencesKey("tts_enabled")
        private val AUTO_PLAY_TTS = booleanPreferencesKey("auto_play_tts")
        private val SPEECH_RATE = floatPreferencesKey("speech_rate")
        private val SPEECH_PITCH = floatPreferencesKey("speech_pitch")
        private val OFFLINE_MODE = booleanPreferencesKey("offline_mode")
        private val IMAGE_GENERATION = booleanPreferencesKey("image_generation")

        // Personality
        private val PERSONALITY_NAME = stringPreferencesKey("personality_name")
        private val PERSONALITY_TONE = stringPreferencesKey("personality_tone")
        private val PERSONALITY_FORMALITY = stringPreferencesKey("personality_formality")
        private val PERSONALITY_VERBOSITY = stringPreferencesKey("personality_verbosity")
        private val CUSTOM_INSTRUCTIONS = stringPreferencesKey("custom_instructions")
    }

    val userSettingsFlow: Flow<UserSettings> = context.settingsDataStore.data.map { preferences ->
        UserSettings(
            voiceInputEnabled = preferences[VOICE_INPUT_ENABLED] ?: true,
            textToSpeechEnabled = preferences[TTS_ENABLED] ?: true,
            autoPlayTTS = preferences[AUTO_PLAY_TTS] ?: false,
            speechRate = preferences[SPEECH_RATE] ?: 1.0f,
            speechPitch = preferences[SPEECH_PITCH] ?: 1.0f,
            offlineModeEnabled = preferences[OFFLINE_MODE] ?: true,
            imageGenerationEnabled = preferences[IMAGE_GENERATION] ?: true,
            customPersonality = PersonalitySettings(
                name = preferences[PERSONALITY_NAME] ?: "सखी",
                tone = PersonalityTone.valueOf(preferences[PERSONALITY_TONE] ?: "FRIENDLY"),
                formality = PersonalityFormality.valueOf(preferences[PERSONALITY_FORMALITY] ?: "INFORMAL"),
                verbosity = PersonalityVerbosity.valueOf(preferences[PERSONALITY_VERBOSITY] ?: "BALANCED"),
                customInstructions = preferences[CUSTOM_INSTRUCTIONS] ?: ""
            )
        )
    }

    suspend fun updateVoiceInputEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[VOICE_INPUT_ENABLED] = enabled }
    }

    suspend fun updateTTSEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[TTS_ENABLED] = enabled }
    }

    suspend fun updateAutoPlayTTS(enabled: Boolean) {
        context.settingsDataStore.edit { it[AUTO_PLAY_TTS] = enabled }
    }

    suspend fun updateSpeechRate(rate: Float) {
        context.settingsDataStore.edit { it[SPEECH_RATE] = rate }
    }

    suspend fun updateSpeechPitch(pitch: Float) {
        context.settingsDataStore.edit { it[SPEECH_PITCH] = pitch }
    }

    suspend fun updateOfflineMode(enabled: Boolean) {
        context.settingsDataStore.edit { it[OFFLINE_MODE] = enabled }
    }

    suspend fun updateImageGeneration(enabled: Boolean) {
        context.settingsDataStore.edit { it[IMAGE_GENERATION] = enabled }
    }

    suspend fun updatePersonality(personality: PersonalitySettings) {
        context.settingsDataStore.edit { preferences ->
            preferences[PERSONALITY_NAME] = personality.name
            preferences[PERSONALITY_TONE] = personality.tone.name
            preferences[PERSONALITY_FORMALITY] = personality.formality.name
            preferences[PERSONALITY_VERBOSITY] = personality.verbosity.name
            preferences[CUSTOM_INSTRUCTIONS] = personality.customInstructions
        }
    }
}
