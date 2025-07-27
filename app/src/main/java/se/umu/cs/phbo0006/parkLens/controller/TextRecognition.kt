package se.umu.cs.phbo0006.parkLens.controller

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
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
import se.umu.cs.phbo0006.parkLens.controller.util.checkForRedText
import se.umu.cs.phbo0006.parkLens.controller.util.detectBlockColor
import se.umu.cs.phbo0006.parkLens.model.signs.SymbolType
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo
import se.umu.cs.phbo0006.parkLens.model.signs.BlockData

class TextRecognition {

    companion object {

        fun recognizeTextFromImage(
            image: Bitmap,
            onResult: (List<BlockInfo>, String) -> Unit
        ) {
            try {
                val inputImage = InputImage.fromBitmap(image, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        val (blockInfos, resultText) = processTextRecognitionResult(visionText, image)
                        onResult(blockInfos, resultText)
                    }
                    .addOnFailureListener { e ->
                        Log.e("MLKit", "Text recognition error", e)
                        onResult(emptyList(), "Text recognition failed")
                    }
            } catch (e: Exception) {
                Log.e("MLKit", "Image processing error", e)
                onResult(emptyList(), "Failed to process image")
            }
        }

        private fun processTextRecognitionResult(visionText: Text, bitmap: Bitmap): Pair<List<BlockInfo>, String> {
            val resultText = StringBuilder()
            val rules = mutableListOf<List<ParkingRule>>()
            val blockInfos = mutableListOf<BlockInfo>()

            val blockData = visionText.textBlocks.mapNotNull { block ->
                block.boundingBox?.let { box ->
                    BlockData(block.lines, box)
                }
            }


            val clusters = clusterTextBoxes(blockData)

            for (i in 0 until visionText.textBlocks.size) {
                val block = visionText.textBlocks[i]

                if (clusters.size > i){
                    val boundingBoxRn = clusters[i].boundingBox
                    val clusterText = clusters[i].text
                    val croppedImage = cropBitmapToBounds(bitmap, boundingBoxRn, true)
                    val color = detectBlockColor(croppedImage?.asBitmap() ?: bitmap)

                    // Extract rules from lines
                    val lineRules = mutableListOf<ParkingRule>()

                    for(y in 0 until clusterText.size){
                        val rule = extractRuleFromLine(clusterText[y]?.text.toString()) as ParkingRule

                        if (rule.type == SymbolType.WEEKDAY){
                            val croppedImage = cropBitmapToBounds(bitmap,
                                clusterText[y]?.boundingBox, false)

                            if (checkForRedText(bitmap = croppedImage?.asBitmap() ?: bitmap)){
                                rule.type = SymbolType.HOLIDAY
                            }
                        }

                        lineRules.add(rule)

                        Log.i("PARSER!", "Parsed Rule: $rule")

                    }

                    rules.add(lineRules)


                    blockInfos.add(
                        BlockInfo(
                            text = clusters[i].combinedText,
                            croppedImage = croppedImage,
                            color = color,
                            rules = lineRules
                        )
                    )
                }

                Log.i("PARSER", "Block: ${block.text}")
                resultText.append("Block: ${block.text}\n")

            }

            //val restricted = isRestrictedNow(rules)
            //resultText.append("\n${if (restricted) "🚫 Cannot park now" else "✅ Parking allowed"}\n")

            return blockInfos to resultText.toString()
        }

        fun cropBitmapToBounds(bitmap: Bitmap, bounds: Rect?, expand: Boolean): ImageBitmap? {
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
                val cropedBounds = if(expand){
                    Rect(bounds).apply {
                        val expansion = (minOf(bitmap.width, bitmap.height) * 0.03).toInt()
                        inset(-expansion, -expansion)
                        left = left.coerceIn(0, bitmap.width - 1)
                        top = top.coerceIn(0, bitmap.height - 1)
                        right = right.coerceIn(left + 1, bitmap.width)
                        bottom = bottom.coerceIn(top + 1, bitmap.height)
                    }
                }else{
                    Rect(bounds).apply {
                        val shrinkage = (minOf(width(), height()) * 0.2).toInt()
                        inset(shrinkage, shrinkage)
                        left = left.coerceIn(0, bitmap.width - 1)
                        top = top.coerceIn(0, bitmap.height - 1)
                        right = right.coerceIn(left + 1, bitmap.width)
                        bottom = bottom.coerceIn(top + 1, bitmap.height)
                    }
                }

                Log.i("CropDebug", "Expanded bounds: $cropedBounds")
                val croppedBitmap = Bitmap.createBitmap(
                    bitmap,
                    cropedBounds.left,
                    cropedBounds.top,
                    cropedBounds.width(),
                    cropedBounds.height()
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