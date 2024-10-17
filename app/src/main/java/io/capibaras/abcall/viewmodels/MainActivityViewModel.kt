package io.capibaras.abcall.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.R
import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.repositories.UsersRepository
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.SuccessUIState
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
    var isLoggedOut by mutableStateOf(false)
        private set
    var isManualLogout by mutableStateOf(false)
        private set
    var errorUIState by mutableStateOf<ErrorUIState>(ErrorUIState.NoError)
        private set
    var successUIState by mutableStateOf<SuccessUIState>(SuccessUIState.NoSuccess)
        private set

    init {
        checkUserSession()
        viewModelScope.launch {
            logoutManager.expiredToken.collect { expired ->
                if (expired) {
                    errorUIState = ErrorUIState.Error(R.string.expired_token)
                } else {
                    errorUIState = ErrorUIState.NoError

                }

            }
        }

        viewModelScope.launch {
            logoutManager.isManualLogout.collect { manual ->
                isManualLogout = manual
                checkLogoutStatus()
            }
        }

        viewModelScope.launch {
            logoutManager.logoutEvent.collect { loggedOut ->
                isLoggedOut = loggedOut
                if (loggedOut) {
                    deleteUserData()
                    checkLogoutStatus()
                }
            }
        }
    }

    private fun checkLogoutStatus() {
        if (isLoggedOut && isManualLogout) {
            successUIState = SuccessUIState.Success(R.string.success_logout)
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

    fun clearErrorUIState() {
        errorUIState = ErrorUIState.NoError
    }

    fun clearSuccessUIState() {
        successUIState = SuccessUIState.NoSuccess
    }
}

