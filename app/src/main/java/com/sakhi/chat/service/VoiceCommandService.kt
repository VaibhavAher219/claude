package com.sakhi.chat.service

import android.content.Context
import com.sakhi.chat.model.BotType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VoiceCommandService(private val context: Context) {

    private val voiceInputService = VoiceInputService(context)

    // Wake words in different languages
    private val wakeWords = listOf(
        "सखी",      // Marathi
        "sakhi",    // English
        "साखी",     // Alternate Marathi spelling
        "hey sakhi",
        "हे सखी"
    )

    // Command patterns
    private val commandPatterns = mapOf(
        // Recipe commands
        Regex("(रेसिपी|recipe)\\s+(सांग|बता|tell|show)", RegexOption.IGNORE_CASE) to VoiceCommand.ShowRecipes,
        Regex("(कसं|कसे|how to)\\s+बनवायचं", RegexOption.IGNORE_CASE) to VoiceCommand.HowToCook,

        // Travel commands
        Regex("(प्रवास|travel|trip)\\s+(माहिती|info|guide)", RegexOption.IGNORE_CASE) to VoiceCommand.TravelInfo,
        Regex("(कुठे|where)\\s+(जायचं|go)", RegexOption.IGNORE_CASE) to VoiceCommand.WhereToGo,

        // Fitness commands
        Regex("(व्यायाम|exercise|workout)\\s+(सांग|show)", RegexOption.IGNORE_CASE) to VoiceCommand.ShowExercise,
        Regex("(फिटनेस|fitness)\\s+(प्लॅन|plan)", RegexOption.IGNORE_CASE) to VoiceCommand.FitnessPlan,

        // Calendar commands
        Regex("(आजचे|today's|my)\\s+(schedule|वेळापत्रक)", RegexOption.IGNORE_CASE) to VoiceCommand.ShowSchedule,
        Regex("(reminder|आठवण)\\s+(set|ठेव)", RegexOption.IGNORE_CASE) to VoiceCommand.SetReminder,

        // General commands
        Regex("(summary|सारांश)\\s+(दे|show|give)", RegexOption.IGNORE_CASE) to VoiceCommand.ShowSummary,
        Regex("(export|save)\\s+(pdf|conversation)", RegexOption.IGNORE_CASE) to VoiceCommand.ExportPDF,
        Regex("(नवीन|new)\\s+(चॅट|chat)", RegexOption.IGNORE_CASE) to VoiceCommand.NewChat,
        Regex("(मदत|help|साहाय्य)", RegexOption.IGNORE_CASE) to VoiceCommand.ShowHelp,

        // Bot selection
        Regex("(open|उघड)\\s+(recipe|पाककृती)", RegexOption.IGNORE_CASE) to VoiceCommand.OpenBot(BotType.RECIPE_MAKER),
        Regex("(open|उघड)\\s+(travel|प्रवास)", RegexOption.IGNORE_CASE) to VoiceCommand.OpenBot(BotType.TRAVEL_GUIDE),
        Regex("(open|उघड)\\s+(fitness|फिटनेस)", RegexOption.IGNORE_CASE) to VoiceCommand.OpenBot(BotType.FITNESS_COACH),
    )

    fun listenForCommands(languageCode: String = "mr-IN"): Flow<VoiceCommandResult> {
        return voiceInputService.startListening(languageCode).map { voiceResult ->
            when (voiceResult) {
                is VoiceResult.Success -> {
                    val text = voiceResult.text.lowercase()

                    // Check for wake word
                    val hasWakeWord = wakeWords.any { wakeWord ->
                        text.contains(wakeWord.lowercase())
                    }

                    if (hasWakeWord) {
                        // Parse command
                        val command = parseCommand(text)
                        VoiceCommandResult.CommandDetected(command, voiceResult.text)
                    } else {
                        VoiceCommandResult.NoWakeWord(voiceResult.text)
                    }
                }
                is VoiceResult.Partial -> VoiceCommandResult.Listening(voiceResult.text)
                is VoiceResult.Error -> VoiceCommandResult.Error(voiceResult.message)
                is VoiceResult.Listening -> VoiceCommandResult.Ready
                is VoiceResult.Speaking -> VoiceCommandResult.Listening("")
                is VoiceResult.RmsChanged -> VoiceCommandResult.VolumeChanged(voiceResult.value)
            }
        }
    }

    private fun parseCommand(text: String): VoiceCommand {
        commandPatterns.forEach { (pattern, command) ->
            if (pattern.containsMatchIn(text)) {
                return command
            }
        }
        return VoiceCommand.UnknownCommand(text)
    }

    fun extractParameters(text: String, command: VoiceCommand): Map<String, String> {
        return when (command) {
            is VoiceCommand.HowToCook -> {
                // Extract dish name
                val dishPattern = Regex("(कसं|कसे|how to)\\s+(.+?)\\s+बनवायचं")
                val match = dishPattern.find(text)
                mapOf("dish" to (match?.groupValues?.get(2) ?: ""))
            }
            is VoiceCommand.SetReminder -> {
                // Extract reminder text and time
                mapOf("text" to text) // TODO: Better time extraction
            }
            else -> emptyMap()
        }
    }
}

sealed class VoiceCommand {
    // Recipe commands
    object ShowRecipes : VoiceCommand()
    object HowToCook : VoiceCommand()

    // Travel commands
    object TravelInfo : VoiceCommand()
    object WhereToGo : VoiceCommand()

    // Fitness commands
    object ShowExercise : VoiceCommand()
    object FitnessPlan : VoiceCommand()

    // Calendar commands
    object ShowSchedule : VoiceCommand()
    object SetReminder : VoiceCommand()

    // General commands
    object ShowSummary : VoiceCommand()
    object ExportPDF : VoiceCommand()
    object NewChat : VoiceCommand()
    object ShowHelp : VoiceCommand()

    // Bot navigation
    data class OpenBot(val botType: BotType) : VoiceCommand()

    // Unknown
    data class UnknownCommand(val text: String) : VoiceCommand()
}

sealed class VoiceCommandResult {
    object Ready : VoiceCommandResult()
    data class Listening(val partial: String) : VoiceCommandResult()
    data class CommandDetected(val command: VoiceCommand, val fullText: String) : VoiceCommandResult()
    data class NoWakeWord(val text: String) : VoiceCommandResult()
    data class Error(val message: String) : VoiceCommandResult()
    data class VolumeChanged(val value: Float) : VoiceCommandResult()
}
