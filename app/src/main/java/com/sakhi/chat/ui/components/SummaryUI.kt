package com.sakhi.chat.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sakhi.chat.service.SummaryType

/**
 * Summary generation dialog
 */
@Composable
fun SummaryGenerationDialog(
    onGenerateSummary: (SummaryType) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Summarize,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "सारांश तयार करा",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "या संभाषणाचा सारांश तयार करण्यासाठी प्रकार निवडा:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Summary type options
                SummaryTypeCard(
                    summaryType = SummaryType.CONCISE,
                    title = "संक्षिप्त सारांश",
                    description = "3-5 वाक्यांमध्ये मुख्य मुद्दे",
                    icon = Icons.Default.ShortText,
                    onClick = { onGenerateSummary(SummaryType.CONCISE) }
                )

                SummaryTypeCard(
                    summaryType = SummaryType.DETAILED,
                    title = "तपशीलवार सारांश",
                    description = "संपूर्ण माहिती सह विस्तृत सारांश",
                    icon = Icons.Default.Subject,
                    onClick = { onGenerateSummary(SummaryType.DETAILED) }
                )

                SummaryTypeCard(
                    summaryType = SummaryType.BULLET_POINTS,
                    title = "बुलेट पॉईंट्स",
                    description = "मुख्य मुद्दे बुलेट फॉर्मॅटमध्ये",
                    icon = Icons.Default.List,
                    onClick = { onGenerateSummary(SummaryType.BULLET_POINTS) }
                )

                SummaryTypeCard(
                    summaryType = SummaryType.ACTION_ITEMS,
                    title = "कृती आयटम",
                    description = "करावयाच्या गोष्टींची यादी",
                    icon = Icons.Default.ChecklistRtl,
                    onClick = { onGenerateSummary(SummaryType.ACTION_ITEMS) }
                )

                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("रद्द करा")
                }
            }
        }
    }
}

/**
 * Summary type selection card
 */
@Composable
private fun SummaryTypeCard(
    summaryType: SummaryType,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Summary display dialog
 */
@Composable
fun SummaryDisplayDialog(
    summary: String,
    summaryType: SummaryType,
    isGenerating: Boolean,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getSummaryTypeTitle(summaryType),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "बंद करा"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Summary content
                if (isGenerating) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "सारांश तयार करत आहे...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = summary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onSave,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("जतन करा")
                        }
                        Button(
                            onClick = onShare,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("शेअर करा")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Summary button in chat
 */
@Composable
fun SummaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Icon(
            imageVector = Icons.Default.Summarize,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("सारांश तयार करा")
    }
}

// Helper function
private fun getSummaryTypeTitle(summaryType: SummaryType): String {
    return when (summaryType) {
        SummaryType.CONCISE -> "संक्षिप्त सारांश"
        SummaryType.DETAILED -> "तपशीलवार सारांश"
        SummaryType.BULLET_POINTS -> "बुलेट पॉईंट्स"
        SummaryType.ACTION_ITEMS -> "कृती आयटम"
    }
}
