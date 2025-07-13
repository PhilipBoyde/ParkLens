package se.umu.cs.phbo0006.parkLens.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import se.umu.cs.phbo0006.parkLens.controller.TextRecognition
import se.umu.cs.phbo0006.parkLens.view.ui.theme.TextColor
import java.util.concurrent.Executors

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue




@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var capturedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var recognizedText by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    if (capturedBitmap != null) {
        ImagePreviewScreen(
            bitmap = capturedBitmap!!,
            recognizedText = recognizedText,
            onBackToCamera = {
                capturedBitmap = null
                recognizedText = null
            }
        )
    } else if (hasPermission) {
        FullScreenCameraView(
            imageCapture = imageCapture,
            onCaptureClick = {
                simpleCapture(
                    imageCapture = imageCapture,
                    onPhotoCaptured = { bitmap ->
                        capturedBitmap = bitmap
                    },
                    onTextRecognized = { text ->
                        recognizedText = text
                    }
                )
            }
        )
    } else {
        PermissionDeniedScreen()
    }
}

private fun simpleCapture(
    imageCapture: ImageCapture,
    onPhotoCaptured: (ImageBitmap) -> Unit,
    onTextRecognized: (String) -> Unit
) {
    imageCapture.takePicture(
        Executors.newSingleThreadExecutor(),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {

                try {
                    val bitmap = image.toBitmap()
                    val rotatedBitmap = if (bitmap.width > bitmap.height) {
                        rotateBitmap(bitmap)
                    } else {
                        bitmap
                    }

                    val imageBitmap = rotatedBitmap.asImageBitmap()
                    onPhotoCaptured(imageBitmap)

                    TextRecognition.recognizeTextFromImage(image) { text ->
                        onTextRecognized(text)
                    }
                } finally {
                    image.close()
                }
            }

            override fun onError(exc: ImageCaptureException) {
                Log.e("Camera", "Capture failed", exc)
            }
        }
    )
}


private fun rotateBitmap(source: Bitmap, angle: Float = 90f): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}



@Composable
fun ImagePreviewScreen(
    bitmap: ImageBitmap,
    recognizedText: String?,
    onBackToCamera: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = "Captured image",
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentScale = ContentScale.Fit
        )

        recognizedText?.let {
            Text(
                text = it,
                color = TextColor,
                modifier = Modifier
                    .padding(16.dp))
        }

        Button(
            onClick = onBackToCamera,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Back to Camera")
        }
    }
}

@Composable
fun PermissionDeniedScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text("Camera permission is required", color = Color.White)
    }
}
