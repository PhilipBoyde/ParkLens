package se.umu.cs.phbo0006.parkLens.view.camera

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import se.umu.cs.phbo0006.parkLens.controller.checkIfAllowedToPark
import se.umu.cs.phbo0006.parkLens.model.signs.SignType
import se.umu.cs.phbo0006.parkLens.view.CameraPermissionDeniedScreen
import se.umu.cs.phbo0006.parkLens.view.ParkingRulesScreen
import se.umu.cs.phbo0006.parkLens.view.ColorBlocksScreen
import se.umu.cs.phbo0006.parkLens.view.LoadingScreen

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

    var debugMode by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var blockInfos by remember { mutableStateOf<List<BlockInfo>>(emptyList()) }
    var recognizedText by remember { mutableStateOf<String?>(null) }
    var currentLanguage by remember { mutableStateOf("English") }

    var showParkingRules by remember { mutableStateOf(false) }

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
    } else if (showParkingRules){

        ParkingRulesScreen(
            checkIfAllowedToPark(blockInfos),
            //TODO -back button -notify button
        )

    } else if (capturedBitmap != null) {

        if (!debugMode){
            ColorBlocksScreen(
                blockInfos,
                onTakeNewPhoto = {
                    capturedBitmap = null
                    blockInfos = emptyList()
                    showParkingRules = false
                },
                onContinue = {
                    showParkingRules = true
                }
            )


        }else {
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
        }

    } else if (hasPermission) {
        FullScreenCameraView(
            imageCapture = imageCapture,
            debugMode = debugMode,
            onDebugModeChange = { debugMode = it },
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
            },
            selectedLanguage = currentLanguage,
            onLanguageSelected = { newLang ->
                currentLanguage = newLang
                // TODO: language change logic
            }
        )
    } else {
        CameraPermissionDeniedScreen()
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
                    val rotationDegrees = image.imageInfo.rotationDegrees.toFloat()

                    val rotatedBitmap = if (rotationDegrees != 0f) {
                        rotateBitmap(bitmap, rotationDegrees)
                    } else {
                        bitmap
                    }

                    val imageBitmap = rotatedBitmap.asImageBitmap()
                    TextRecognition.recognizeTextFromImage(bitmap) { blockInfos, text ->
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

private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix().apply {
        postRotate(degrees)
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
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
                .fillMaxWidth(0.25f),
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

