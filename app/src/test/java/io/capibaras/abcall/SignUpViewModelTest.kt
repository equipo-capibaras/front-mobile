package io.capibaras.abcall

import io.capibaras.abcall.ui.viewmodels.ValidationUIState
import io.capibaras.abcall.viewmodels.SignUpViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignUpViewModelTest {

    private lateinit var viewModel: SignUpViewModel


    @Before
    fun setUp() {
        viewModel = SignUpViewModel()
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
        viewModel.selectedText = "Empresa XYZ"

        val isValid = viewModel.validateFields()

        assertTrue(isValid)
        assertEquals(ValidationUIState.NoError, viewModel.nameValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.emailValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.companyValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.passwordValidationState)
        assertEquals(ValidationUIState.NoError, viewModel.confirmPasswordValidationState)
    }
}
