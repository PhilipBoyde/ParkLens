package se.umu.cs.phbo0006.parkLens.view.helper


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.umu.cs.phbo0006.parkLens.view.ui.theme.BackgroundColor
import se.umu.cs.phbo0006.parkLens.R

/**
 * Displays a loading screen with a background, circular progress indicator,
 * and an optional loading message.
 *
 * @param showText Whether to display the loading message below the indicator.
 */
@Composable
fun LoadingScreen(showText: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .systemBarsPadding()
            .padding(16.dp)
            .pointerInput(Unit) {},
        contentAlignment = Alignment.Center
    ) {
        LoadingContent(showText)
    }
}

/**
 * Arranges the loading indicator and optional loading text vertically.
 *
 * @param showText Whether to display the loading message.
 */
@Composable
private fun LoadingContent(showText: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoadingIndicator()
        if (showText) {
            Spacer(modifier = Modifier.height(16.dp))
            LoadingText()
        }
    }
}

/**
 * Shows a circular progress indicator for loading state.
 */
@Composable
private fun LoadingIndicator() {
    CircularProgressIndicator(
        color = Color.White,
        strokeWidth = 6.dp,
        modifier = Modifier.size(64.dp)
    )
}

/**
 * Displays a loading message text.
 */
@Composable
private fun LoadingText() {
    Text(
        text = stringResource(R.string.loading_screen),
        fontSize = 24.sp,
        color = Color.White
    )
}
