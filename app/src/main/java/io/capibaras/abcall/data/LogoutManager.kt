package io.capibaras.abcall.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LogoutManager {
    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent = _logoutEvent.asStateFlow()
    private val _expiredToken = MutableStateFlow(false)
    val expiredToken = _expiredToken.asStateFlow()
    private val _isManualLogout = MutableStateFlow(false)
    val isManualLogout = _isManualLogout.asStateFlow()

    fun logout(isExpiredToken: Boolean = false, isManual: Boolean = false) {
        _expiredToken.value = isExpiredToken
        _logoutEvent.value = true
        _isManualLogout.value = isManual
    }

    fun resetLogoutState() {
        _expiredToken.value = false
        _logoutEvent.value = false
        _isManualLogout.value = false
    }
}