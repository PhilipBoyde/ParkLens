package se.umu.cs.phbo0006.parkLens.model.signs

import android.graphics.Rect
import com.google.mlkit.vision.text.Text

/**
 * Represents the data for a block of text.
 *
 * This data class holds the lines of text that make up a block,
 * along with its bounding rectangle.
 *
 * @param line The list of Text.Line objects that comprise the block.
 * @param boundingBox The bounding rectangle for the block.
 */
data class BlockData(
    val line: List<Text.Line?>,
    val boundingBox: Rect
)