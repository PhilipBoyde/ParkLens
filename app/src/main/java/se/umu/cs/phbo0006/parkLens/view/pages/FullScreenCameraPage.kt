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
import kotlinx.coroutines.launch
import se.umu.cs.phbo0006.parkLens.model.appData.Languages
import se.umu.cs.phbo0006.parkLens.view.ui.theme.BackgroundColor
import se.umu.cs.phbo0006.parkLens.view.ui.theme.ParkingBlue
import se.umu.cs.phbo0006.parkLens.view.ui.theme.RestrictedParkingBorder
import se.umu.cs.phbo0006.parkLens.view.ui.theme.TextColor

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
                    // Menu content
                    Text(
                        text = stringResource(R.string.settings_title),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )


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
                                onClick = { expanded = true },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF323232),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(text = selectedLanguage.toString())
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .background(Color(0xFF323232))
                            ) {
                                languageList.forEach { lang ->
                                    DropdownMenuItem(
                                        onClick = {
                                            onLanguageSelected(lang)
                                            expanded = false
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

                    Spacer(modifier = Modifier.weight(1f))

                    // Debug toggle
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
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Camera Preview
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

                // Settings Button
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier
                        .statusBarsPadding()
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .size(50.dp)
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

                // Capture Button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 80.dp)
                ) {
                    Box( // Clickable area
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onCaptureClick)
                    ) {
                        Box( // Outer ring
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
                        Box( // Inner button
                            modifier = Modifier
                                .size(64.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }
        }
    )
}