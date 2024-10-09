package io.capibaras.abcall

import io.capibaras.abcall.viewmodels.SignUpViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignUpViewModelTest {

    private lateinit var viewModel: SignUpViewModel

    private val requiredMsg = "Este campo es obligatorio"
    private val invalidEmailMsg = "Correo no válido"
    private val invalidPasswordMsg = "La contraseña debe tener al menos 8 caracteres"
    private val invalidConfirmPasswordMsg = "Las contraseñas no coinciden"

    @Before
    fun setUp() {
        viewModel = SignUpViewModel()
    }

    @Test
    fun `test empty fields return false`() {
        val isValid = viewModel.validateFields(
            requiredMsg,
            invalidEmailMsg,
            invalidPasswordMsg,
            invalidConfirmPasswordMsg
        )

        assertFalse(isValid)
        assertEquals("Este campo es obligatorio", viewModel.nameError)
        assertEquals("Este campo es obligatorio", viewModel.emailError)
        assertEquals("Este campo es obligatorio", viewModel.companyError)
        assertEquals("Este campo es obligatorio", viewModel.passwordError)
        assertEquals("Este campo es obligatorio", viewModel.confirmPasswordError)
    }

    @Test
    fun `test invalid email returns false`() {
        viewModel.email = "invalid-email"

        val isValid = viewModel.validateFields(
            requiredMsg,
            invalidEmailMsg,
            invalidPasswordMsg,
            invalidConfirmPasswordMsg
        )

        assertFalse(isValid)
        assertEquals("Correo no válido", viewModel.emailError)
    }

    @Test
    fun `test password too short returns false`() {
        viewModel.password = "short"
        viewModel.confirmPassword = "short"

        val isValid = viewModel.validateFields(
            requiredMsg,
            invalidEmailMsg,
            invalidPasswordMsg,
            invalidConfirmPasswordMsg
        )

        assertFalse(isValid)
        assertEquals("La contraseña debe tener al menos 8 caracteres", viewModel.passwordError)
    }

    @Test
    fun `test passwords do not match returns false`() {
        viewModel.password = "password123"
        viewModel.confirmPassword = "password456"

        val isValid = viewModel.validateFields(
            requiredMsg,
            invalidEmailMsg,
            invalidPasswordMsg,
            invalidConfirmPasswordMsg
        )

        assertFalse(isValid)
        assertEquals("Las contraseñas no coinciden", viewModel.confirmPasswordError)
    }


    @Test
    fun `test valid fields return true`() {
        viewModel.name = "John Doe"
        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"
        viewModel.selectedText = "Empresa XYZ"

        val isValid = viewModel.validateFields(
            requiredMsg,
            invalidEmailMsg,
            invalidPasswordMsg,
            invalidConfirmPasswordMsg
        )

        assertTrue(isValid)
        assertEquals("", viewModel.nameError)
        assertEquals("", viewModel.emailError)
        assertEquals("", viewModel.companyError)
        assertEquals("", viewModel.passwordError)
        assertEquals("", viewModel.confirmPasswordError)
    }
}
