package io.capibaras.abcall.data.repositories

import android.util.Log
import io.capibaras.abcall.data.database.dao.UserDAO
import io.capibaras.abcall.data.database.models.User
import io.capibaras.abcall.data.network.models.CreateUserRequest
import io.capibaras.abcall.data.network.services.UsersService
import org.json.JSONObject
import retrofit2.Response

class UsersRepository(private val usersService: UsersService, private val userDAO: UserDAO) {
    suspend fun createUser(
        clientId: String,
        name: String,
        email: String,
        password: String
    ): Response<User> {
        return usersService.createUser(CreateUserRequest(clientId, name, email, password))
    }

    suspend fun getUserInfo(): User {
//        val localData = userDAO.getUserInfo()
//
//        if (localData != null) {
//            return localData
//        }

        return try {
            Log.d("getUserInfo remoteData", usersService.getUserInfo().toString())
            val response = usersService.getUserInfo()
            if (response.isSuccessful) {
                val remoteData =
                    response.body() ?: throw Exception("El cuerpo de la respuesta es nulo")
                userDAO.refreshUser(remoteData)
                remoteData
            } else {
                val errorBody = response.errorBody()!!.string()
                val jsonObject = JSONObject(errorBody)
                val message = jsonObject.getString("message")

                throw Exception(
                    "Error del servidor: ${response.code()} - $message"
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteUsers() {
        userDAO.deleteUsers()
    }
}
