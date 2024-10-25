package io.capibaras.abcall.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.R
import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.LogoutState
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
                isManualLogout = state.isManualLogout

                if (state.isLoggedOut) {
                    deleteUserData()
                    redirectToLogin = true
                    checkLogoutStatus(state)
                }
            }
        }
    }

    private fun checkLogoutStatus(state: LogoutState) {
        when {
            state.isExpiredToken -> {
                stateMediator.setErrorState(ErrorUIState.Error(R.string.expired_token))
            }

            !isUserLoggedIn && isManualLogout -> {
                stateMediator.setSuccessState(SuccessUIState.Success(R.string.success_logout))
            }

            else -> {
                stateMediator.setErrorState(ErrorUIState.NoError)
                stateMediator.setSuccessState(SuccessUIState.NoSuccess)
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
        isUserLoggedIn = false
        logoutManager.resetLogoutState()
    }
}

