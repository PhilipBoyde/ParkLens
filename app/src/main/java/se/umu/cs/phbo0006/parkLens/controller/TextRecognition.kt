package se.umu.cs.phbo0006.parkLens.controller

import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

import se.umu.cs.phbo0006.parkLens.controller.util.extractRuleFromLine
import se.umu.cs.phbo0006.parkLens.model.signs.ParkingRule


class TextRecognition (){

    companion object{
        fun recognizeTextFromImage(
            image: ImageProxy,
            onResult: (String) -> Unit
        ) {
            try {
                val bitmap = image.toBitmap()
                val inputImage = InputImage.fromBitmap(bitmap, image.imageInfo.rotationDegrees)

                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        val result = processTextRecognitionResult(visionText)
                        onResult(result)
                    }
                    .addOnFailureListener { e ->
                        Log.e("MLKit", "Text recognition error", e)
                        onResult("Text recognition failed")
                    }
            } catch (e: Exception) {
                Log.e("MLKit", "Image processing error", e)
                onResult("Failed to process image")
            } finally {
                image.close()
            }
        }



        private fun processTextRecognitionResult(visionText: Text): String {
            val resultText = StringBuilder()
            val rules = mutableListOf<ParkingRule>()


            for (block in visionText.textBlocks) {
                Log.i("PARSER!", "Block: ${block.text}")
                resultText.append("Block: ${block.text}\n")

                for (line in block.lines) {
                    val rule = extractRuleFromLine(line.text)
                    if (rule != null) {
                        rules.add(rule)
                        Log.i("PARSER!", "Parsed Rule: $rule")
                    }
                }
            }

            val restricted = isRestrictedNow(rules)
            resultText.append("\n${if (restricted) "🚫 Cannot park now" else "✅ Parking allowed"}\n")

            return resultText.toString()
        }



    }


}

