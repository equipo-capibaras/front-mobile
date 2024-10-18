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
    private val sharedViewModel: SharedViewModel,
    private val logoutManager: LogoutManager,
    private val usersRepository: UsersRepository
) : ViewModel() {
    var user by mutableStateOf<User?>(null)
        private set

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        if (sharedViewModel.isLoading) return
        sharedViewModel.setLoadingState(true)
        viewModelScope.launch {
            try {
                val response: User = usersRepository.getUserInfo()
                user = response
            } catch (e: Exception) {
                if (e.message != null) {
                    sharedViewModel.setErrorState(ErrorUIState.Error(message = e.message.toString()))
                } else {
                    sharedViewModel.setErrorState(ErrorUIState.Error(R.string.error_getting_user_info))
                }
            } finally {
                sharedViewModel.setLoadingState(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutManager.logout(isManual = true)
        }
    }
    

}
