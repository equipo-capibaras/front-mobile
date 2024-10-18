package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.database.dao.CompanyDAO
import io.capibaras.abcall.data.database.models.Company
import io.capibaras.abcall.data.network.services.CompanyService
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class CompanyRepository(
    private val companyDAO: CompanyDAO,
    private val companyService: CompanyService,
) {
    suspend fun getCompanies(): List<Company> {
        val localData = companyDAO.getAllCompanies()

        val remoteData = try {
            companyService.getCompanies()
        } catch (e: IOException) {
            if (localData.isNotEmpty()) {
                return localData
            } else {
                throw e
            }
        }

        if (remoteData.isNotEmpty()) {
            companyDAO.refreshCompanies(remoteData)
        }

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
