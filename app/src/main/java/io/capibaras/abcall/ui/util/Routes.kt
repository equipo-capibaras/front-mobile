package io.capibaras.abcall.ui.util

import androidx.navigation.NavController
import io.capibaras.abcall.R

object Routes {
    const val HOME = "home"
    const val SIGN_UP = "signup"
    const val LOGIN = "login"
    const val ACCOUNT = "account"
    const val CREATE_INCIDENT = "create-incident"
    const val INCIDENT_DETAIL = "$CREATE_INCIDENT/{id}"

    private val routesWithBackButton = setOf(CREATE_INCIDENT, INCIDENT_DETAIL)

    fun shouldShowBackButton(route: String?): Boolean {
        return route in routesWithBackButton
    }

    fun getBackNavigationAction(route: String?, navController: NavController): () -> Unit {
        return if (route == INCIDENT_DETAIL) {
            { navController.navigate(HOME) { popUpTo(HOME) { inclusive = true } } }
        } else {
            { navController.popBackStack() }
        }
    }

    private val titlesMap = mapOf(
        HOME to R.string.requests_title,
        ACCOUNT to R.string.account_title,
        CREATE_INCIDENT to R.string.create_incident,
        INCIDENT_DETAIL to R.string.incident_detail
    )

    fun getTitleForRoute(route: String?): Int? {
        return titlesMap[route]
    }
}