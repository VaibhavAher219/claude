package com.sakhi.chat.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sakhi.chat.service.ScreenShareSession

/**
 * Start/Stop screen sharing button in group chat
 */
@Composable
fun ScreenShareButton(
    isSharing: Boolean,
    onStartSharing: () -> Unit,
    onStopSharing: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = if (isSharing) onStopSharing else onStartSharing,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSharing) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    ) {
        Icon(
            imageVector = if (isSharing) {
                Icons.Default.StopScreenShare
            } else {
                Icons.Default.ScreenShare
            },
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isSharing) {
                "शेअरिंग थांबवा"
            } else {
                "स्क्रीन शेअर करा"
            }
        )
    }
}

/**
 * Screen sharing indicator overlay
 */
@Composable
fun ScreenSharingIndicator(
    sharerName: String,
    viewerCount: Int,
    isOwnShare: Boolean,
    onStopSharing: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Animated recording dot
                ScreenShareAnimatedDot()

                Column {
                    Text(
                        text = if (isOwnShare) {
                            "तुमचा स्क्रीन शेअर होत आहे"
                        } else {
                            "$sharerName स्क्रीन शेअर करत आहे"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "$viewerCount दर्शक",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            if (isOwnShare) {
                IconButton(onClick = onStopSharing) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "थांबवा",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

/**
 * Animated dot for screen share indicator
 */
@Composable
private fun ScreenShareAnimatedDot() {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(800)
            visible = !visible
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error)
        )
    }
}

/**
 * Viewer list display
 */
@Composable
fun ScreenShareViewerList(
    viewers: List<String>,
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
                    text = "दर्शक (${ viewers.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewers) { viewerId ->
                        ViewerItem(viewerId)
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("बंद करा")
                }
            }
        }
    }
}

@Composable
private fun ViewerItem(viewerId: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = viewerId,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Full-screen screen share viewer
 */
@Composable
fun FullScreenShareViewer(
    session: ScreenShareSession,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video player for screen recording would go here
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "स्क्रीन शेअर व्ह्यू",
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
            Card(
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
                        imageVector = Icons.Default.ScreenShare,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = session.sharerName,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.7f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "बंद करा",
                    tint = Color.White
                )
            }
        }

        // Viewer count badge
        Card(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${session.viewers.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Screen share permission request dialog
 */
@Composable
fun ScreenSharePermissionDialog(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.ScreenShare,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "स्क्रीन शेअर परवानगी",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("तुमचा स्क्रीन शेअर करण्यासाठी परवानगी आवश्यक आहे.")
                Text(
                    text = "• तुमचा संपूर्ण स्क्रीन रेकॉर्ड होईल\n• ग्रुप मेंबर्स तुमचा स्क्रीन पाहू शकतील\n• तुम्ही कधीही थांबवू शकता",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("परवानगी द्या")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करा")
            }
        }
    )
}

/**
 * Screen share notification card in group chat
 */
@Composable
fun ScreenShareNotificationCard(
    session: ScreenShareSession,
    onJoinViewing: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ScreenShare,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${session.sharerName} स्क्रीन शेअर करत आहे",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "${session.viewers.size} दर्शक पाहत आहेत",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Button(
                onClick = onJoinViewing,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("पाहायला सुरू करा")
            }
        }
    }
}

/**
 * FAB for screen sharing
 */
@Composable
fun ScreenShareFAB(
    isSharing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = if (isSharing) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.tertiaryContainer
        }
    ) {
        Icon(
            imageVector = if (isSharing) {
                Icons.Default.StopScreenShare
            } else {
                Icons.Default.ScreenShare
            },
            contentDescription = if (isSharing) {
                "स्क्रीन शेअरिंग थांबवा"
            } else {
                "स्क्रीन शेअर करा"
            }
        )
    }
}
