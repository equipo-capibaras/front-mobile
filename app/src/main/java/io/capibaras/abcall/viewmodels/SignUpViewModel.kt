package io.capibaras.abcall.viewmodels

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.capibaras.abcall.R
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.ValidationUIState

class SignUpViewModel : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var selectedText by mutableStateOf("")

    var isLoading: Boolean = false

    var errorUIState by mutableStateOf<ErrorUIState>(ErrorUIState.NoError)
        private set
    var nameValidationState by mutableStateOf<ValidationUIState>(ValidationUIState.NoError)
        private set
    var companyValidationState by mutableStateOf<ValidationUIState>(ValidationUIState.NoError)
        private set
    var emailValidationState by mutableStateOf<ValidationUIState>(ValidationUIState.NoError)
        private set
    var passwordValidationState by mutableStateOf<ValidationUIState>(ValidationUIState.NoError)
        private set
    var confirmPasswordValidationState by mutableStateOf<ValidationUIState>(ValidationUIState.NoError)
        private set

    private fun validateField(
        value: String,
        validationStateSetter: (ValidationUIState) -> Unit,
        additionalCheck: ((String) -> Boolean)? = null,
        invalidErrorMessage: Int? = null
    ): Boolean {
        return when {
            value.isBlank() -> {
                validationStateSetter(ValidationUIState.Error(R.string.form_required))
                false
            }

            additionalCheck != null && !additionalCheck(value) && invalidErrorMessage != null -> {
                validationStateSetter(
                    ValidationUIState.Error(
                        invalidErrorMessage
                    )
                )
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
            name,
            { nameValidationState = it },
        ) && isValid

        isValid = validateField(
            email,
            { emailValidationState = it },
            additionalCheck = { Patterns.EMAIL_ADDRESS.matcher(it).matches() },
            invalidErrorMessage = R.string.form_invalid_email
        ) && isValid

        isValid = validateField(
            selectedText,
            { companyValidationState = it },
        ) && isValid

        isValid = validateField(
            password,
            { passwordValidationState = it },
            additionalCheck = { it.length >= 8 },
            invalidErrorMessage = R.string.form_password_length
        ) && isValid

        isValid = validateField(
            confirmPassword,
            { confirmPasswordValidationState = it },
            additionalCheck = { it == password },
            invalidErrorMessage = R.string.form_confirm_password_invalid
        ) && isValid

        return isValid
    }

}
