package com.sakhi.chat.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sakhi.chat.service.VoiceCommand

/**
 * Voice command listening indicator overlay
 */
@Composable
fun VoiceCommandListeningOverlay(
    isListening: Boolean,
    wakeWordDetected: Boolean,
    recognizedCommand: VoiceCommand?,
    partialText: String = "",
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = isListening,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Animated microphone icon
                    VoiceCommandAnimatedIcon(
                        isActive = wakeWordDetected,
                        isListening = isListening
                    )

                    // Status text
                    Text(
                        text = when {
                            recognizedCommand != null -> "आदेश ओळखला!"
                            wakeWordDetected -> "मी ऐकत आहे..."
                            else -> "\"सखी\" बोला..."
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (wakeWordDetected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    // Partial recognition text
                    if (partialText.isNotEmpty()) {
                        Text(
                            text = partialText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Recognized command display
                    recognizedCommand?.let { command ->
                        CommandRecognizedCard(command)
                    }

                    // Command suggestions
                    if (!wakeWordDetected && recognizedCommand == null) {
                        VoiceCommandSuggestions()
                    }

                    // Close button
                    TextButton(onClick = onDismiss) {
                        Text("बंद करा")
                    }
                }
            }
        }
    }
}

/**
 * Animated microphone icon for voice command
 */
@Composable
private fun VoiceCommandAnimatedIcon(
    isActive: Boolean,
    isListening: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isActive) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        Color.Transparent
                    )
                )
            )
            .scale(if (isListening) scale else 1f),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isActive) {
                Icons.Default.Mic
            } else {
                Icons.Default.MicNone
            },
            contentDescription = "Voice Command",
            modifier = Modifier.size(64.dp),
            tint = if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

/**
 * Display recognized command
 */
@Composable
private fun CommandRecognizedCard(command: VoiceCommand) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Column {
                Text(
                    text = getCommandDisplayName(command),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = getCommandDescription(command),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Voice command suggestions
 */
@Composable
private fun VoiceCommandSuggestions() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "सुचवलेले आदेश:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CommandSuggestionChip("सखी, रेसिपी सांग")
        CommandSuggestionChip("सखी, प्रवास माहिती")
        CommandSuggestionChip("सखी, नवीन चॅट सुरू कर")
        CommandSuggestionChip("सखी, सारांश द्या")
    }
}

/**
 * Command suggestion chip
 */
@Composable
private fun CommandSuggestionChip(text: String) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

/**
 * Floating action button for voice commands
 */
@Composable
fun VoiceCommandFAB(
    isListening: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = if (isListening) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }
    ) {
        Icon(
            imageVector = if (isListening) {
                Icons.Default.Stop
            } else {
                Icons.Default.Mic
            },
            contentDescription = if (isListening) {
                "आदेश ऐकणे थांबवा"
            } else {
                "आदेश ऐकणे सुरू करा"
            }
        )
    }
}

// Helper functions
private fun getCommandDisplayName(command: VoiceCommand): String {
    return when (command) {
        VoiceCommand.ShowRecipes -> "पाककृती दाखवा"
        VoiceCommand.TravelInfo -> "प्रवास माहिती"
        VoiceCommand.RelationshipAdvice -> "नातेसंबंध सल्ला"
        VoiceCommand.AstronomyInfo -> "खगोलशास्त्र माहिती"
        VoiceCommand.FitnessAdvice -> "फिटनेस सल्ला"
        VoiceCommand.LanguageHelp -> "भाषा मदत"
        VoiceCommand.CareerGuidance -> "करिअर मार्गदर्शन"
        VoiceCommand.HealthAdvice -> "आरोग्य सल्ला"
        VoiceCommand.NewChat -> "नवीन चॅट"
        VoiceCommand.Summarize -> "सारांश"
        VoiceCommand.Unknown -> "अज्ञात आदेश"
    }
}

private fun getCommandDescription(command: VoiceCommand): String {
    return when (command) {
        VoiceCommand.ShowRecipes -> "पाककृती बॉट उघडत आहे..."
        VoiceCommand.TravelInfo -> "प्रवास मार्गदर्शक उघडत आहे..."
        VoiceCommand.RelationshipAdvice -> "नातेसंबंध सल्लागार उघडत आहे..."
        VoiceCommand.AstronomyInfo -> "खगोलशास्त्र तज्ञ उघडत आहे..."
        VoiceCommand.FitnessAdvice -> "फिटनेस प्रशिक्षक उघडत आहे..."
        VoiceCommand.LanguageHelp -> "भाषा शिक्षक उघडत आहे..."
        VoiceCommand.CareerGuidance -> "करिअर मार्गदर्शक उघडत आहे..."
        VoiceCommand.HealthAdvice -> "आरोग्य सल्लागार उघडत आहे..."
        VoiceCommand.NewChat -> "नवीन संभाषण तयार करत आहे..."
        VoiceCommand.Summarize -> "संभाषण सारांश तयार करत आहे..."
        VoiceCommand.Unknown -> "आदेश समजला नाही"
    }
}
