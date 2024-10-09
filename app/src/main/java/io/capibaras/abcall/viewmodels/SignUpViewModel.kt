package io.capibaras.abcall.viewmodels

import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.capibaras.abcall.R

class SignUpViewModel(
    private val isEmailValid: (String) -> Boolean = { email ->
        Patterns.EMAIL_ADDRESS.matcher(
            email
        ).matches()
    }
) : ViewModel() {
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

    fun validateFields(context: Context): Boolean {
        var isValid = true

        if (name.isBlank()) {
            nameError = context.getString(R.string.form_required)
            isValid = false
        } else {
            nameError = ""
        }

        if (email.isBlank()) {
            emailError = context.getString(R.string.form_required)
            isValid = false
        } else if (!isEmailValid(email)) {
            emailError = context.getString(R.string.form_invalid_email)
            isValid = false
        } else {
            emailError = ""
        }

        if (selectedText.isBlank()) {
            companyError = context.getString(R.string.form_required)
            isValid = false
        } else {
            companyError = ""
        }

        if (password.isBlank()) {
            passwordError = context.getString(R.string.form_required)
            isValid = false
        } else if (password.length < 8) {
            passwordError = context.getString(R.string.form_password_length)
            isValid = false
        } else {
            passwordError = ""
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordError = context.getString(R.string.form_required)
            isValid = false
        } else if (confirmPassword != password) {
            confirmPasswordError = context.getString(R.string.form_confirm_password_invalid)
            isValid = false
        } else {
            confirmPasswordError = ""
        }

        return isValid
    }

}