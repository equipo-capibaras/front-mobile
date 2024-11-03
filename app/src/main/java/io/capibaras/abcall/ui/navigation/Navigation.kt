package io.capibaras.abcall.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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

object Routes {
    const val HOME = "home"
    const val SIGN_UP = "signup"
    const val LOGIN = "login"
    const val ACCOUNT = "account"
    const val CREATE_INCIDENT = "create-incident"
}

object TopBarTitles {
    private val titlesMap = mapOf(
        Routes.HOME to R.string.requests_title,
        Routes.ACCOUNT to R.string.account_title,
        Routes.CREATE_INCIDENT to R.string.create_incident
    )

    fun getTitleForRoute(route: String?): Int? {
        return titlesMap[route]
    }
}

@Composable
fun CustomScaffold(
    viewModel: NavigationViewModel,
    currentRoute: String?,
    snackbarHostState: SnackbarHostState,
    navBackStackEntry: State<NavBackStackEntry?>,
    navController: NavHostController,
    isUserLoggedIn: Boolean
) {
    val titleResId = TopBarTitles.getTitleForRoute(currentRoute)
    val topBarTitle = titleResId?.let { stringResource(it) } ?: ""
    val showBackButton = currentRoute == Routes.CREATE_INCIDENT
    Scaffold(
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { ScaffoldTopBar(navBackStackEntry, topBarTitle, showBackButton, navController) },
        bottomBar = { ScaffoldBottomBar(navBackStackEntry, navController) },
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ScaffoldNavHost(
                navController = navController,
                isUserLoggedIn = isUserLoggedIn,
                paddingValues = paddingValues
            )

            if (viewModel.redirectToLogin) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
                viewModel.redirectToLogin = false
            }

        }
    }
}

fun shouldShowBar(navBackStackEntry: State<NavBackStackEntry?>): Boolean {
    val currentRoute = navBackStackEntry.value?.destination?.route
    return currentRoute !in listOf(Routes.LOGIN, Routes.SIGN_UP)
}

@Composable
fun ScaffoldTopBar(
    navBackStackEntry: State<NavBackStackEntry?>,
    topBarTitle: String,
    showBackButton: Boolean,
    navController: NavHostController
) {
    if (shouldShowBar(navBackStackEntry)) {
        TopBar(
            title = topBarTitle,
            showBackButton = showBackButton,
            onBackClick = { navController.popBackStack() }
        )
    }
}

@Composable
fun ScaffoldBottomBar(
    navBackStackEntry: State<NavBackStackEntry?>,
    navController: NavHostController
) {
    if (shouldShowBar(navBackStackEntry)) {
        BottomNavBar(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldNavHost(
    navController: NavHostController,
    isUserLoggedIn: Boolean,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) Routes.HOME else Routes.LOGIN,
        modifier = Modifier
            .padding(paddingValues)
    ) {
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.SIGN_UP) { SignUpScreen(navController) }
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.ACCOUNT) { AccountScreen() }
        composable(Routes.CREATE_INCIDENT) { CreateIncidentScreen(navController) }
        composable("${Routes.CREATE_INCIDENT}/{id}") { IncidentDetailScreen() }
    }
}
