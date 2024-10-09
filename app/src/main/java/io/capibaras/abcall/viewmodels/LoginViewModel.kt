package io.capibaras.abcall.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.capibaras.abcall.R

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

    fun validateFields(context: Context): Boolean {
        var isValid = true

        isValid = validateField(
            email,
            { emailError = it },
            context.getString(R.string.form_required)
        ) && isValid

        isValid = validateField(
            password,
            { passwordError = it },
            context.getString(R.string.form_required)
        ) && isValid

        return isValid
    }

}
