package io.capibaras.abcall.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var emailError by mutableStateOf("")
    var passwordError by mutableStateOf("")

    private fun validateField(
        value: String,
        setError: (String) -> Unit,
        emptyErrorMessage: String,
    ): Boolean {
        return when {
            value.isBlank() -> {
                setError(emptyErrorMessage)
                false
            }

            else -> {
                setError("")
                true
            }
        }
    }

    fun validateFields(requiredMsg: String): Boolean {
        var isValid = true

        isValid = validateField(
            email,
            { emailError = it },
            requiredMsg
        ) && isValid

        isValid = validateField(
            password,
            { passwordError = it },
            requiredMsg
        ) && isValid

        return isValid
    }

}
