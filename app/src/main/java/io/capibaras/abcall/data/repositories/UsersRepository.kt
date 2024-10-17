package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.database.dao.UserDAO
import io.capibaras.abcall.data.database.models.User
import io.capibaras.abcall.data.network.models.CreateUserRequest
import io.capibaras.abcall.data.network.services.UsersService
import org.json.JSONObject
import retrofit2.Response

class UsersRepository(
    private val usersService: UsersService,
    private val userDAO: UserDAO,
    private val companyRepository: CompanyRepository
) {
    suspend fun createUser(
        clientId: String,
        name: String,
        email: String,
        password: String
    ): Response<User> {
        return usersService.createUser(CreateUserRequest(clientId, name, email, password))
    }

    suspend fun getUserInfo(): User {
        val localData = userDAO.getUserInfo()
        if (localData != null) {
            return localData
        }

        return try {
            val response = usersService.getUserInfo()
            if (response.isSuccessful) {
                val remoteData = response.body()!!
                val company = remoteData.let { companyRepository.getCompany(it.clientId) }
                val userWithCompany = remoteData.copy(clientName = company.name)
                userDAO.refreshUser(userWithCompany)
                userWithCompany
            } else {
                val errorBody = response.errorBody()!!.string()
                val jsonObject = JSONObject(errorBody)
                val message = jsonObject.getString("message")

                throw Exception(message)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteUsers() {
        userDAO.deleteUsers()
    }
}
