package com.sakhi.chat.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.sakhi.chat.service.CalendarEvent
import com.sakhi.chat.service.Reminder
import com.sakhi.chat.service.ReminderType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Today's events card in chat
 */
@Composable
fun TodaysEventsCard(
    events: List<CalendarEvent>,
    onViewAllClick: () -> Unit,
    onAddEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "आजचे इव्हेंट",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onAddEventClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "इव्हेंट जोडा"
                    )
                }
            }

            // Events list
            if (events.isEmpty()) {
                Text(
                    text = "आज कोणतेही इव्हेंट नाहीत",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                events.take(3).forEach { event ->
                    CalendarEventItem(event)
                }

                if (events.size > 3) {
                    TextButton(onClick = onViewAllClick) {
                        Text("सर्व पहा (${events.size})")
                    }
                }
            }
        }
    }
}

/**
 * Calendar event item
 */
@Composable
private fun CalendarEventItem(event: CalendarEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Time indicator
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTime(event.startTime),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Event details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                if (event.location.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = event.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Event creation dialog
 */
@Composable
fun EventCreationDialog(
    onCreateEvent: (CalendarEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedTime by remember { mutableStateOf("10:00") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "नवीन इव्हेंट तयार करा",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("शीर्षक") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("तपशील") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("स्थान") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, null)
                    }
                )

                // Date and time pickers would go here
                // For simplicity, showing text fields
                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = { selectedTime = it },
                    label = { Text("वेळ (HH:MM)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, null)
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("रद्द करा")
                    }
                    Button(
                        onClick = {
                            val event = CalendarEvent(
                                title = title,
                                description = description,
                                location = location,
                                startTime = selectedDate,
                                endTime = selectedDate + (60 * 60 * 1000)
                            )
                            onCreateEvent(event)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = title.isNotEmpty()
                    ) {
                        Text("तयार करा")
                    }
                }
            }
        }
    }
}

/**
 * Reminder creation dialog
 */
@Composable
fun ReminderCreationDialog(
    onCreateReminder: (Reminder) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ReminderType.ONE_TIME) }
    var hours by remember { mutableStateOf("1") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "नवीन आठवण तयार करा",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("शीर्षक") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("संदेश") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                // Reminder type selector
                Text(
                    text = "प्रकार:",
                    style = MaterialTheme.typography.labelLarge
                )

                ReminderType.values().forEach { type ->
                    ReminderTypeChip(
                        type = type,
                        isSelected = type == selectedType,
                        onClick = { selectedType = type }
                    )
                }

                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text("तासांमध्ये वेळ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("रद्द करा")
                    }
                    Button(
                        onClick = {
                            val hoursInt = hours.toIntOrNull() ?: 1
                            val reminder = Reminder(
                                title = title,
                                message = message,
                                type = selectedType,
                                time = System.currentTimeMillis() + (hoursInt * 60 * 60 * 1000)
                            )
                            onCreateReminder(reminder)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = title.isNotEmpty()
                    ) {
                        Text("तयार करा")
                    }
                }
            }
        }
    }
}

/**
 * Reminder type chip
 */
@Composable
private fun ReminderTypeChip(
    type: ReminderType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = when (type) {
                    ReminderType.ONE_TIME -> "एकदा"
                    ReminderType.RECURRING_DAILY -> "दररोज"
                    ReminderType.RECURRING_WEEKLY -> "साप्ताहिक"
                    ReminderType.RECURRING_HOURLY -> "दर तासाला"
                }
            )
        },
        leadingIcon = if (isSelected) {
            { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
        } else null
    )
}

/**
 * Active reminders list in settings
 */
@Composable
fun ActiveRemindersList(
    reminders: List<Reminder>,
    onCancelReminder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "सक्रिय आठवणी",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (reminders.isEmpty()) {
                Text(
                    text = "कोणत्याही सक्रिय आठवणी नाहीत",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                reminders.forEach { reminder ->
                    ReminderItem(
                        reminder = reminder,
                        onCancel = { onCancelReminder(reminder.id) }
                    )
                }
            }
        }
    }
}

/**
 * Reminder item
 */
@Composable
private fun ReminderItem(
    reminder: Reminder,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatReminderTime(reminder.time),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "रद्द करा",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Quick reminder templates
 */
@Composable
fun QuickReminderTemplates(
    onTemplateSelected: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text =="झटपट आठवणी:",
            style = MaterialTheme.typography.labelLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickReminderChip("15 मिनिटे", 15) { title, mins ->
                onTemplateSelected(title, mins)
            }
            QuickReminderChip("1 तास", 60) { title, mins ->
                onTemplateSelected(title, mins)
            }
            QuickReminderChip("2 तास", 120) { title, mins ->
                onTemplateSelected(title, mins)
            }
        }
    }
}

@Composable
private fun QuickReminderChip(
    label: String,
    minutes: Int,
    onSelect: (String, Int) -> Unit
) {
    AssistChip(
        onClick = { onSelect(label, minutes) },
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

// Helper functions
private fun formatTime(timeMillis: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timeMillis))
}

private fun formatReminderTime(timeMillis: Long): String {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("mr", "IN"))
    return sdf.format(Date(timeMillis))
}
