package io.capibaras.abcall.data.network.models

data class CreateUserRequest(
    val clientId: String,
    val name: String,
    val email: String,
    val password: String
)