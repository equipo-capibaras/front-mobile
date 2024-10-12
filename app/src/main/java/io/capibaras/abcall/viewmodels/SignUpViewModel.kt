package io.capibaras.abcall.viewmodels

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.capibaras.abcall.R
import io.capibaras.abcall.data.database.models.Company
import io.capibaras.abcall.data.network.models.CreateUserResponse
import io.capibaras.abcall.data.repositories.CompanyRepository
import io.capibaras.abcall.data.repositories.UsersRepository
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.SuccessUIState
import io.capibaras.abcall.ui.viewmodels.ValidationUIState
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class SignUpViewModel(
    private val companyRepository: CompanyRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var company by mutableStateOf("")

    var companies by mutableStateOf<List<Company>>(emptyList())
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
    var successUIState by mutableStateOf<SuccessUIState>(SuccessUIState.NoSuccess)
        private set

    init {
        getCompanies()
    }

    private fun validateField(
        value: String,
        setValidationState: (ValidationUIState) -> Unit,
        checks: List<Pair<() -> Boolean, Int>> = emptyList()
    ): Boolean {
        if (value.isBlank()) {
            setValidationState(ValidationUIState.Error(R.string.form_required))
            return false
        }

        checks.forEach { (check, errorMessageId) ->
            if (!check()) {
                setValidationState(ValidationUIState.Error(errorMessageId))
                return false
            }
        }

        setValidationState(ValidationUIState.NoError)
        return true
    }

    fun validateFields(): Boolean {
        var isValid = true

        isValid = validateField(
            name,
            { nameValidationState = it },
            checks = listOf(
                { name.length <= 60 } to R.string.form_60_length
            )
        ) && isValid

        isValid = validateField(
            email,
            { emailValidationState = it },
            checks = listOf(
                { email.length <= 60 } to R.string.form_60_length,
                { Patterns.EMAIL_ADDRESS.matcher(email).matches() } to R.string.form_invalid_email
            )
        ) && isValid

        isValid = validateField(
            company,
            { companyValidationState = it },
            checks = listOf(
                { companies.any { it.name == company } } to R.string.form_company_doesnt_exist
            )
        ) && isValid

        isValid = validateField(
            password,
            { passwordValidationState = it },
            checks = listOf(
                { password.length >= 8 } to R.string.form_password_length
            )
        ) && isValid

        isValid = validateField(
            confirmPassword,
            { confirmPasswordValidationState = it },
            checks = listOf(
                { confirmPassword == password } to R.string.form_confirm_password_invalid
            )
        ) && isValid

        return isValid
    }

    private fun getCompanies(forceUpdate: Boolean = false) {
        viewModelScope.launch {
            isLoading = true
            try {
                companies = companyRepository.getCompanies(forceUpdate)
            } catch (e: IOException) {
                errorUIState = ErrorUIState.Error(R.string.error_network)
            } catch (e: Exception) {
                errorUIState = ErrorUIState.Error(R.string.error_get_companies)
            } finally {
                isLoading = false
            }
        }
    }

    fun createUser(onSuccess: () -> Unit) {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            try {
                val companyId = companies.find { it.name == company }!!.id
                val response: Response<CreateUserResponse> =
                    usersRepository.createUser(companyId, name, email, password)

                if (response.isSuccessful) {
                    response.body()?.let {
                        errorUIState = ErrorUIState.NoError
                        successUIState = SuccessUIState.Success(R.string.success_create_user)
                        onSuccess()
                    }
                } else {
                    errorUIState = if (response.code() == 409) {
                        ErrorUIState.Error(R.string.error_email_exist)
                    } else {
                        val errorBody = response.errorBody()!!.string()
                        val jsonObject = JSONObject(errorBody)
                        val message = jsonObject.getString("message")

                        ErrorUIState.Error(message = message)
                    }
                }
            } catch (e: IOException) {
                errorUIState = ErrorUIState.Error(R.string.error_network)
            } catch (e: Exception) {
                errorUIState = ErrorUIState.Error(R.string.error_create_user)
            } finally {
                isLoading = false
            }
        }
    }

    fun clearErrorUIState() {
        errorUIState = ErrorUIState.NoError
    }


}
