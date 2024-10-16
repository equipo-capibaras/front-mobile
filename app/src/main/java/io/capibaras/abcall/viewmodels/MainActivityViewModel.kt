package io.capibaras.abcall.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.repositories.UsersRepository
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val usersRepository: UsersRepository,
    private val tokenManager: TokenManager,
    private val logoutManager: LogoutManager,
) : ViewModel() {

    var isSessionChecked by mutableStateOf(false)
        private set
    var isUserLoggedIn by mutableStateOf(false)
        private set

    init {
        checkUserSession()
        viewModelScope.launch {
            logoutManager.logoutEvent.collect {
                deleteUserData()
            }
        }
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

    private suspend fun deleteUserData() {
        tokenManager.clearAuthToken()
        usersRepository.deleteUsers()
        logoutManager.resetLogoutState()
    }
}

