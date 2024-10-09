package io.capibaras.abcall.viewmodels

import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.capibaras.abcall.R

class SignUpViewModel : ViewModel() {
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

    fun validateFields(context: Context): Boolean {
        var isValid = true

        isValid = validateField(
            name,
            { nameError = it },
            context.getString(R.string.form_required)
        ) && isValid

        isValid = validateField(
            email,
            { emailError = it },
            context.getString(R.string.form_required),
            context.getString(R.string.form_invalid_email)
        ) {
            Patterns.EMAIL_ADDRESS.matcher(it).matches()
        } && isValid

        isValid = validateField(
            selectedText,
            { companyError = it },
            context.getString(R.string.form_required)
        ) && isValid

        isValid = validateField(
            password,
            { passwordError = it },
            context.getString(R.string.form_required),
            context.getString(R.string.form_password_length)
        ) {
            it.length >= 8
        } && isValid

        isValid = validateField(
            confirmPassword,
            { confirmPasswordError = it },
            context.getString(R.string.form_required),
            context.getString(R.string.form_confirm_password_invalid)
        ) {
            it == password
        } && isValid

        return isValid
    }

}
