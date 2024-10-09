package io.capibaras.abcall

import android.content.Context
import io.capibaras.abcall.viewmodels.LoginViewModel
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        viewModel = LoginViewModel()
    }

    @Test
    fun `test empty fields return false`() {
        every { mockContext.getString(R.string.form_required) } returns "Campo requerido"

        val isValid = viewModel.validateFields(mockContext)

        assertFalse(isValid)
        assertEquals("Campo requerido", viewModel.emailError)
        assertEquals("Campo requerido", viewModel.passwordError)
    }

    @Test
    fun `test valid fields return true`() {
        every { mockContext.getString(R.string.form_required) } returns "Campo requerido"

        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "password123"

        val isValid = viewModel.validateFields(mockContext)

        assertTrue(isValid)
        assertEquals("", viewModel.emailError)
        assertEquals("", viewModel.passwordError)
    }
}
