package io.capibaras.abcall.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.theme.LocalCustomColors
import io.capibaras.abcall.ui.theme.pillText

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val navigationBarItemColors = getNavigationBarItemColors()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.tertiary,
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.requests),
                    contentDescription = stringResource(R.string.navbar_requests)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.navbar_requests),
                    style = pillText.copy(color = if (currentRoute == "home") navigationBarItemColors.selectedTextColor else navigationBarItemColors.unselectedTextColor)
                )
            },
            colors = navigationBarItemColors
        )

        NavigationBarItem(
            selected = currentRoute == "account",
            onClick = { navController.navigate("account") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = stringResource(R.string.navbar_account)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.navbar_account),
                    style = pillText.copy(color = if (currentRoute == "account") navigationBarItemColors.selectedTextColor else navigationBarItemColors.unselectedTextColor)
                )
            },
            colors = navigationBarItemColors
        )
    }
}

@Composable
fun getNavigationBarItemColors(): NavigationBarItemColors {
    return NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onBackground,
        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
        selectedTextColor = MaterialTheme.colorScheme.onBackground,
        unselectedTextColor = MaterialTheme.colorScheme.onBackground,
        indicatorColor = LocalCustomColors.current.pill
    )
}

