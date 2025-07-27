package se.umu.cs.phbo0006.parkLens.model.signs

import android.graphics.Rect
import com.google.mlkit.vision.text.Text

data class BlockData(
    val line: List<Text.Line?>,
    val boundingBox: Rect
)