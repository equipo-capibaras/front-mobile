package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.network.models.LoginRequest
import io.capibaras.abcall.data.network.models.LoginResponse
import io.capibaras.abcall.data.network.services.AuthService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthRepositoryTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var authService: AuthService

    @Before
    fun setUp() {
        authService = mockk()
        authRepository = AuthRepository(authService)
    }

    @Test
    fun `login should return success response`() = runTest {
        val username = "testuser"
        val password = "password123"
        val loginResponse = mockk<Response<LoginResponse>> {
            coEvery { isSuccessful } returns true
        }

        coEvery { authService.login(LoginRequest(username, password)) } returns loginResponse

        val result = authRepository.login(username, password)

        assertEquals(loginResponse, result)
        coVerify { authService.login(LoginRequest(username, password)) }
    }

    @Test
    fun `login should return error response`() = runTest {
        val username = "testuser"
        val password = "wrongpassword"
        val errorResponse = mockk<Response<LoginResponse>> {
            coEvery { isSuccessful } returns false
        }

        coEvery { authService.login(LoginRequest(username, password)) } returns errorResponse

        val result = authRepository.login(username, password)

        assertEquals(errorResponse, result)
        coVerify { authService.login(LoginRequest(username, password)) }
    }
}