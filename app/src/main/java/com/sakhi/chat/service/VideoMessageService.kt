package com.sakhi.chat.service

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.video.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoMessageService(private val context: Context) {

    private var recording: Recording? = null
    private var videoCapture: VideoCapture<Recorder>? = null

    fun setupCamera(lifecycleOwner: LifecycleOwner, onReady: () -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    videoCapture
                )
                onReady()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun startRecording(
        maxDurationSeconds: Int = 60,
        onStart: () -> Unit,
        onComplete: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        val videoCapture = videoCapture ?: return

        val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            .format(System.currentTimeMillis())

        val outputFile = File(
            context.getExternalFilesDir(null),
            "sakhi_video_$name.mp4"
        )

        val outputOptions = FileOutputOptions.Builder(outputFile).build()

        recording = videoCapture.output
            .prepareRecording(context, outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(context)) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> {
                        onStart()
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (event.hasError()) {
                            onError("Video recording error: ${event.error}")
                        } else {
                            onComplete(Uri.fromFile(outputFile))
                        }
                    }
                }
            }

        // Auto-stop after max duration
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            stopRecording()
        }, (maxDurationSeconds * 1000).toLong())
    }

    fun stopRecording() {
        recording?.stop()
        recording = null
    }

    fun isRecording(): Boolean = recording != null
}

data class VideoMessage(
    val id: Long = System.currentTimeMillis(),
    val videoUrl: String,
    val thumbnailUrl: String? = null,
    val duration: Long, // in seconds
    val senderId: String,
    val senderName: String,
    val timestamp: Long = System.currentTimeMillis()
)
