package se.umu.cs.phbo0006.parkLens.view.helper.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import se.umu.cs.phbo0006.parkLens.controller.TextRecognition
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo
import java.util.concurrent.Executors

fun simpleCapture(
    imageCapture: ImageCapture,
    onPhotoCaptured: (ImageBitmap, List<BlockInfo>) -> Unit,
    onTextRecognized: (String) -> Unit,
    onComplete: () -> Unit
) {
    val executor = Executors.newSingleThreadExecutor()

    imageCapture.takePicture(
        executor,
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
                        executor.shutdown()
                    }

                } catch (e: Exception) {
                    Log.e("Camera", "Processing error", e)
                    onComplete()
                    executor.shutdown()
                } finally {
                    image.close()
                }
            }

            override fun onError(exc: ImageCaptureException) {
                Log.e("Camera", "Capture failed", exc)
                onComplete()
                executor.shutdown()
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