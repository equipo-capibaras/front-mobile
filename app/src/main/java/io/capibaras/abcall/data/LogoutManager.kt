package io.capibaras.abcall.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class LogoutManager {
    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent = _logoutEvent.asSharedFlow()
    private val _expiredToken = MutableStateFlow(false)
    val expiredToken = _expiredToken.asStateFlow()
    private val _isManualLogout = MutableStateFlow(false)
    val isManualLogout = _isManualLogout.asSharedFlow()

    suspend fun logout(isExpiredToken: Boolean = false, isManual: Boolean = false) {
        _expiredToken.emit(isExpiredToken)
        _logoutEvent.emit(true)
        _isManualLogout.emit(isManual)
    }

    suspend fun resetLogoutState() {
        _expiredToken.emit(false)
        _logoutEvent.emit(false)
        _isManualLogout.emit(false)
    }
}