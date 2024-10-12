package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.network.models.CreateUserRequest
import io.capibaras.abcall.data.network.models.CreateUserResponse
import io.capibaras.abcall.data.network.services.UsersService
import retrofit2.Response

class UsersRepository(private val createUsersService: UsersService) {
    suspend fun createUser(
        clientId: String,
        name: String,
        email: String,
        password: String
    ): Response<CreateUserResponse> {
        return createUsersService.createUser(CreateUserRequest(clientId, name, email, password))
    }
}
