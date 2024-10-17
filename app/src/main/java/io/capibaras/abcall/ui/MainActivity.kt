package io.capibaras.abcall.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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

    if (isLoggedOut) {
        Navigation(false)
    } else if (isSessionChecked) {
        Navigation(isUserLoggedIn)
    }
}