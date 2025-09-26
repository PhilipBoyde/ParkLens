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
import java.util.concurrent.ExecutorService

/**
 * Captures an image from the camera and processes it, including text recognition.
 *
 * @param imageCapture The ImageCapture object for taking pictures.
 * @param onPhotoCaptured Called after a photo is captured, receives the captured ImageBitmap and BlockInfo list.
 * @param onTextRecognized Called after text recognition is complete, receives the recognized text.
 * @param onComplete Called after all operations are completed.
 * @param onError Called if an error occurs during capture or processing.
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
                processCapturedImage(
                    image,
                    executor,
                    onPhotoCaptured,
                    onTextRecognized,
                    onComplete,
                    onError
                )
            }

            override fun onError(exc: ImageCaptureException) {
                handleCaptureError(exc, onComplete, executor)
            }
        }
    )
}

/**
 * Processes the captured image, rotates if needed, and starts text recognition.
 *
 * @param image The captured ImageProxy.
 * @param executor The ExecutorService for background tasks.
 * @param onPhotoCaptured Callback for photo captured.
 * @param onTextRecognized Callback for text recognized.
 * @param onComplete Callback for completion.
 * @param onError Callback for errors.
 */
private fun processCapturedImage(
    image: ImageProxy,
    executor: ExecutorService,
    onPhotoCaptured: (ImageBitmap, List<BlockInfo>) -> Unit,
    onTextRecognized: (String) -> Unit,
    onComplete: () -> Unit,
    onError: () -> Unit
) {
    try {
        val bitmap = image.toBitmap()
        val rotationDegrees = image.imageInfo.rotationDegrees.toFloat()
        val rotatedBitmap = if (rotationDegrees != 0f) {
            rotateBitmap(bitmap, rotationDegrees)
        } else {
            bitmap
        }
        val imageBitmap = rotatedBitmap.asImageBitmap()
        handleTextRecognition(
            bitmap,
            imageBitmap,
            executor,
            onPhotoCaptured,
            onTextRecognized,
            onComplete,
            onError
        )
    } catch (e: Exception) {
        Log.e("Camera", "Processing error", e)
        onComplete()
        executor.shutdown()
    } finally {
        image.close()
    }
}

/**
 * Handles text recognition from a bitmap and invokes callbacks.
 *
 * @param bitmap The bitmap to recognize text from.
 * @param imageBitmap The ImageBitmap for display.
 * @param executor The ExecutorService for background tasks.
 * @param onPhotoCaptured Callback for photo captured.
 * @param onTextRecognized Callback for text recognized.
 * @param onComplete Callback for completion.
 * @param onError Callback for errors.
 */
private fun handleTextRecognition(
    bitmap: Bitmap,
    imageBitmap: ImageBitmap,
    executor: ExecutorService,
    onPhotoCaptured: (ImageBitmap, List<BlockInfo>) -> Unit,
    onTextRecognized: (String) -> Unit,
    onComplete: () -> Unit,
    onError: () -> Unit
) {
    TextRecognition.recognizeTextFromImage(
        bitmap,
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
}

/**
 * Handles errors during image capture.
 *
 * @param exc The ImageCaptureException thrown.
 * @param onComplete Callback for completion.
 * @param executor The ExecutorService for background tasks.
 */
private fun handleCaptureError(
    exc: ImageCaptureException,
    onComplete: () -> Unit,
    executor: ExecutorService
) {
    Log.e("Camera", "Capture failed", exc)
    onComplete()
    executor.shutdown()
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