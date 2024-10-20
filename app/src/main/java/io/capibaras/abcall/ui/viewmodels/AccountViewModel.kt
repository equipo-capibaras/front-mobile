package io.capibaras.abcall.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.R
import io.capibaras.abcall.data.LogoutManager
import io.capibaras.abcall.data.database.models.User
import io.capibaras.abcall.data.repositories.UsersRepository
import io.capibaras.abcall.ui.util.StateMediator
import kotlinx.coroutines.launch
import java.io.IOException

class AccountViewModel(
    private val stateMediator: StateMediator,
    private val logoutManager: LogoutManager,
    private val usersRepository: UsersRepository
) : ViewModel() {
    var user by mutableStateOf<User?>(null)
        private set

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        if (stateMediator.isLoading) return
        stateMediator.setLoadingState(true)
        viewModelScope.launch {
            try {
                val response: User = usersRepository.getUserInfo()
                user = response
            } catch (e: IOException) {
                stateMediator.setErrorState(ErrorUIState.Error(R.string.error_network))
            } catch (e: Exception) {
                if (e.message != null) {
                    stateMediator.setErrorState(ErrorUIState.Error(message = e.message.toString()))
                } else {
                    stateMediator.setErrorState(ErrorUIState.Error(R.string.error_getting_user_info))
                }
            } finally {
                stateMediator.setLoadingState(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutManager.logout(isManual = true)
        }
    }


}
