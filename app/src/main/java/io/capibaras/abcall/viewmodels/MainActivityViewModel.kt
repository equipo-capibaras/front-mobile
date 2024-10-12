package io.capibaras.abcall.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.capibaras.abcall.data.TokenManager

class MainActivityViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val tag = "MainActivityViewModel"
    var isSessionChecked by mutableStateOf(false)
        private set
    var isUserLoggedIn by mutableStateOf(false)
        private set

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        try {
            val token = tokenManager.getAuthToken()
            isUserLoggedIn = !token.isNullOrEmpty()
        } catch (e: Exception) {
            isUserLoggedIn = false
        } finally {
            isSessionChecked = true
        }
    }
}

