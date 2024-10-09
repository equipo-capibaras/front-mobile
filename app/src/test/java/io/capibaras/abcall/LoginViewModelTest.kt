package io.capibaras.abcall

import io.capibaras.abcall.viewmodels.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel()
    }

    @Test
    fun `test empty fields return false`() {
        val isValid = viewModel.validateFields()

        assertFalse(isValid)
        assertEquals("Este campo es obligatorio", viewModel.emailError)
        assertEquals("Este campo es obligatorio", viewModel.passwordError)
    }

    @Test
    fun `test valid fields return true`() {

        viewModel.email = "johndoe@gmail.com"
        viewModel.password = "password123"

        val isValid = viewModel.validateFields()

        assertTrue(isValid)
        assertEquals("", viewModel.emailError)
        assertEquals("", viewModel.passwordError)
    }
}
