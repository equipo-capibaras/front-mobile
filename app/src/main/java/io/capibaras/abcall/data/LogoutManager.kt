package io.capibaras.abcall.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

class LogoutManager {
    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent = _logoutEvent.asSharedFlow()

    suspend fun logout() {
        _logoutEvent.emit(true)
    }

    suspend fun resetLogoutState() {
        _logoutEvent.emit(false)
    }
}