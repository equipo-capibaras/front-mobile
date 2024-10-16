package io.capibaras.abcall.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.components.BottomNavBar
import io.capibaras.abcall.ui.components.CustomSnackbarHost
import io.capibaras.abcall.ui.components.TopBar
import io.capibaras.abcall.ui.views.AccountScreen
import io.capibaras.abcall.ui.views.HomeScreen
import io.capibaras.abcall.ui.views.LoginScreen
import io.capibaras.abcall.ui.views.SignUpScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(isUserLoggedIn: Boolean) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry = navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry.value?.destination?.route
    val topBarTitle = when (currentRoute) {
        "home" -> stringResource(R.string.requests_title)
        "account" -> stringResource(R.string.account_title)
        else -> ""
    }

    Scaffold(
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (navBackStackEntry.value?.destination?.route !in listOf(
                    "login",
                    "signup"
                )
            ) {
                TopBar(topBarTitle)
            }
        },
        bottomBar = {
            if (navBackStackEntry.value?.destination?.route !in listOf(
                    "login",
                    "signup"
                )
            ) {
                BottomNavBar(navController)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = if (isUserLoggedIn) "home" else "login",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("login") {
                    LoginScreen(navController, snackbarHostState)
                }
                composable("signup") {
                    SignUpScreen(navController, snackbarHostState)
                }
                composable("home") {
                    HomeScreen()
                }
                composable("account") {
                    AccountScreen()
                }
            }
        }
    }
}
