package com.sakhi.chat.ui.components

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.sakhi.chat.service.VideoMessage

/**
 * Video recording screen
 */
@Composable
fun VideoRecordingScreen(
    isRecording: Boolean,
    recordingSeconds: Int,
    maxDurationSeconds: Int = 60,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onSwitchCamera: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera preview would be rendered here using CameraX
        // This is a placeholder as actual camera preview requires AndroidView with PreviewView
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Camera Preview",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Top controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "बंद करा",
                    tint = Color.White
                )
            }

            // Recording timer
            if (isRecording) {
                RecordingTimer(
                    seconds = recordingSeconds,
                    maxSeconds = maxDurationSeconds
                )
            }

            // Switch camera button
            IconButton(
                onClick = onSwitchCamera,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "कॅमेरा बदला",
                    tint = Color.White
                )
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Duration limit indicator
            if (!isRecording) {
                Text(
                    text = "60 सेकंद पर्यंत रेकॉर्ड करा",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Record button
            VideoRecordButton(
                isRecording = isRecording,
                onStartRecording = onStartRecording,
                onStopRecording = onStopRecording
            )
        }
    }
}

/**
 * Video record button
 */
@Composable
private fun VideoRecordButton(
    isRecording: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.3f))
            .clickable {
                if (isRecording) {
                    onStopRecording()
                } else {
                    onStartRecording()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (isRecording) 32.dp else 64.dp)
                .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                .background(Color.Red)
        )
    }
}

/**
 * Recording timer
 */
@Composable
private fun RecordingTimer(
    seconds: Int,
    maxSeconds: Int
) {
    val progress = seconds.toFloat() / maxSeconds.toFloat()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recording dot animation
            RecordingDot()

            Text(
                text = formatTime(seconds),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "/ ${formatTime(maxSeconds)}",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Animated recording dot
 */
@Composable
private fun RecordingDot() {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(500)
            visible = !visible
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

/**
 * Video player for chat messages
 */
@Composable
fun VideoMessagePlayer(
    videoMessage: VideoMessage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isPlaying by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoMessage.videoUrl))
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
    ) {
        // ExoPlayer view
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    controllerAutoShow = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Play button overlay
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable {
                        exoPlayer.play()
                        isPlaying = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    modifier = Modifier.size(64.dp),
                    tint = Color.White
                )
            }
        }

        // Duration badge
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = formatTime((videoMessage.duration / 1000).toInt()),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Video message bubble in chat
 */
@Composable
fun VideoMessageBubble(
    videoMessage: VideoMessage,
    isFromUser: Boolean,
    modifier: Modifier = Modifier
) {
    var showPlayer by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .width(280.dp)
            .clickable { showPlayer = true },
        colors = CardDefaults.cardColors(
            containerColor = if (isFromUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isFromUser) 16.dp else 4.dp,
            bottomEnd = if (isFromUser) 4.dp else 16.dp
        )
    ) {
        Box {
            // Thumbnail placeholder (in real app, show actual thumbnail)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "व्हिडिओ चालवा",
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }

            // Duration badge
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = formatTime((videoMessage.duration / 1000).toInt()),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }

    // Full screen video player dialog
    if (showPlayer) {
        Dialog(onDismissRequest = { showPlayer = false }) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                VideoMessagePlayer(
                    videoMessage = videoMessage,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Video message FAB
 */
@Composable
fun VideoMessageFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Icon(
            imageVector = Icons.Default.Videocam,
            contentDescription = "व्हिडिओ मेसेज पाठवा"
        )
    }
}

// Helper function
private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
