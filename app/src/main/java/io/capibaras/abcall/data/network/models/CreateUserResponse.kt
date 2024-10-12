package io.capibaras.abcall.data.network.models

data class CreateUserResponse(
    val id: String,
    val clientId: String,
    val name: String,
    val email: String,
)