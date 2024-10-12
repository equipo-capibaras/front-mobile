package io.capibaras.abcall.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.R
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.network.models.LoginResponse
import io.capibaras.abcall.data.repositories.AuthRepository
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.ValidationUIState
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class LoginViewModel(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)

    var errorUIState by mutableStateOf<ErrorUIState>(ErrorUIState.NoError)
        private set
    var emailValidationState by mutableStateOf<ValidationUIState>(ValidationUIState.NoError)
        private set
    var passwordValidationState by mutableStateOf<ValidationUIState>(ValidationUIState.NoError)
        private set

    private fun validateField(
        value: String,
        setValidationState: (ValidationUIState) -> Unit,
    ): Boolean {
        return when {
            value.isBlank() -> {
                setValidationState(ValidationUIState.Error(R.string.form_required))
                false
            }

            else -> {
                setValidationState(ValidationUIState.NoError)
                true
            }
        }
    }

    fun validateFields(): Boolean {
        var isValid = true

        isValid = validateField(
            email,
            { emailValidationState = it },
        ) && isValid

        isValid = validateField(
            password,
            { passwordValidationState = it },
        ) && isValid

        return isValid
    }

    fun loginUser(onSuccess: (String) -> Unit) {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            try {
                val response: Response<LoginResponse> = authRepository.login(email, password)

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        tokenManager.saveAuthToken(loginResponse.token)
                        errorUIState = ErrorUIState.NoError
                        onSuccess(loginResponse.token)
                    }
                } else {
                    errorUIState = if (response.code() == 401) {
                        ErrorUIState.Error(R.string.error_incorrect_credentials)
                    } else {
                        val errorBody = response.errorBody()!!.string()
                        val jsonObject = JSONObject(errorBody)
                        val message = jsonObject.getString("message")
                        ErrorUIState.Error(message = message)
                    }
                }
            } catch (e: Exception) {
                errorUIState = ErrorUIState.Error(
                    if (e is IOException) R.string.error_network else R.string.error_authenticate
                )
            } finally {
                isLoading = false
            }
        }
    }

    fun clearErrorUIState() {
        errorUIState = ErrorUIState.NoError
    }

}
