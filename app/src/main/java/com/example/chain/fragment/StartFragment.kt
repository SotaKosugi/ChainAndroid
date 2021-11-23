package com.example.chain.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraMetadata
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.chain.R
import com.github.mikephil.charting.utils.Utils
import com.google.common.util.concurrent.ListenableFuture

import kotlinx.android.synthetic.main.fragment_start.*
//import org.opencv.android.OpenCVLoader
//import org.opencv.android.Utils
//import org.opencv.core.Mat
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

typealias CornersListener = () -> Unit

class StartFragment : Fragment() {

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null


    private lateinit var safeContext: Context

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    val qualitySelector = QualitySelector
        .firstTry(QualitySelector.QUALITY_UHD)
        .thenTry(QualitySelector.QUALITY_FHD)
        .thenTry(QualitySelector.QUALITY_HD)
        .finallyTry(QualitySelector.QUALITY_SD,
            QualitySelector.FALLBACK_STRATEGY_LOWER)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    private fun getStatusBarHeight(): Int {
        val resourceId = safeContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            safeContext.resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Setup the listener for take photo button
        camera_capture_button.setOnClickListener { takePhoto() }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
//        cameraExecutor = Executors.newCachedThreadPool()
    }

    private fun startCamera() {
        //OpenCVLoader.initDebug()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)




        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider = cameraProviderFuture.get()

            val cameraInfo = cameraProvider.getAvailableCameraInfos()



            // Preview
            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val qualitySelector = QualitySelector
                .firstTry(QualitySelector.QUALITY_SD)
                .thenTry(QualitySelector.QUALITY_SD)
                .thenTry(QualitySelector.QUALITY_SD)
                .finallyTry(QualitySelector.QUALITY_SD,
                    QualitySelector.FALLBACK_STRATEGY_LOWER)

            val recorder = Recorder.Builder()
                .setExecutor(cameraExecutor).setQualitySelector(qualitySelector)
                .build()
            val videoCapture = VideoCapture.withOutput(recorder)





            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, videoCapture)

                // Create MediaStoreOutputOptions for our recorder
                val name = "CameraX-recording-" +
                        SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                            .format(System.currentTimeMillis()) + ".mp4"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, name)
                }
                val mediaStoreOutput = MediaStoreOutputOptions.Builder(safeContext.contentResolver,
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    .setContentValues(contentValues)
                    .build()

                // 2. Configure Recorder and Start recording to the mediaStoreOutput.
                val activeRecording = if (ActivityCompat.checkSelfPermission(
                        safeContext,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    videoCapture.output.prepareRecording(safeContext, mediaStoreOutput)
                        .start()
                }else{
                    //ここ適当に入れたけどたぶん怪しい
                    videoCapture.output.prepareRecording(safeContext, mediaStoreOutput)
                        .start()
                }





            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }





        }, ContextCompat.getMainExecutor(safeContext))




    }

    private fun takePhoto() {

        Log.d("TAG", "いまここ！！")
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(safeContext), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                val msg = "Photo capture succeeded: $savedUri"
                Toast.makeText(safeContext, msg, Toast.LENGTH_SHORT).show()
                Log.d(TAG, msg)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        isOffline = true
    }

    override fun onResume() {
        super.onResume()
        isOffline = false
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(safeContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(safeContext, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
//                finish()
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    //ここあやしい
    fun getOutputDirectory(): File {
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else activity?.filesDir!!
    }


    companion object {
        val TAG = "CameraXFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        internal const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        var isOffline = false // prevent app crash when goes offline
    }

    private class CornerAnalyzer(private val listener: CornersListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        @SuppressLint("UnsafeExperimentalUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            if (!isOffline) {
                listener()
            }
            imageProxy.close() // important! if it is not closed it will only run once
        }

    }
}