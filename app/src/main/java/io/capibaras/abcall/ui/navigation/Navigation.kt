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
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.components.BottomNavBar
import io.capibaras.abcall.ui.components.CustomSnackbarHost
import io.capibaras.abcall.ui.components.HandleErrorState
import io.capibaras.abcall.ui.components.HandleSuccessState
import io.capibaras.abcall.ui.components.TopBar
import io.capibaras.abcall.ui.util.StateMediator
import io.capibaras.abcall.ui.viewmodels.NavigationViewModel
import io.capibaras.abcall.ui.views.AccountScreen
import io.capibaras.abcall.ui.views.CreateIncidentScreen
import io.capibaras.abcall.ui.views.FullScreenLoading
import io.capibaras.abcall.ui.views.HomeScreen
import io.capibaras.abcall.ui.views.IncidentDetailScreen
import io.capibaras.abcall.ui.views.LoginScreen
import io.capibaras.abcall.ui.views.SignUpScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun Navigation(
    viewModel: NavigationViewModel = koinViewModel(),
    stateMediator: StateMediator = koinInject(),
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val isSessionChecked = viewModel.isSessionChecked
    val isUserLoggedIn = viewModel.isUserLoggedIn
    val snackbarHostState = remember { SnackbarHostState() }

    if (isSessionChecked) {
        HandleErrorState(
            errorUIState = stateMediator.errorUIState,
            snackbarHostState = snackbarHostState,
            onClearError = { stateMediator.clearErrorUIState() }
        )

        HandleSuccessState(
            successUIState = stateMediator.successUIState,
            snackbarHostState = snackbarHostState,
            onClearSuccess = { stateMediator.clearSuccessUIState() }
        )

        FullScreenLoading(isLoading = stateMediator.isLoading)

        val currentRoute = navBackStackEntry.value?.destination?.route

        CustomScaffold(
            viewModel,
            currentRoute,
            snackbarHostState,
            navBackStackEntry,
            navController,
            isUserLoggedIn
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomScaffold(
    viewModel: NavigationViewModel,
    currentRoute: String?,
    snackbarHostState: SnackbarHostState,
    navBackStackEntry: State<NavBackStackEntry?>,
    navController: NavHostController,
    isUserLoggedIn: Boolean
) {
    val topBarTitle = when (currentRoute) {
        "home" -> stringResource(R.string.requests_title)
        "account" -> stringResource(R.string.account_title)
        "create-incident" -> stringResource(R.string.create_incident)
        else -> ""
    }
    val showBackButton = currentRoute == "create-incident"
    Scaffold(
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (navBackStackEntry.value?.destination?.route !in listOf(
                    "login",
                    "signup"
                )
            ) {
                TopBar(
                    title = topBarTitle,
                    showBackButton = showBackButton,
                    onBackClick = { navController.popBackStack() }
                )
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
                    LoginScreen(navController)
                }
                composable("signup") {
                    SignUpScreen(navController)
                }
                composable("home") {
                    HomeScreen(navController)
                }
                composable("account") {
                    AccountScreen()
                }
                composable("create-incident") {
                    CreateIncidentScreen(navController)
                }
                composable("create-incident/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    if (id != null) {
                        IncidentDetailScreen(id)
                    }
                }
            }

            if (viewModel.redirectToLogin) {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
                viewModel.redirectToLogin = false
            }

        }
    }
}
