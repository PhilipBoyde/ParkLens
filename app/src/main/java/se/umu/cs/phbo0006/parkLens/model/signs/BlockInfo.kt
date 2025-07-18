package se.umu.cs.phbo0006.parkLens.model.signs

import androidx.compose.ui.graphics.ImageBitmap

data class BlockInfo(
    val text: String,
    val croppedImage: ImageBitmap?,
    val color: SignType
)