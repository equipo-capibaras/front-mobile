package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.network.models.LoginRequest
import io.capibaras.abcall.data.network.models.LoginResponse
import io.capibaras.abcall.data.network.services.AuthService
import retrofit2.Response

class AuthRepository(private val authService: AuthService) {
    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return authService.login(LoginRequest(username, password))
    }
}
