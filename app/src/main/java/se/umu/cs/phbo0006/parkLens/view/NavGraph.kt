package se.umu.cs.phbo0006.parkLens.view


import android.content.Context
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
import se.umu.cs.phbo0006.parkLens.view.helper.ErrorDialog
import se.umu.cs.phbo0006.parkLens.view.helper.camera.simpleCapture
import se.umu.cs.phbo0006.parkLens.view.pages.FullScreenCameraPage
import se.umu.cs.phbo0006.parkLens.view.pages.ParkingRulePage
import se.umu.cs.phbo0006.parkLens.view.pages.SignPreviewPage
import se.umu.cs.phbo0006.parkLens.view.ui.theme.BackgroundColor
import androidx.core.content.edit
import androidx.navigation.NavHostController


/**
 * Handles the navigation graph.
 * Defines navigation routes and manages navigation between composable screens.
 */
class NavGraph {

    companion object {
        private const val PREFS_NAME = "park_lens_prefs"
        private const val KEY_LANGUAGE = "selected_language"
    }

    /**
     * Object containing navigation route constants for the application.
     */
    private object Routes {
        const val ROUTE_CAMERA = "camera"
        const val ROUTE_COLOR_BLOCKS = "color_blocks"
        const val ROUTE_DEBUG_PREVIEW = "debug_preview"
        const val ROUTE_PARKING_RULES = "parking_rules"
        const val ROUTE_ERROR_POP_UP = "error_pop_up"
    }

    /**
     * Retrieves the saved language from shared preferences.
     *
     * @param context The context used to access shared preferences.
     * @return The saved language, or English if none is saved.
     */
    private fun getSavedLanguage(context: Context): Languages {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val langName = prefs.getString(KEY_LANGUAGE, null)
        return Languages.entries.find { it.name == langName } ?: Languages.ENGLISH
    }

    /**
     * Saves the selected language to shared preferences.
     *
     * @param context The context used to access shared preferences.
     * @param language The language to save.
     */
    private fun saveLanguage(context: Context, language: Languages) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_LANGUAGE, language.name) }
    }

    /**
     * Main navigation host composable for the application.
     * Sets up navigation routes and manages language selection.
     */
    @Composable
    fun AppNavHost() {
        val context = LocalContext.current
        val navController = rememberNavController()
        val sharedViewModel: AppViewModel = viewModel()

        var currentLanguage by remember { mutableStateOf(getSavedLanguage(context)) }

        LaunchedEffect(currentLanguage) {
            saveLanguage(context, currentLanguage)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            CompositionLocalProvider(
                LocalContext provides LanguageManager.updateLocale(context, currentLanguage.locale)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Routes.ROUTE_CAMERA,
                    enterTransition = { fadeIn(animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                    popExitTransition = { fadeOut(animationSpec = tween(300)) }
                ) {
                    composable(Routes.ROUTE_CAMERA) {
                        CameraPage(
                            navController = navController,
                            sharedViewModel = sharedViewModel,
                            currentLanguage = currentLanguage,
                            onLanguageSelected = { selected -> currentLanguage = selected }
                        )
                    }
                    composable(Routes.ROUTE_ERROR_POP_UP) {
                        ErrorPopUpPage(navController)
                    }
                    composable(Routes.ROUTE_COLOR_BLOCKS) {
                        ColorBlocksPage(navController, sharedViewModel)
                    }
                    composable(Routes.ROUTE_DEBUG_PREVIEW) {
                        DebugPreviewPage(navController, sharedViewModel)
                    }
                    composable(Routes.ROUTE_PARKING_RULES) {
                        ParkingRulesPage(navController, sharedViewModel)
                    }
                }
            }
        }
    }

    /**
     * Composable for the camera page.
     * Handles image capture, debug mode, and language selection.
     *
     * @param navController Navigation controller for route changes.
     * @param sharedViewModel Shared view model for app state.
     * @param currentLanguage Currently selected language.
     * @param onLanguageSelected Callback for language selection.
     */
    @Composable
    private fun CameraPage(
        navController: NavHostController,
        sharedViewModel: AppViewModel,
        currentLanguage: Languages,
        onLanguageSelected: (Languages) -> Unit
    ) {
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
                        },
                        onError = {
                            sharedViewModel.setProcessing(false)
                            navController.navigate(Routes.ROUTE_ERROR_POP_UP)
                        }
                    )
                },
                selectedLanguage = currentLanguage,
                onLanguageSelected = onLanguageSelected
            )
            if (isProcessing) {
                LoadingScreen(true)
            }
        }
    }

    /**
     * Composable for the error dialog page.
     * Displays an error dialog and handles dismiss action.
     *
     * @param navController Navigation controller for route changes.
     */
    @Composable
    private fun ErrorPopUpPage(navController: NavHostController) {
        ErrorDialog(
            onDismiss = { navController.popBackStack() }
        )
    }

    /**
     * Composable for the color blocks preview page.
     * Shows detected color blocks and handles navigation.
     *
     * @param navController Navigation controller for route changes.
     * @param sharedViewModel Shared view model for app state.
     */
    @Composable
    private fun ColorBlocksPage(
        navController: NavHostController,
        sharedViewModel: AppViewModel
    ) {
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

    /**
     * Composable for the debug preview page.
     * Shows debug information and handles navigation.
     *
     * @param navController Navigation controller for route changes.
     * @param sharedViewModel Shared view model for app state.
     */
    @Composable
    private fun DebugPreviewPage(
        navController: NavHostController,
        sharedViewModel: AppViewModel
    ) {
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

    /**
     * Composable for the parking rules page.
     * Displays parking rules based on detected blocks.
     *
     * @param navController Navigation controller for route changes.
     * @param sharedViewModel Shared view model for app state.
     */
    @Composable
    private fun ParkingRulesPage(
        navController: NavHostController,
        sharedViewModel: AppViewModel
    ) {
        val blocksInfos by sharedViewModel.blockInfos
        ParkingRulePage(
            rules = checkIfAllowedToPark(blocksInfos),
            onBack = { navController.popBackStack() },
        )
    }
}
