package io.capibaras.abcall.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LogoutState(
    val isLoggedOut: Boolean = false,
    val isExpiredToken: Boolean = false,
    val isManualLogout: Boolean = false
)

class LogoutManager {
    private val _logoutState = MutableStateFlow(LogoutState())
    val logoutState: StateFlow<LogoutState> = _logoutState

    fun logout(isExpiredToken: Boolean = false, isManual: Boolean = false) {
        _logoutState.value =
            LogoutState(
                isLoggedOut = true,
                isManualLogout = isManual,
                isExpiredToken = isExpiredToken
            )

    }

    fun resetLogoutState() {
        _logoutState.value =
            LogoutState(
                isLoggedOut = false,
                isManualLogout = false,
                isExpiredToken = false
            )
    }
}