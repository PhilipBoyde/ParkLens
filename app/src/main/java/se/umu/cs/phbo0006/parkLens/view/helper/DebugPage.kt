package se.umu.cs.phbo0006.parkLens.view.helper

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo
import se.umu.cs.phbo0006.parkLens.model.signs.SignType
import se.umu.cs.phbo0006.parkLens.view.ui.theme.TextColor

@Composable
fun DebugPage(
    bitmap: ImageBitmap,
    blockInfos: List<BlockInfo>,
    recognizedText: String?,
    onBackToCamera: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {


        Image(
            bitmap = bitmap,
            contentDescription = "Captured image",
            modifier = Modifier
                .fillMaxWidth(0.25f),
            contentScale = ContentScale.Fit
        )


        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(16.dp)
        ) {
            items(blockInfos.size) { index ->
                val block = blockInfos[index]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    block.croppedImage?.let { cropped ->
                        Image(
                            bitmap = cropped,
                            contentDescription = "Cropped block ${index + 1}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Text(
                        text = "Block ${index + 1}: ${block.text}",
                        color = TextColor,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Color: ${block.color}",
                        color = when (block.color) {
                            SignType.BLUE -> Color.Blue
                            SignType.YELLOW  -> Color.Yellow
                            else -> TextColor
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }


        /*
        recognizedText?.let {
            Text(
                text = it,
                color = TextColor,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
         */

        Button(
            onClick = onBackToCamera,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Back to Camera")
        }
    }
}