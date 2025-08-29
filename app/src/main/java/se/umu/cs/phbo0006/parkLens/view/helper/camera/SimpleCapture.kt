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

/**
 * Captures an image from the camera and processes it, including text recognition.
 *
 * @param imageCapture The ImageCapture object for taking pictures.
 * @param onPhotoCaptured A lambda function called after a photo is captured,
 *                        receiving the captured ImageBitmap and BlockInfo list.
 * @param onTextRecognized A lambda function called after text recognition is complete,
 *                         receiving the recognized text.
 * @param onComplete A lambda function called after all operations are completed.
 */
fun simpleCapture(
    imageCapture: ImageCapture,
    onPhotoCaptured: (ImageBitmap, List<BlockInfo>) -> Unit,
    onTextRecognized: (String) -> Unit,
    onComplete: () -> Unit,
    onError: () -> Unit
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
                    TextRecognition.recognizeTextFromImage(bitmap,
                        onError = {
                            onError()
                            executor.shutdown()
                        }
                    ) { blockInfos, text ->
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

/**
 * Rotates a Bitmap by the specified degrees.
 *
 * @param bitmap The Bitmap to rotate.
 * @param degrees The angle of rotation in degrees.
 * @return The rotated Bitmap.
 */
private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix().apply {
        postRotate(degrees)
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}