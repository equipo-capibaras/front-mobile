package io.capibaras.abcall.data.repositories

import android.content.SharedPreferences
import android.util.Log
import io.capibaras.abcall.data.database.dao.CompanyDAO
import io.capibaras.abcall.data.database.models.Company
import io.capibaras.abcall.data.network.services.CompanyService
import org.json.JSONObject

class CompanyRepository(
    private val companyDAO: CompanyDAO,
    private val companyService: CompanyService,
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val CACHE_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L // 7 días en milisegundos
        private const val LAST_UPDATE_KEY = "companies_last_update"
    }

    suspend fun getCompanies(forceUpdate: Boolean = false): List<Company> {
        val lastUpdate = sharedPreferences.getLong(LAST_UPDATE_KEY, 0L)
        val cacheExpired = System.currentTimeMillis() - lastUpdate > CACHE_EXPIRATION_TIME
        val localData = companyDAO.getAllCompanies()

        if (localData.isNotEmpty() && !forceUpdate && !cacheExpired) {
            return localData
        }

        val remoteData = try {
            companyService.getCompanies()
        } catch (e: Exception) {
            return localData
        }

        companyDAO.refreshCompanies(remoteData)

        sharedPreferences.edit().putLong(LAST_UPDATE_KEY, System.currentTimeMillis()).apply()

        return remoteData
    }

    suspend fun getCompany(clientId: String): Company {
        Log.d("getCompany", "clientId $clientId")
        val localCompany = companyDAO.getCompany(clientId)
        if (localCompany != null) {
            return localCompany
        }

        return try {
            val response = companyService.getCompany(clientId)
            Log.d("getCompany", "response $response")
            if (response.isSuccessful) {
                val remoteData =
                    response.body() ?: throw Exception("El cuerpo de la respuesta es nulo")

                Log.d("getCompany", "remoteData $remoteData")
                companyDAO.insertCompany(remoteData)
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
}
