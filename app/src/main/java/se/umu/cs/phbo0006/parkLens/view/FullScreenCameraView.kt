package se.umu.cs.phbo0006.parkLens.view

import android.view.Surface
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import se.umu.cs.phbo0006.parkLens.view.ui.theme.BlueActive

@Composable
fun FullScreenCameraView(
    imageCapture: ImageCapture? = null,
    onCaptureClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val preview = Preview.Builder()
                    .setTargetRotation(Surface.ROTATION_0)
                    .build().apply {
                        surfaceProvider = previewView.surfaceProvider
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                val cameraProvider = cameraProviderFuture.get()

                try {
                    cameraProvider.unbindAll()
                    if (imageCapture != null) {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } else {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                previewView
            }
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {

            Box( // Clickable area
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onCaptureClick)
            ) {

                Box( // Outer ring
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            clip = true
                        )
                        .border(
                            width = 4.dp,
                            color = BlueActive.copy(alpha = 0.8f),
                            shape = CircleShape
                        )
                        .background(Color.Transparent)
                )


                Box( // Inner button
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(BlueActive)
                )
            }
        }
    }
}