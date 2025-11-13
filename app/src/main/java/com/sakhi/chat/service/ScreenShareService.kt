package com.sakhi.chat.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.RequiresApi
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ScreenShareService(private val context: Context) {

    companion object {
        const val REQUEST_CODE_SCREEN_CAPTURE = 1001
        const val VIRTUAL_DISPLAY_NAME = "SakhiScreenShare"
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var mediaRecorder: MediaRecorder? = null
    private var screenDensity: Int = 0
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var isRecording = false

    private val mediaProjectionManager: MediaProjectionManager by lazy {
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    init {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            screenWidth = bounds.width()
            screenHeight = bounds.height()
            screenDensity = context.resources.displayMetrics.densityDpi
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(metrics)
            screenWidth = metrics.widthPixels
            screenHeight = metrics.heightPixels
            screenDensity = metrics.densityDpi
        }
    }

    /**
     * Get intent to request screen capture permission
     */
    fun getScreenCaptureIntent(): Intent {
        return mediaProjectionManager.createScreenCaptureIntent()
    }

    /**
     * Start screen sharing after permission is granted
     */
    fun startScreenShare(
        resultCode: Int,
        data: Intent?,
        onStart: () -> Unit,
        onError: (String) -> Unit
    ): File? {
        if (resultCode != Activity.RESULT_OK || data == null) {
            onError("स्क्रीन शेअरिंग परवानगी नाकारली")
            return null
        }

        try {
            // Create output file
            val outputFile = createOutputFile()

            // Setup MediaRecorder
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(outputFile.absolutePath)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setVideoSize(screenWidth, screenHeight)
                setVideoFrameRate(30)
                setVideoEncodingBitRate(5 * 1024 * 1024) // 5 Mbps

                try {
                    prepare()
                } catch (e: Exception) {
                    onError("मीडिया रेकॉर्डर तयार करता आला नाही: ${e.message}")
                    return null
                }
            }

            // Start MediaProjection
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)

            // Create VirtualDisplay
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                VIRTUAL_DISPLAY_NAME,
                screenWidth,
                screenHeight,
                screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder?.surface,
                null,
                null
            )

            // Start recording
            mediaRecorder?.start()
            isRecording = true
            onStart()

            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            onError("स्क्रीन रेकॉर्डिंग सुरू करता आले नाही: ${e.message}")
            stopScreenShare()
            return null
        }
    }

    /**
     * Stop screen sharing
     */
    fun stopScreenShare(): File? {
        if (!isRecording) return null

        return try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            isRecording = false

            virtualDisplay?.release()
            mediaProjection?.stop()

            virtualDisplay = null
            mediaProjection = null

            // Return the file if recording was successful
            null // File reference would be stored separately
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            cleanup()
        }
    }

    /**
     * Check if currently sharing screen
     */
    fun isSharing(): Boolean = isRecording

    /**
     * Pause screen sharing (Android 7.0+)
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun pauseScreenShare() {
        if (isRecording && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                mediaRecorder?.pause()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Resume screen sharing (Android 7.0+)
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun resumeScreenShare() {
        if (isRecording && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                mediaRecorder?.resume()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createOutputFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            .format(System.currentTimeMillis())
        val fileName = "sakhi_screen_share_$timestamp.mp4"
        val outputDir = context.getExternalFilesDir(null)
        return File(outputDir, fileName)
    }

    private fun cleanup() {
        try {
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Screen share session data
 */
data class ScreenShareSession(
    val id: String = UUID.randomUUID().toString(),
    val groupId: String,
    val sharerId: String,
    val sharerName: String,
    val videoUrl: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val isActive: Boolean = true,
    val viewers: List<String> = emptyList()
)

/**
 * Screen share event for real-time updates
 */
sealed class ScreenShareEvent {
    data class Started(val session: ScreenShareSession) : ScreenShareEvent()
    data class Stopped(val sessionId: String) : ScreenShareEvent()
    data class ViewerJoined(val sessionId: String, val viewerId: String) : ScreenShareEvent()
    data class ViewerLeft(val sessionId: String, val viewerId: String) : ScreenShareEvent()
    data class Error(val message: String) : ScreenShareEvent()
}

/**
 * Manager for handling multiple screen share sessions in a group
 */
class ScreenShareManager {
    private val activeSessions = mutableMapOf<String, ScreenShareSession>()

    fun startSession(session: ScreenShareSession) {
        activeSessions[session.id] = session
    }

    fun stopSession(sessionId: String): ScreenShareSession? {
        val session = activeSessions[sessionId]
        activeSessions.remove(sessionId)
        return session?.copy(
            isActive = false,
            endTime = System.currentTimeMillis()
        )
    }

    fun addViewer(sessionId: String, viewerId: String): ScreenShareSession? {
        val session = activeSessions[sessionId] ?: return null
        val updatedSession = session.copy(
            viewers = session.viewers + viewerId
        )
        activeSessions[sessionId] = updatedSession
        return updatedSession
    }

    fun removeViewer(sessionId: String, viewerId: String): ScreenShareSession? {
        val session = activeSessions[sessionId] ?: return null
        val updatedSession = session.copy(
            viewers = session.viewers - viewerId
        )
        activeSessions[sessionId] = updatedSession
        return updatedSession
    }

    fun getActiveSession(groupId: String): ScreenShareSession? {
        return activeSessions.values.find { it.groupId == groupId && it.isActive }
    }

    fun getAllActiveSessions(): List<ScreenShareSession> {
        return activeSessions.values.filter { it.isActive }
    }
}
