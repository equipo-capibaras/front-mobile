package io.capibaras.abcall.data.repositories

import android.util.Log
import io.capibaras.abcall.data.database.dao.IncidentDAO
import io.capibaras.abcall.data.database.models.Incident
import io.capibaras.abcall.data.network.services.IncidentsService
import org.json.JSONObject
import java.io.IOException

sealed class IncidentsRepositoryError : Exception() {
    data object GetIncidentsError : RepositoryError() {
        private fun readResolve(): Any = GetIncidentsError
    }
}

class IncidentsRepository(
    private val incidentDAO: IncidentDAO,
    private val incidentsService: IncidentsService
) {

    suspend fun getIncidents(): Result<List<Incident>> {
        return try {
            val response = incidentsService.getIncidents()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                val localData = incidentDAO.getAllIncidents()
                if (localData.isNotEmpty()) {
                    Result.success(localData)
                } else {
                    val errorBody = response.errorBody()!!.string()
                    val jsonObject = JSONObject(errorBody)
                    val message = jsonObject.getString("message")
                    Log.d("IncidentsRepository", message)
                    Result.failure(RepositoryError.CustomError(message))
                }
            }
        } catch (e: IOException) {
            Result.failure(IncidentsRepositoryError.GetIncidentsError)
        } catch (e: Exception) {
            if (e.message != null) {
                Result.failure(RepositoryError.CustomError(e.message!!))
            } else {
                Result.failure(RepositoryError.UnknownError)
            }
        }
    }
}
