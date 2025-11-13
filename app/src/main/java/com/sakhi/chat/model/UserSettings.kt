package com.sakhi.chat.model

data class UserSettings(
    val voiceInputEnabled: Boolean = true,
    val textToSpeechEnabled: Boolean = true,
    val autoPlayTTS: Boolean = false,
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val offlineModeEnabled: Boolean = true,
    val imageGenerationEnabled: Boolean = true,
    val customPersonality: PersonalitySettings = PersonalitySettings()
)

data class PersonalitySettings(
    val name: String = "सखी",
    val tone: PersonalityTone = PersonalityTone.FRIENDLY,
    val formality: PersonalityFormality = PersonalityFormality.INFORMAL,
    val verbosity: PersonalityVerbosity = PersonalityVerbosity.BALANCED,
    val customInstructions: String = ""
)

enum class PersonalityTone {
    FRIENDLY,      // मैत्रीपूर्ण
    PROFESSIONAL,  // व्यावसायिक
    CASUAL,        // अनौपचारिक
    ENTHUSIASTIC   // उत्साही
}

enum class PersonalityFormality {
    FORMAL,        // औपचारिक
    INFORMAL       // अनौपचारिक
}

enum class PersonalityVerbosity {
    CONCISE,       // संक्षिप्त
    BALANCED,      // संतुलित
    DETAILED       // तपशीलवार
}
