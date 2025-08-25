package se.umu.cs.phbo0006.parkLens.model.appData

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo

class AppViewModel : ViewModel() {
    private val _capturedBitmap = mutableStateOf<ImageBitmap?>(null)
    val capturedBitmap: State<ImageBitmap?> = _capturedBitmap

    private val _blockInfos = mutableStateOf<List<BlockInfo>>(emptyList())
    val blockInfos: State<List<BlockInfo>> = _blockInfos

    private val _recognizedText = mutableStateOf<String?>(null)
    val recognizedText: State<String?> = _recognizedText

    private val _debugMode = mutableStateOf(false)
    val debugMode: State<Boolean> = _debugMode

    private val _isProcessing = mutableStateOf(false)
    val isProcessing: State<Boolean> = _isProcessing

    fun updateCapturedData(bitmap: ImageBitmap, infos: List<BlockInfo>, text: String?) {
        _capturedBitmap.value = bitmap
        _blockInfos.value = infos
        _recognizedText.value = text
    }

    fun updateRecognizedText(text: String?) {
        _recognizedText.value = text
    }

    fun setDebugMode(enabled: Boolean) {
        _debugMode.value = enabled
    }

    fun setProcessing(processing: Boolean) {
        _isProcessing.value = processing
    }

    fun clearCapturedData() {
        _capturedBitmap.value = null
        _blockInfos.value = emptyList()
        _recognizedText.value = null
        _capturedBitmap.value = null
    }
}