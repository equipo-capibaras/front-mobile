package io.capibaras.abcall.data.network.services

import io.capibaras.abcall.data.network.models.LoginRequestJson
import io.capibaras.abcall.data.network.models.LoginResponseJson
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

fun interface AuthService {
    @POST("auth/employee")
    suspend fun login(@Body loginRequest: LoginRequestJson): Response<LoginResponseJson>
}
