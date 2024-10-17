package io.capibaras.abcall.data.repositories

import android.content.SharedPreferences
import io.capibaras.abcall.data.database.dao.CompanyDAO
import io.capibaras.abcall.data.database.models.Company
import io.capibaras.abcall.data.network.services.CompanyService
import org.json.JSONException
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
        val localCompany = companyDAO.getCompany(clientId)
        if (localCompany != null) {
            return localCompany
        }

        return try {
            val response = companyService.getCompany(clientId)
            if (response.isSuccessful) {
                val remoteData = response.body()!!
                companyDAO.insertCompany(remoteData)
                remoteData
            } else {
                val errorBody = response.errorBody()!!.string()
                val message = try {
                    val jsonObject = JSONObject(errorBody)
                    jsonObject.getString("message")
                } catch (e: JSONException) {
                    e.message
                }
                throw Exception(message)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
