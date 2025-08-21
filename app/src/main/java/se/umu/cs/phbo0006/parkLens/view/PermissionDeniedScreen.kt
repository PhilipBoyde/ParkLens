package se.umu.cs.phbo0006.parkLens.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import se.umu.cs.phbo0006.parkLens.R

/**
 * Represents a screen displayed when camera permission is denied.
 *
 * This composable function creates a simple screen with a black background
 * and a white text message indicating that camera permission was denied.
 */
@Composable
fun CameraPermissionDeniedScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.camera_permission_denied),
            color = Color.White)
    }
}