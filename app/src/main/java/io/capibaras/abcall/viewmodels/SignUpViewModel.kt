package io.capibaras.abcall.viewmodels

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.R
import io.capibaras.abcall.data.repositories.CompanyRepository
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.ValidationUIState
import kotlinx.coroutines.launch
import java.io.IOException

class SignUpViewModel(private val companyRepository: CompanyRepository) : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var company by mutableStateOf("")

    var companies by mutableStateOf<List<String>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
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

    init {
        getCompanies()
    }

    private fun validateField(
        value: String,
        setValidationState: (ValidationUIState) -> Unit,
        additionalCheck: ((String) -> Boolean)? = null,
        invalidErrorMessage: Int? = null
    ): Boolean {
        return when {
            value.isBlank() -> {
                setValidationState(ValidationUIState.Error(R.string.form_required))
                false
            }

            additionalCheck != null && !additionalCheck(value) && invalidErrorMessage != null -> {
                setValidationState(
                    ValidationUIState.Error(
                        invalidErrorMessage
                    )
                )
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
            company,
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

    private fun getCompanies(forceUpdate: Boolean = false) {
        viewModelScope.launch {
            isLoading = true
            try {
                val companyList = companyRepository.getCompanies(forceUpdate)
                companies = companyList.map { it.name }
            } catch (e: IOException) {
                errorUIState = ErrorUIState.Error(R.string.error_network)
            } catch (e: Exception) {
                errorUIState = ErrorUIState.Error(R.string.error_get_companies)
            } finally {
                isLoading = false
            }
        }
    }

    fun clearErrorUIState() {
        errorUIState = ErrorUIState.NoError
    }

}
