package io.capibaras.abcall.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.capibaras.abcall.ui.components.HandleErrorState
import io.capibaras.abcall.ui.components.HandleSuccessState
import io.capibaras.abcall.ui.navigation.Navigation
import io.capibaras.abcall.ui.theme.ABCallTheme
import io.capibaras.abcall.viewmodels.MainActivityViewModel
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            false
        }

        setContent {
            ABCallTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainActivityViewModel = koinViewModel(),
) {
    val isSessionChecked = viewModel.isSessionChecked
    val isUserLoggedIn = viewModel.isUserLoggedIn
    val isLoggedOut = viewModel.isLoggedOut
    val snackbarHostState = remember { SnackbarHostState() }

    HandleSuccessState(
        successUIState = viewModel.successUIState,
        snackbarHostState = snackbarHostState,
        onClearSuccess = { viewModel.clearSuccessUIState() }
    )

    HandleErrorState(
        errorUIState = viewModel.errorUIState,
        snackbarHostState = snackbarHostState,
        onClearError = { viewModel.clearErrorUIState() }
    )

    if (isLoggedOut) {
        Navigation(false, snackbarHostState)
    } else if (isSessionChecked) {
        Navigation(isUserLoggedIn, snackbarHostState)
    }
}