package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.network.models.LoginRequestJson
import io.capibaras.abcall.data.network.models.LoginResponseJson
import io.capibaras.abcall.data.network.services.AuthService
import retrofit2.Response

class AuthRepository(private val authService: AuthService) {
    suspend fun login(username: String, password: String): Response<LoginResponseJson> {
        return authService.login(LoginRequestJson(username, password))
    }
}
