package io.capibaras.abcall.data.network.models


data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
)

