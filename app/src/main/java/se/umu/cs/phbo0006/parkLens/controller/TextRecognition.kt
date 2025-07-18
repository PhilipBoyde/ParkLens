package se.umu.cs.phbo0006.parkLens.controller

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import se.umu.cs.phbo0006.parkLens.controller.util.extractRuleFromLine
import se.umu.cs.phbo0006.parkLens.model.signs.ParkingRule
import androidx.core.graphics.createBitmap
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo

class TextRecognition {

    companion object {

        fun recognizeTextFromImage(
            image: ImageProxy,
            onResult: (List<BlockInfo>, String) -> Unit
        ) {
            try {
                val bitmap = image.toBitmap()
                Log.i("TextRecognition", "Rotation degrees: ${image.imageInfo.rotationDegrees}")
                val inputImage = InputImage.fromBitmap(bitmap, 0)

                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        val (blockInfos, resultText) = processTextRecognitionResult(visionText, bitmap)
                        onResult(blockInfos, resultText)
                    }
                    .addOnFailureListener { e ->
                        Log.e("MLKit", "Text recognition error", e)
                        onResult(emptyList(), "Text recognition failed")
                    }
            } catch (e: Exception) {
                Log.e("MLKit", "Image processing error", e)
                onResult(emptyList(), "Failed to process image")
            } finally {
                image.close()
            }
        }

        private fun processTextRecognitionResult(visionText: Text, bitmap: Bitmap): Pair<List<BlockInfo>, String> {
            val resultText = StringBuilder()
            val rules = mutableListOf<ParkingRule>()
            val blockInfos = mutableListOf<BlockInfo>()

            for (block in visionText.textBlocks) {
                Log.i("PARSER!", "Block: ${block.text}")
                resultText.append("Block: ${block.text}\n")

                // Extract rules from lines
                for (line in block.lines) {
                    val rule = extractRuleFromLine(line.text)
                    if (rule != null) {
                        rules.add(rule)
                        Log.i("PARSER!", "Parsed Rule: $rule")
                    }
                }

                val blockBounds = block.boundingBox
                val croppedImage = cropBitmapToBounds(bitmap, blockBounds)
                val color = detectBlockColor(croppedImage?.asBitmap() ?: bitmap)

                blockInfos.add(
                    BlockInfo(
                        text = block.text,
                        croppedImage = croppedImage,
                        color = color
                    )
                )
            }

            val restricted = isRestrictedNow(rules)
            resultText.append("\n${if (restricted) "🚫 Cannot park now" else "✅ Parking allowed"}\n")

            return blockInfos to resultText.toString()
        }

        fun cropBitmapToBounds(bitmap: Bitmap, bounds: Rect?): ImageBitmap? {
            if (bounds == null) {
                Log.w("CropDebug", "Null bounds, cannot crop")
                return null
            }
            Log.i(
                "CropBounds",
                "Corners: TL=(${bounds.left},${bounds.top}), TR=(${bounds.right},${bounds.top}), " +
                        "BL=(${bounds.left},${bounds.bottom}), BR=(${bounds.right},${bounds.bottom})"
            )
            Log.i("CropDebug", "Bitmap size: ${bitmap.width}x${bitmap.height}, Bounds: $bounds")
            try {
                val expandedBounds = Rect(bounds).apply {
                    val expansion = (minOf(bitmap.width, bitmap.height) * 0.03).toInt()
                    inset(-expansion, -expansion)
                    left = left.coerceIn(0, bitmap.width - 1)
                    top = top.coerceIn(0, bitmap.height - 1)
                    right = right.coerceIn(left + 1, bitmap.width)
                    bottom = bottom.coerceIn(top + 1, bitmap.height)
                }
                Log.i("CropDebug", "Expanded bounds: $expandedBounds")
                val croppedBitmap = Bitmap.createBitmap(
                    bitmap,
                    expandedBounds.left,
                    expandedBounds.top,
                    expandedBounds.width(),
                    expandedBounds.height()
                )
                return croppedBitmap.asImageBitmap()
            } catch (e: Exception) {
                Log.e("CropDebug", "Crop failed: $e")
                return null
            }
        }


        private fun ImageBitmap.asBitmap(): Bitmap {
            val bitmap = createBitmap(width, height)
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawBitmap(
                this.asAndroidBitmap(),
                0f,
                0f,
                null
            )
            return bitmap
        }
    }
}