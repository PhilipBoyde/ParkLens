package se.umu.cs.phbo0006.parkLens.model.signs

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Represents information about a parking block.
 *
 * This data class holds details such as the text content,
 * a cropped image (if available), the color of the block,
 * and the associated parking rules.
 *
 * @param text The text content of the parking block.
 * @param croppedImage The cropped image associated with the block (nullable).
 * @param color The color of the parking block.
 * @param rules The list of parking rules associated with the block.
 */
data class BlockInfo(
    val text: String,
    val croppedImage: ImageBitmap?,
    val color: SignType,
    val rules: List<ParkingRule>
)