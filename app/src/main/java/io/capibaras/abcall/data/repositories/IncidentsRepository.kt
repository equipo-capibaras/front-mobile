package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.database.dao.IncidentDAO
import io.capibaras.abcall.data.database.models.History
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
    private fun getFiledDate(history: List<History>): String {
        return history.first().date
    }

    private fun getEscalatedDate(history: List<History>): String? {
        if (history.size < 2) return null
        return history.lastOrNull { it.action == "escalated" }?.date
    }

    private fun getClosedDate(history: List<History>): String? {
        return if (history.size >= 2 && history.last().action == "closed") {
            history.last().date
        } else {
            null
        }
    }

    private suspend fun setRecentlyUpdated(incident: Incident): Boolean {
        val localIncident = incidentDAO.getIncident(incident.id)
        return if (localIncident != null) {
            incident.history.size != localIncident.history.size
        } else {
            false
        }
    }

    suspend fun getIncidents(): Result<List<Incident>> {
        return try {
            val response = incidentsService.getIncidents()
            if (response.isSuccessful) {
                val incidents = response.body()?.map { incident ->
                    incident.copy(
                        filedDate = getFiledDate(incident.history),
                        escalatedDate = getEscalatedDate(incident.history),
                        closedDate = getClosedDate(incident.history),
                        recentlyUpdated = setRecentlyUpdated(incident)
                    )
                } ?: emptyList()
                Result.success(incidents)
            } else {
                val localData = incidentDAO.getAllIncidents()
                if (localData.isNotEmpty()) {
                    Result.success(localData)
                } else {
                    val errorBody = response.errorBody()!!.string()
                    val jsonObject = JSONObject(errorBody)
                    val message = jsonObject.getString("message")
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
