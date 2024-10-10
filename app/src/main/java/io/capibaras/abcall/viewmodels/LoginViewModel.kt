package io.capibaras.abcall.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.R
import io.capibaras.abcall.data.TokenManager
import io.capibaras.abcall.data.network.models.LoginResponseJson
import io.capibaras.abcall.data.repositories.AuthRepository
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.ValidationUIState
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class LoginViewModel(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading: Boolean = false

    var errorUIState by mutableStateOf<ErrorUIState>(ErrorUIState.NoError)
        private set
    var emailValidationState by mutableStateOf<ValidationUIState>(ValidationUIState.NoError)
        private set
    var passwordValidationState by mutableStateOf<ValidationUIState>(ValidationUIState.NoError)
        private set

    private fun validateField(
        value: String,
        validationStateSetter: (ValidationUIState) -> Unit,
    ): Boolean {
        return when {
            value.isBlank() -> {
                validationStateSetter(ValidationUIState.Error(R.string.form_required))
                false
            }

            else -> {
                validationStateSetter(ValidationUIState.NoError)
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
                val response: Response<LoginResponseJson> = authRepository.login(email, password)

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        tokenManager.saveAuthToken(loginResponse.token)
                        errorUIState = ErrorUIState.NoError
                        onSuccess(loginResponse.token)
                    } ?: run {
                        errorUIState = ErrorUIState.Error(R.string.error_authenticate)
                    }
                } else {
                    errorUIState = if (response.code() == 401) {
                        ErrorUIState.Error(R.string.error_incorrect_credentials)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            try {
                                val jsonObject = JSONObject(errorBody)
                                jsonObject.getString("message")
                            } catch (e: Exception) {
                                ErrorUIState.Error(R.string.error_authenticate)
                            }
                        } else {
                            ErrorUIState.Error(R.string.error_authenticate)
                        }
                        ErrorUIState.Error(R.string.error_authenticate)
                    }
                }
            } catch (e: Exception) {
                errorUIState = ErrorUIState.Error(R.string.error_network)
            } finally {
                isLoading = false
            }
        }
    }

    fun clearErrorUIState() {
        errorUIState = ErrorUIState.NoError
    }

}
