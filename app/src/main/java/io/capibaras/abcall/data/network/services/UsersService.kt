package io.capibaras.abcall.data.network.services

import io.capibaras.abcall.data.network.models.CreateUserRequest
import io.capibaras.abcall.data.network.models.CreateUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

fun interface UsersService {
    @POST("users")
    suspend fun createUser(@Body createUserRequest: CreateUserRequest): Response<CreateUserResponse>
}
