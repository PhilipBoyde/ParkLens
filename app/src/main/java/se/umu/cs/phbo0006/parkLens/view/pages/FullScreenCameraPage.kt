package se.umu.cs.phbo0006.parkLens.view.pages

import se.umu.cs.phbo0006.parkLens.R
import android.view.Surface
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import se.umu.cs.phbo0006.parkLens.model.appData.Languages
import se.umu.cs.phbo0006.parkLens.view.ui.theme.BackgroundColor
import se.umu.cs.phbo0006.parkLens.view.ui.theme.ParkingBlue
import se.umu.cs.phbo0006.parkLens.view.ui.theme.RestrictedParkingBorder
import se.umu.cs.phbo0006.parkLens.view.ui.theme.TextColor
import androidx.lifecycle.LifecycleOwner

/**
 * Displays a full-screen camera interface with debug controls, settings panel,
 * and image capture functionality.
 *
 * @param imageCapture Optional ImageCapture instance to handle captured images
 * @param debugMode Current debug mode state (true/false)
 * @param onDebugModeChange Callback invoked when the user toggles debug mode
 * @param onCaptureClick Callback triggered by tapping the capture button
 * @param selectedLanguage Currently selected language for UI text localization
 * @param onLanguageSelected Callback to change the application's language
 */
@Composable
fun FullScreenCameraPage(
    imageCapture: ImageCapture? = null,
    debugMode: Boolean,
    onDebugModeChange: (Boolean) -> Unit,
    onCaptureClick: () -> Unit,
    selectedLanguage: Languages,
    onLanguageSelected: (Languages) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    val languageList = listOf(Languages.ENGLISH, Languages.SVENSKA)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SettingsDrawerContent(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                languageList = languageList,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected,
                debugMode = debugMode,
                onDebugModeChange = onDebugModeChange
            )
        },
        content = {
            CameraScreenContent(
                imageCapture = imageCapture,
                cameraProviderFuture = cameraProviderFuture,
                lifecycleOwner = lifecycleOwner,
                scope = scope,
                drawerState = drawerState,
                onCaptureClick = onCaptureClick
            )
        }
    )
}

/**
 * Drawer content for settings, including language selection and debug mode toggle.
 */
@Composable
private fun SettingsDrawerContent(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    languageList: List<Languages>,
    selectedLanguage: Languages,
    onLanguageSelected: (Languages) -> Unit,
    debugMode: Boolean,
    onDebugModeChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.65f)
            .background(BackgroundColor)
            .padding(12.dp)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LanguageSelectionRow(
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                languageList = languageList,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
            Spacer(modifier = Modifier.weight(1f))
            DebugToggleRow(
                debugMode = debugMode,
                onDebugModeChange = onDebugModeChange
            )
        }
    }
}

/**
 * Row for language selection in the settings drawer.
 */
@Composable
private fun LanguageSelectionRow(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    languageList: List<Languages>,
    selectedLanguage: Languages,
    onLanguageSelected: (Languages) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.language_icon),
            contentDescription = "Debug",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.language),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Box {
            Button(
                onClick = { onExpandedChange(true) },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF323232),
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(text = selectedLanguage.toString())
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.background(Color(0xFF323232))
            ) {
                languageList.forEach { lang ->
                    DropdownMenuItem(
                        onClick = {
                            onLanguageSelected(lang)
                            onExpandedChange(false)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        text = {
                            Text(
                                lang.toString(),
                                fontSize = 18.sp,
                                color = TextColor
                            )
                        }
                    )
                }
            }
        }
    }
}

/**
 * Row for debug mode toggle in the settings drawer.
 */
@Composable
private fun DebugToggleRow(
    debugMode: Boolean,
    onDebugModeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.debug_mode_icon),
            contentDescription = "Debug",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.debug_mode),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = debugMode,
            onCheckedChange = { onDebugModeChange(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextColor,
                checkedTrackColor = RestrictedParkingBorder,
                uncheckedThumbColor = TextColor,
                uncheckedTrackColor = Color(0xFF323232)
            )
        )
    }
}

/**
 * Main camera screen content, including preview, settings button, and capture button.
 */
@Composable
private fun CameraScreenContent(
    imageCapture: ImageCapture?,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    lifecycleOwner: LifecycleOwner,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onCaptureClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CameraPreview(
            imageCapture = imageCapture,
            cameraProviderFuture = cameraProviderFuture,
            lifecycleOwner = lifecycleOwner
        )
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .size(50.dp)
                .align(Alignment.TopStart)
        ) {
            SettingsButton(
                scope = scope,
                drawerState = drawerState
            )
        }
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 80.dp)
                .align(Alignment.BottomCenter)
        ) {
            CaptureButton(
                onCaptureClick = onCaptureClick
            )
        }
    }
}

/**
 * Camera preview composable using CameraX.
 */
@Composable
private fun CameraPreview(
    imageCapture: ImageCapture?,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    lifecycleOwner: LifecycleOwner
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val preview = Preview.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .build().apply {
                    surfaceProvider = previewView.surfaceProvider
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val cameraProvider = cameraProviderFuture.get()

            try {
                cameraProvider.unbindAll()
                if (imageCapture != null) {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } else {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            previewView
        }
    )
}

/**
 * Settings button composable that opens the drawer.
 */
@Composable
private fun SettingsButton(
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    IconButton(
        onClick = { scope.launch { drawerState.open() } },
        modifier = Modifier
            .background(
                color = ParkingBlue.copy(alpha = 0.4f),
                shape = CircleShape
            )
            .clip(CircleShape)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.settings_icon),
            contentDescription = "Settings",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * Capture button composable for taking a photo.
 */
@Composable
private fun CaptureButton(
    onCaptureClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .clickable(onClick = onCaptureClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    clip = true
                )
                .border(
                    width = 4.dp,
                    color = Color.White,
                    shape = CircleShape
                )
                .background(Color.Transparent)
        )
        Box(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}