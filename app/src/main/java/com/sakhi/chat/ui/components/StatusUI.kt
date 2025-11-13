package com.sakhi.chat.ui.components

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.sakhi.chat.model.StatusItem
import com.sakhi.chat.model.StatusType
import com.sakhi.chat.model.UserStatus

/**
 * Status ring indicator on profile
 */
@Composable
fun StatusRingIndicator(
    hasActiveStatus: Boolean,
    isViewed: Boolean,
    photoUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clickable(onClick = onClick)
    ) {
        // Ring indicator
        if (hasActiveStatus) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 3.dp,
                        brush = if (isViewed) {
                            Brush.linearGradient(listOf(Color.Gray, Color.LightGray))
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFE91E63),
                                    Color(0xFF9C27B0),
                                    Color(0xFF2196F3)
                                )
                            )
                        },
                        shape = CircleShape
                    )
            )
        }

        // Profile image
        AsyncImage(
            model = photoUrl.ifEmpty { R.drawable.ic_launcher_foreground },
            contentDescription = "Profile",
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )

        // Add status button
        if (!hasActiveStatus) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Status",
                    modifier = Modifier.padding(4.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

/**
 * Status list row
 */
@Composable
fun StatusListRow(
    statuses: List<UserStatus>,
    currentUserId: String,
    onStatusClick: (UserStatus) -> Unit,
    onAddStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // My status (always first)
        item {
            val myStatus = statuses.find { it.userId == currentUserId }
            StatusCircle(
                userName = "तुमची स्टोरी",
                photoUrl = "",
                hasActiveStatus = myStatus != null,
                isViewed = false,
                onClick = {
                    if (myStatus != null) {
                        onStatusClick(myStatus)
                    } else {
                        onAddStatusClick()
                    }
                }
            )
        }

        // Other users' statuses
        items(statuses.filter { it.userId != currentUserId }) { status ->
            val isViewed = status.viewedBy.contains(currentUserId)
            StatusCircle(
                userName = status.userName,
                photoUrl = status.userPhotoUrl,
                hasActiveStatus = true,
                isViewed = isViewed,
                onClick = { onStatusClick(status) }
            )
        }
    }
}

/**
 * Status circle item
 */
@Composable
private fun StatusCircle(
    userName: String,
    photoUrl: String,
    hasActiveStatus: Boolean,
    isViewed: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        StatusRingIndicator(
            hasActiveStatus = hasActiveStatus,
            isViewed = isViewed,
            photoUrl = photoUrl,
            onClick = onClick
        )
        Text(
            text = userName,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

/**
 * Status creation screen
 */
@Composable
fun StatusCreationScreen(
    onCreateTextStatus: (String, String, String) -> Unit,
    onCreateImageStatus: (Uri) -> Unit,
    onCreateVideoStatus: (Uri) -> Unit,
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
                Text(
                    text = "नवीन स्टेटस तयार करा",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Status type options
                StatusTypeOption(
                    icon = Icons.Default.TextFields,
                    title = "मजकूर स्टेटस",
                    description = "मजकूर सह स्टेटस तयार करा",
                    onClick = { /* Show text status dialog */ }
                )

                StatusTypeOption(
                    icon = Icons.Default.Image,
                    title = "फोटो स्टेटस",
                    description = "फोटो शेअर करा",
                    onClick = { /* Open image picker */ }
                )

                StatusTypeOption(
                    icon = Icons.Default.Videocam,
                    title = "व्हिडिओ स्टेटस",
                    description = "व्हिडिओ शेअर करा",
                    onClick = { /* Open video picker */ }
                )

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

@Composable
private fun StatusTypeOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
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
                contentDescription = null
            )
        }
    }
}

/**
 * Status viewer (Instagram/WhatsApp style)
 */
@Composable
fun StatusViewer(
    userStatus: UserStatus,
    currentUserId: String,
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableStateOf(0) }
    val currentItem = userStatus.items.getOrNull(currentIndex)

    // Auto-progress timer
    LaunchedEffect(currentIndex) {
        currentItem?.let { item ->
            kotlinx.coroutines.delay(item.duration)
            if (currentIndex < userStatus.items.size - 1) {
                currentIndex++
            } else {
                onComplete()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Status content
        currentItem?.let { item ->
            when (item.type) {
                StatusType.TEXT -> {
                    TextStatusContent(item)
                }
                StatusType.IMAGE -> {
                    ImageStatusContent(item)
                }
                StatusType.VIDEO -> {
                    VideoStatusContent(item)
                }
            }
        }

        // Progress indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            userStatus.items.forEachIndexed { index, _ ->
                LinearProgressIndicator(
                    progress = when {
                        index < currentIndex -> 1f
                        index == currentIndex -> 0.5f // Animated progress would be better
                        else -> 0f
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 32.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = userStatus.userPhotoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column {
                    Text(
                        text = userStatus.userName,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatStatusTime(currentItem?.timestamp ?: 0),
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }

        // Navigation areas (left/right tap)
        Row(modifier = Modifier.fillMaxSize()) {
            // Previous
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        if (currentIndex > 0) {
                            currentIndex--
                        }
                    }
            )
            // Next
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        if (currentIndex < userStatus.items.size - 1) {
                            currentIndex++
                        } else {
                            onComplete()
                        }
                    }
            )
        }

        // Viewed by list (only for own status)
        if (userStatus.userId == currentUserId && userStatus.viewedBy.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${userStatus.viewedBy.size} लोकांनी पाहिले",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun TextStatusContent(item: StatusItem) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(android.graphics.Color.parseColor(item.backgroundColor))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.content,
            color = Color(android.graphics.Color.parseColor(item.textColor)),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Composable
private fun ImageStatusContent(item: StatusItem) {
    AsyncImage(
        model = item.content,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun VideoStatusContent(item: StatusItem) {
    // Video player would go here
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Video Player",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

// Helper function
private fun formatStatusTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val hours = diff / (1000 * 60 * 60)
    return when {
        hours < 1 -> "आत्ताच"
        hours < 24 -> "$hours तासांपूर्वी"
        else -> "काल"
    }
}
