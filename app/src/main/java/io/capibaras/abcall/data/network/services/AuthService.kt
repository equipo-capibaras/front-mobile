package io.capibaras.abcall.data.network.services

import io.capibaras.abcall.data.network.models.LoginRequest
import io.capibaras.abcall.data.network.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

fun interface AuthService {
    @POST("auth/user")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}
