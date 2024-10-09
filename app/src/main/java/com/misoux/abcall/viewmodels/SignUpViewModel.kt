package io.capibaras.abcall.viewmodels

import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    /*    var name by mutableStateOf("")
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
            val errors = mutableMapOf<String, String>()

            if (name.isBlank()) {
                nameError = context.getString(R.string.form_required)
                errors["nameError"] = nameError
            }

            if (email.isBlank()) {
                emailError = context.getString(R.string.form_required)
                errors["emailError"] = emailError
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailError = context.getString(R.string.form_invalid_email)
                errors["emailError"] = emailError
            }

            if (selectedText.isBlank()) {
                companyError = context.getString(R.string.form_required)
                errors["companyError"] = companyError
            }

            if (password.isBlank()) {
                passwordError = context.getString(R.string.form_required)
                errors["passwordError"] = passwordError
            } else if (password.length < 8) {
                passwordError = context.getString(R.string.form_password_length)
                errors["passwordError"] = passwordError
            }

            if (confirmPassword.isBlank()) {
                confirmPasswordError = context.getString(R.string.form_required)
                errors["confirmPasswordError"] = confirmPasswordError
            } else if (confirmPassword != password) {
                confirmPasswordError = context.getString(R.string.form_confirm_password_invalid)
                errors["confirmPasswordError"] = confirmPasswordError
            }

            return errors.isEmpty()
        }*/
}