package io.capibaras.abcall.viewmodels

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SignUpViewModel() : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var selectedText by mutableStateOf("")

    var nameError by mutableStateOf("")
    var emailError by mutableStateOf("")
    var passwordError by mutableStateOf("")
    var confirmPasswordError by mutableStateOf("")
    var companyError by mutableStateOf("")

    private fun validateField(
        value: String,
        setError: (String) -> Unit,
        emptyErrorMessage: String,
        invalidErrorMessage: String? = null,
        additionalCheck: ((String) -> Boolean)? = null
    ): Boolean {
        return when {
            value.isBlank() -> {
                setError(emptyErrorMessage)
                false
            }

            additionalCheck != null && !additionalCheck(value) -> {
                invalidErrorMessage?.let { setError(it) }
                false
            }

            else -> {
                setError("")
                true
            }
        }
    }

    fun validateFields(): Boolean {
        var isValid = true
        var requiredMsg = "Este campo es obligatorio"
        var invalidEmailMsg = "Correo no válido"
        var invalidPasswordMsg = "La contraseña debe tener al menos 8 caracteres"
        var invalidConfirmPasswordMsg = "Las contraseñas no coinciden"

        isValid = validateField(
            name,
            { nameError = it },
            requiredMsg
        ) && isValid

        isValid = validateField(
            email,
            { emailError = it },
            requiredMsg,
            invalidEmailMsg
        ) {
            Patterns.EMAIL_ADDRESS.matcher(it).matches()
        } && isValid

        isValid = validateField(
            selectedText,
            { companyError = it },
            requiredMsg
        ) && isValid

        isValid = validateField(
            password,
            { passwordError = it },
            requiredMsg,
            invalidPasswordMsg
        ) {
            it.length >= 8
        } && isValid

        isValid = validateField(
            confirmPassword,
            { confirmPasswordError = it },
            requiredMsg,
            invalidConfirmPasswordMsg
        ) {
            it == password
        } && isValid

        return isValid
    }

}
