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
 * Displays the camera permission UI flow.
 *
 * Shows either the main navigation, a permission denied screen, or a loading screen
 * depending on the permission state.
 *
 * @param hasCameraPermission True if camera permission is granted.
 * @param showPermissionDeniedScreen True if permission denied UI should be shown.
 * @param shouldShowRationale True if rationale for permission should be shown.
 * @param onRequestPermission Callback for requesting permission.
 * @param onOpenSettings Callback for opening device settings.
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
            CameraPermissionContent(
                hasCameraPermission = hasCameraPermission,
                showPermissionDeniedScreen = showPermissionDeniedScreen,
                shouldShowRationale = shouldShowRationale,
                onRequestPermission = onRequestPermission,
                onOpenSettings = onOpenSettings,
                navGraph = navGraph
            )
        }
    }
}

/**
 * Handles which UI to show based on permission state.
 *
 * @param hasCameraPermission True if camera permission is granted.
 * @param showPermissionDeniedScreen True if permission denied UI should be shown.
 * @param shouldShowRationale True if rationale for permission should be shown.
 * @param onRequestPermission Callback for requesting permission.
 * @param onOpenSettings Callback for opening device settings.
 * @param navGraph Navigation graph instance.
 */
@Composable
private fun CameraPermissionContent(
    hasCameraPermission: Boolean,
    showPermissionDeniedScreen: Boolean,
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    navGraph: NavGraph
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

/**
 * Screen shown when camera permission is denied.
 *
 * Offers options to request permission or open settings.
 *
 * @param shouldShowRationale True if rationale for permission should be shown.
 * @param onRequestPermission Callback for requesting permission.
 * @param onOpenSettings Callback for opening device settings.
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
        PermissionDeniedTitle()
        Spacer(modifier = Modifier.height(24.dp))
        PermissionDeniedText()
        Spacer(modifier = Modifier.height(32.dp))
        PermissionDeniedActions(
            shouldShowRationale = shouldShowRationale,
            onRequestPermission = onRequestPermission,
            onOpenSettings = onOpenSettings
        )
    }
}

/**
 * Displays the title for the permission denied screen.
 */
@Composable
private fun PermissionDeniedTitle() {
    Text(
        text = stringResource(R.string.camera_permission_title),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    )
}

/**
 * Displays the explanatory text for the permission denied screen.
 */
@Composable
private fun PermissionDeniedText() {
    Text(
        text = stringResource(R.string.camera_permission_text),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/**
 * Displays actions for the permission denied screen.
 *
 * Shows either a permission request button or a button to open settings.
 *
 * @param shouldShowRationale True if rationale for permission should be shown.
 * @param onRequestPermission Callback for requesting permission.
 * @param onOpenSettings Callback for opening device settings.
 */
@Composable
private fun PermissionDeniedActions(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    if (shouldShowRationale) {
        PermissionRequestButton(onRequestPermission)
    } else {
        OpenSettingsButton(onOpenSettings)
        Spacer(modifier = Modifier.height(16.dp))
        SettingsNavigationText()
    }
}

/**
 * Button to request camera permission.
 *
 * @param onRequestPermission Callback for requesting permission.
 */
@Composable
private fun PermissionRequestButton(onRequestPermission: () -> Unit) {
    Button(
        onClick = onRequestPermission,
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Text(stringResource(R.string.grant_permission))
    }
}

/**
 * Button to open device settings.
 *
 * @param onOpenSettings Callback for opening device settings.
 */
@Composable
private fun OpenSettingsButton(onOpenSettings: () -> Unit) {
    Button(
        onClick = onOpenSettings,
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Text(stringResource(R.string.open_settings))
    }
}

/**
 * Text explaining how to navigate to settings.
 */
@Composable
private fun SettingsNavigationText() {
    Text(
        text = stringResource(R.string.settings_navigation),
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}