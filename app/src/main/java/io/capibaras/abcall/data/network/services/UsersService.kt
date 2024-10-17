package io.capibaras.abcall.data.network.services

import io.capibaras.abcall.data.database.models.User
import io.capibaras.abcall.data.network.models.CreateUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UsersService {
    @POST("users")
    suspend fun createUser(@Body createUserRequest: CreateUserRequest): Response<User>

    @GET("users/me")
    suspend fun getUserInfo(): Response<User>
}
