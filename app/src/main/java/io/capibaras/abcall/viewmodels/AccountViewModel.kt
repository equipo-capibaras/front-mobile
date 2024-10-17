package io.capibaras.abcall.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.R
import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.database.models.User
import io.capibaras.abcall.data.repositories.UsersRepository
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import kotlinx.coroutines.launch

class AccountViewModel(
    private val logoutManager: LogoutManager,
    private val usersRepository: UsersRepository
) : ViewModel() {
    var user by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)

    var errorUIState by mutableStateOf<ErrorUIState>(ErrorUIState.NoError)
        private set

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            try {
                val response: User = usersRepository.getUserInfo()
                user = response
            } catch (e: Exception) {
                errorUIState = if (e.message != null) {
                    ErrorUIState.Error(message = e.message.toString())
                } else {
                    ErrorUIState.Error(R.string.error_getting_user_info)
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutManager.logout()
        }
    }

    fun clearErrorUIState() {
        errorUIState = ErrorUIState.NoError
    }

}
