package se.umu.cs.phbo0006.parkLens.view


import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.view.Surface
import se.umu.cs.phbo0006.parkLens.controller.checkIfAllowedToPark
import se.umu.cs.phbo0006.parkLens.view.helper.LoadingScreen
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import se.umu.cs.phbo0006.parkLens.controller.util.LanguageManager
import se.umu.cs.phbo0006.parkLens.model.appData.AppViewModel
import se.umu.cs.phbo0006.parkLens.model.appData.Languages
import se.umu.cs.phbo0006.parkLens.view.helper.DebugModePage
import se.umu.cs.phbo0006.parkLens.view.helper.camera.simpleCapture
import se.umu.cs.phbo0006.parkLens.view.pages.FullScreenCameraPage
import se.umu.cs.phbo0006.parkLens.view.pages.ParkingRulePage
import se.umu.cs.phbo0006.parkLens.view.pages.SignPreviewPage
import se.umu.cs.phbo0006.parkLens.view.ui.theme.BackgroundColor


class NavGraph {

    private object Routes {
        const val ROUTE_CAMERA = "camera"
        const val ROUTE_COLOR_BLOCKS = "color_blocks"
        const val ROUTE_DEBUG_PREVIEW = "debug_preview"
        const val ROUTE_PARKING_RULES = "parking_rules"
    }

    @Composable
    fun AppNavHost() {
        val context = LocalContext.current
        val navController = rememberNavController()
        val sharedViewModel: AppViewModel = viewModel()

        var currentLanguage by remember { mutableStateOf(Languages.ENGLISH) }

        val localizedContext = remember(currentLanguage) {
            LanguageManager.updateLocale(context, currentLanguage.locale)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {

            CompositionLocalProvider(LocalContext provides localizedContext) {
                NavHost(
                    navController = navController,
                    startDestination = Routes.ROUTE_CAMERA,
                    enterTransition = { fadeIn(animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                    popExitTransition = { fadeOut(animationSpec = tween(300)) }
                ) {
                    composable(Routes.ROUTE_CAMERA) {
                        val imageCapture = remember {
                            ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                                .setTargetRotation(Surface.ROTATION_0)
                                .build()
                        }

                        val isProcessing by sharedViewModel.isProcessing
                        val debugMode by sharedViewModel.debugMode

                        Box {
                            FullScreenCameraPage(
                                imageCapture = imageCapture,
                                debugMode = debugMode,
                                onDebugModeChange = { sharedViewModel.setDebugMode(it) },
                                onCaptureClick = {
                                    sharedViewModel.setProcessing(true)

                                    simpleCapture(
                                        imageCapture = imageCapture,
                                        onPhotoCaptured = { bitmap, infos ->
                                            sharedViewModel.updateCapturedData(
                                                bitmap,
                                                infos,
                                                sharedViewModel.recognizedText.value
                                            )
                                        },
                                        onTextRecognized = { text ->
                                            sharedViewModel.updateRecognizedText(text)
                                        },
                                        onComplete = {
                                            sharedViewModel.setProcessing(false)

                                            if (debugMode) {
                                                navController.navigate(Routes.ROUTE_DEBUG_PREVIEW)
                                            } else {
                                                navController.navigate(Routes.ROUTE_COLOR_BLOCKS)
                                            }
                                        }
                                    )
                                },
                                selectedLanguage = currentLanguage,
                                onLanguageSelected = { selected ->
                                    currentLanguage = selected
                                }
                            )

                            if (isProcessing) {
                                LoadingScreen(true)
                            }
                        }
                    }


                    composable(Routes.ROUTE_COLOR_BLOCKS) {
                        val blocksInfos by sharedViewModel.blockInfos

                        SignPreviewPage(
                            blocks = blocksInfos,
                            onTakeNewPhoto = {
                                if (!sharedViewModel.debugMode.value){
                                    sharedViewModel.clearCapturedData()
                                }
                                navController.popBackStack()

                            },
                            onContinue = {
                                navController.navigate(Routes.ROUTE_PARKING_RULES)
                            }

                        )
                    }

                    composable(Routes.ROUTE_DEBUG_PREVIEW) {
                        val blocksInfos by sharedViewModel.blockInfos

                        DebugModePage(
                            blocksInfos,
                            onBackClick = {
                                navController.popBackStack()
                                sharedViewModel.clearCapturedData()
                            },
                            onNextClick = {
                                navController.navigate(Routes.ROUTE_COLOR_BLOCKS)
                            }
                        )
                    }

                    composable(Routes.ROUTE_PARKING_RULES) {
                        val blocksInfos by sharedViewModel.blockInfos

                        ParkingRulePage(
                            rules = checkIfAllowedToPark(blocksInfos),
                            onBack = { navController.popBackStack() },
                        )
                    }
                }

            }
        }
    }
}








