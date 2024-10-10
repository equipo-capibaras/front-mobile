package io.capibaras.abcall.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.ui.navigation.Navigation
import io.capibaras.abcall.ui.theme.ABCallTheme
import org.koin.android.ext.android.getKoin


class MainActivity : ComponentActivity() {
    private var isSessionChecked = false
    private var isUserLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        splashScreen.setKeepOnScreenCondition {
            !isSessionChecked
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkUserSession()
        setContent {
            ABCallTheme {
                Navigation(isUserLoggedIn)
            }
        }
    }

    private fun checkUserSession() {
        val tokenManager = getKoin().get<TokenManager>()
        val token = tokenManager.getAuthToken()
        isUserLoggedIn = !token.isNullOrEmpty()
        isSessionChecked = true
    }
}