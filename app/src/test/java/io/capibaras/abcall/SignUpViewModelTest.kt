package io.capibaras.abcall

import android.content.Context
import io.capibaras.abcall.viewmodels.SignUpViewModel
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignUpViewModelTest {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        viewModel = SignUpViewModel()
    }

    @Test
    fun `test empty fields return false`() {
        every { mockContext.getString(R.string.form_required) } returns "Campo requerido"
        every { mockContext.getString(R.string.form_invalid_email) } returns "Correo no válido"
        every { mockContext.getString(R.string.form_password_length) } returns "Contraseña debe tener al menos 8 caracteres"
        every { mockContext.getString(R.string.form_confirm_password_invalid) } returns "Las contraseñas no coinciden"

        val isValid = viewModel.validateFields(mockContext)

        assertFalse(isValid)
        assertEquals("Campo requerido", viewModel.nameError)
        assertEquals("Campo requerido", viewModel.emailError)
        assertEquals("Campo requerido", viewModel.companyError)
        assertEquals("Campo requerido", viewModel.passwordError)
        assertEquals("Campo requerido", viewModel.confirmPasswordError)
    }

    @Test
    fun `test invalid email returns false`() {
        every { mockContext.getString(R.string.form_required) } returns "Campo requerido"
        every { mockContext.getString(R.string.form_invalid_email) } returns "Correo no válido"

        viewModel.email = "invalid-email"

        val isValid = viewModel.validateFields(mockContext)

        assertFalse(isValid)
        assertEquals("Correo no válido", viewModel.emailError)
    }

    @Test
    fun `test password too short returns false`() {
        every { mockContext.getString(R.string.form_required) } returns "Campo requerido"
        every { mockContext.getString(R.string.form_password_length) } returns "Contraseña debe tener al menos 8 caracteres"

        viewModel.password = "short"
        viewModel.confirmPassword = "short"

        val isValid = viewModel.validateFields(mockContext)

        assertFalse(isValid)
        assertEquals("Contraseña debe tener al menos 8 caracteres", viewModel.passwordError)
    }

    @Test
    fun `test passwords do not match returns false`() {
        every { mockContext.getString(R.string.form_required) } returns "Campo requerido"
        every { mockContext.getString(R.string.form_confirm_password_invalid) } returns "Las contraseñas no coinciden"

        viewModel.password = "password123"
        viewModel.confirmPassword = "password456"

        val isValid = viewModel.validateFields(mockContext)

        assertFalse(isValid)
        assertEquals("Las contraseñas no coinciden", viewModel.confirmPasswordError)
    }


    @Test
    fun `test valid fields return true`() {
        every { mockContext.getString(R.string.form_required) } returns "Campo requerido"
        every { mockContext.getString(R.string.form_invalid_email) } returns "Correo no válido"
        every { mockContext.getString(R.string.form_password_length) } returns "Contraseña debe tener al menos 8 caracteres"
        every { mockContext.getString(R.string.form_confirm_password_invalid) } returns "Las contraseñas no coinciden"

        viewModel.name = "John Doe"
        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"
        viewModel.selectedText = "Empresa XYZ"

        val isValid = viewModel.validateFields(mockContext)

        assertTrue(isValid)
        assertEquals("", viewModel.nameError)
        assertEquals("", viewModel.emailError)
        assertEquals("", viewModel.companyError)
        assertEquals("", viewModel.passwordError)
        assertEquals("", viewModel.confirmPasswordError)
    }
}
