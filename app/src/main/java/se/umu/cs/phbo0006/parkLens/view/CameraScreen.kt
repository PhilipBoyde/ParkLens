package se.umu.cs.phbo0006.parkLens.view

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo
import se.umu.cs.phbo0006.parkLens.view.ui.theme.TextColor
import java.util.concurrent.Executors
import android.view.Surface
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import se.umu.cs.phbo0006.parkLens.model.signs.SignType

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

    var isProcessing by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var blockInfos by remember { mutableStateOf<List<BlockInfo>>(emptyList()) }
    var recognizedText by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
    }
    if (isProcessing){
        LoadingScreen()
    }else if (capturedBitmap != null) {
        ImagePreviewScreen(
            bitmap = capturedBitmap!!,
            blockInfos = blockInfos,
            recognizedText = recognizedText,
            onBackToCamera = {
                capturedBitmap = null
                blockInfos = emptyList()
                recognizedText = null
            }
        )

    } else if (hasPermission) {
        FullScreenCameraView(
            imageCapture = imageCapture,
            onCaptureClick = {
                isProcessing = true
                simpleCapture(
                    imageCapture = imageCapture,
                    onPhotoCaptured = { bitmap, infos ->
                        capturedBitmap = bitmap
                        blockInfos = infos
                    },
                    onTextRecognized = { text ->
                        recognizedText = text
                    },
                    onComplete = { isProcessing = false }
                )
            }
        )
    } else {
        PermissionDeniedScreen()
    }
}

private fun simpleCapture(
    imageCapture: ImageCapture,
    onPhotoCaptured: (ImageBitmap, List<BlockInfo>) -> Unit,
    onTextRecognized: (String) -> Unit,
    onComplete: () -> Unit
) {
    imageCapture.takePicture(
        Executors.newSingleThreadExecutor(),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    val bitmap = image.toBitmap()
                    val imageBitmap = bitmap.asImageBitmap()
                    TextRecognition.recognizeTextFromImage(image) { blockInfos, text ->
                        onPhotoCaptured(imageBitmap, blockInfos)
                        onTextRecognized(text)
                        onComplete()
                    }
                } catch (e: Exception) {
                    Log.e("Camera", "Processing error", e)
                    onComplete()
                } finally {
                    image.close()
                }
            }

            override fun onError(exc: ImageCaptureException) {
                Log.e("Camera", "Capture failed", exc)
                onComplete()
            }
        }
    )
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .pointerInput(Unit) { detectTapGestures {} },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun ImagePreviewScreen(
    bitmap: ImageBitmap,
    blockInfos: List<BlockInfo>,
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
                .fillMaxWidth()
                .weight(0.5f),
            contentScale = ContentScale.Fit
        )


        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(16.dp)
        ) {
            items(blockInfos.size) { index ->
                val block = blockInfos[index]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    block.croppedImage?.let { cropped ->
                        Image(
                            bitmap = cropped,
                            contentDescription = "Cropped block ${index + 1}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Text(
                        text = "Block ${index + 1}: ${block.text}",
                        color = TextColor,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Color: ${block.color}",
                        color = when (block.color) {
                            SignType.BLUE -> Color.Blue
                            SignType.YELLOW  -> Color.Yellow
                            else -> TextColor
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }


        /*
        recognizedText?.let {
            Text(
                text = it,
                color = TextColor,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
         */

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