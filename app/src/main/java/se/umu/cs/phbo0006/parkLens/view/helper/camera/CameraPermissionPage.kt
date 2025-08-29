package se.umu.cs.phbo0006.parkLens.view.helper.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import se.umu.cs.phbo0006.parkLens.R
import se.umu.cs.phbo0006.parkLens.view.NavGraph
import se.umu.cs.phbo0006.parkLens.view.helper.LoadingScreen

/**
 * A Compose UI component for handling camera permission requests.
 * This component displays different screens based on whether the user has granted
 * camera permission, or if there are issues with the permission.
 *
 * @param hasCameraPermission True if the user has granted camera permission.
 * @param showPermissionDeniedScreen True if a permission denied screen is being displayed.
 * @param shouldShowRationale True if the user has been prompted to grant permission
 *                           and chose not to, indicating a "Don't ask again" option.
 * @param onRequestPermission A lambda function to be called when the user requests
 *                            to grant camera permission.
 * @param onOpenSettings A lambda function to be called when the user needs to
 *                       manually open the device's settings app.
 */
@Composable
fun CameraPermission(
    hasCameraPermission: Boolean,
    showPermissionDeniedScreen: Boolean,
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val navGraph = NavGraph()
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                hasCameraPermission -> {
                    navGraph.AppNavHost()
                }
                showPermissionDeniedScreen -> {
                    PermissionDeniedScreen(
                        shouldShowRationale = shouldShowRationale,
                        onRequestPermission = onRequestPermission,
                        onOpenSettings = onOpenSettings
                    )
                }
                else -> {
                    LoadingScreen(false)
                }
            }
        }
    }
}

/**
 * A Compose UI screen to display when the user has denied camera permission.
 * This screen provides options for the user to grant permission or manually
 * open the device's settings app.
 *
 * @param shouldShowRationale True if the user has been prompted to grant permission
 *                           and chose not to.
 * @param onRequestPermission A lambda function to be called when the user requests
 *                            to grant camera permission.
 * @param onOpenSettings A lambda function to be called when the user needs to
 *                       manually open the device's settings app.
 */
@Composable
fun PermissionDeniedScreen(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.camera_permission_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.camera_permission_text),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (shouldShowRationale) {
            // User denied
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(stringResource(R.string.grant_permission))
            }
        } else {
            // "Don't ask again" - need to go to settings
            Button(
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(stringResource(R.string.open_settings))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.settings_navigation),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}