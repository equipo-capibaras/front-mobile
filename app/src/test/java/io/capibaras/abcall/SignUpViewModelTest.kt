package io.capibaras.abcall

import io.capibaras.abcall.data.database.models.Company
import io.capibaras.abcall.data.repositories.CompanyRepository
import io.capibaras.abcall.ui.viewmodels.ErrorUIState
import io.capibaras.abcall.ui.viewmodels.ValidationUIState
import io.capibaras.abcall.viewmodels.SignUpViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    private lateinit var viewModel: SignUpViewModel

    @MockK
    private lateinit var companyRepository: CompanyRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        coEvery { companyRepository.getCompanies(any()) } returns listOf(
            Company(
                name = "Empresa XYZ",
                id = "Empresa_XYZ"
            )
        )

        viewModel = SignUpViewModel(companyRepository)
    }

    @Test
    fun `test empty fields return false`() {
        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        assertEquals(
            ValidationUIState.Error(R.string.form_required),
            viewModel.nameValidationState
        )
        assertEquals(
            ValidationUIState.Error(R.string.form_required),
            viewModel.emailValidationState
        )
        assertEquals(
            ValidationUIState.Error(R.string.form_required),
            viewModel.companyValidationState
        )
        assertEquals(
            ValidationUIState.Error(R.string.form_required),
            viewModel.passwordValidationState
        )
        assertEquals(
            ValidationUIState.Error(R.string.form_required),
            viewModel.confirmPasswordValidationState
        )
    }

    @Test
    fun `test invalid email returns false`() {
        viewModel.email = "invalid-email"

        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        assertEquals(
            ValidationUIState.Error(R.string.form_invalid_email),
            viewModel.emailValidationState
        )
    }

    @Test
    fun `test password too short returns false`() {
        viewModel.password = "short"
        viewModel.confirmPassword = "short"

        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        assertEquals(
            ValidationUIState.Error(R.string.form_password_length),
            viewModel.passwordValidationState
        )
    }

    @Test
    fun `test passwords do not match returns false`() {
        viewModel.password = "password123"
        viewModel.confirmPassword = "password456"

        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        assertEquals(
            ValidationUIState.Error(R.string.form_confirm_password_invalid),
            viewModel.confirmPasswordValidationState
        )
    }


    @Test
    fun `test valid fields return true`() {
        viewModel.name = "John Doe"
        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"
        viewModel.company = "Empresa XYZ"

        val isValid = viewModel.validateFields()

        assertTrue(isValid)
        assertEquals(ValidationUIState.NoError, viewModel.nameValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.emailValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.companyValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.passwordValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.confirmPasswordValidationState)
    }

    @Test
    fun `test getCompanies success`() = runTest {
        assertFalse(viewModel.isLoading)
        assertEquals(listOf("Empresa XYZ"), viewModel.companies.value)
    }

    @Test
    fun `test getCompanies network failure`() = runTest {
        coEvery { companyRepository.getCompanies() } throws IOException("Network error")
        viewModel = SignUpViewModel(companyRepository)

        advanceUntilIdle()

        assertEquals(ErrorUIState.Error(R.string.error_network), viewModel.errorUIState)
    }

    @Test
    fun `test clearErrorUIState`() = runTest {
        coEvery { companyRepository.getCompanies() } throws IOException("Network error")
        viewModel = SignUpViewModel(companyRepository)

        advanceUntilIdle()

        assertEquals(ErrorUIState.Error(R.string.error_network), viewModel.errorUIState)

        viewModel.clearErrorUIState()

        assertEquals(ErrorUIState.NoError, viewModel.errorUIState)
    }
}
