package io.capibaras.abcall.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.capibaras.abcall.ui.components.CustomSnackbarVisuals
import io.capibaras.abcall.ui.components.SnackbarState
import io.capibaras.abcall.ui.theme.LocalCustomColors
import io.capibaras.abcall.ui.views.HomeScreen
import io.capibaras.abcall.ui.views.LoginScreen
import io.capibaras.abcall.ui.views.SignUpScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    val customColors = LocalCustomColors.current

                    val snackbarState =
                        (snackbarData.visuals as? CustomSnackbarVisuals)?.state
                            ?: SnackbarState.SUCCESS

                    android.util.Log.d("snackbarState", snackbarState.toString())

                    val backgroundColor = when (snackbarState) {
                        SnackbarState.ERROR -> MaterialTheme.colorScheme.error
                        SnackbarState.SUCCESS -> customColors.success
                        SnackbarState.INFO -> MaterialTheme.colorScheme.primary
                    }

                    val contentColor = when (snackbarState) {
                        SnackbarState.ERROR -> MaterialTheme.colorScheme.onError
                        SnackbarState.SUCCESS -> customColors.onSuccess
                        SnackbarState.INFO -> MaterialTheme.colorScheme.onPrimary
                    }

                    val dismissActionColor = when (snackbarState) {
                        SnackbarState.ERROR -> MaterialTheme.colorScheme.onError
                        SnackbarState.SUCCESS -> customColors.onSuccess
                        SnackbarState.INFO -> MaterialTheme.colorScheme.onPrimary
                    }

                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = backgroundColor,
                        contentColor = contentColor,
                        dismissActionContentColor = dismissActionColor
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                LoginScreen(navController, snackbarHostState)
            }
            composable("signup") {
                SignUpScreen(navController)
            }
            composable("home") {
                HomeScreen()
            }
        }
    }
}
