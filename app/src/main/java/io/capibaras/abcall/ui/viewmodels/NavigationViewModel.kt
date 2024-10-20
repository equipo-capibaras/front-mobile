package io.capibaras.abcall.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.R
import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.repositories.UsersRepository
import io.capibaras.abcall.ui.util.StateMediator
import kotlinx.coroutines.launch

class NavigationViewModel(
    private val stateMediator: StateMediator,
    private val usersRepository: UsersRepository,
    private val tokenManager: TokenManager,
    private val logoutManager: LogoutManager,
) : ViewModel() {
    var isSessionChecked by mutableStateOf(false)
        private set
    var isUserLoggedIn by mutableStateOf(false)
        private set
    private var isManualLogout by mutableStateOf(false)
    var redirectToLogin by mutableStateOf(false)

    init {
        checkUserSession()
        viewModelScope.launch {
            logoutManager.logoutState.collect { state ->
                if (state.isExpiredToken) {
                    stateMediator.setErrorState(ErrorUIState.Error(R.string.expired_token))
                } else {
                    stateMediator.setErrorState(ErrorUIState.NoError)
                }

                isManualLogout = state.isManualLogout

                if (state.isLoggedOut) {
                    deleteUserData()
                    redirectToLogin = true
                    checkLogoutStatus()
                }
            }
        }
    }

    private fun checkLogoutStatus() {
        if (!isUserLoggedIn && isManualLogout) {
            stateMediator.setSuccessState(SuccessUIState.Success(R.string.success_logout))
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
        isUserLoggedIn = false
        logoutManager.resetLogoutState()
    }
}

