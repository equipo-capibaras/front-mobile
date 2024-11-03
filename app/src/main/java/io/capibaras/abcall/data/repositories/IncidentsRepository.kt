package io.capibaras.abcall.data.repositories

import io.capibaras.abcall.data.database.dao.IncidentDAO
import io.capibaras.abcall.data.database.models.History
import io.capibaras.abcall.data.database.models.Incident
import io.capibaras.abcall.data.network.models.CreateIncidentRequest
import io.capibaras.abcall.data.network.models.CreateIncidentResponse
import io.capibaras.abcall.data.network.services.IncidentsService
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
        return history.lastOrNull { it.action == "closed" }?.date
    }

    private suspend fun setRecentlyUpdated(incident: Incident): Boolean {
        val localIncident = incidentDAO.getIncident(incident.id) ?: return false

        return when {
            incident.history.size != localIncident.history.size && localIncident.isViewed -> {
                incidentDAO.updateIncidentViewedStatus(incident.id, false)
                true
            }

            !localIncident.isViewed -> true
            else -> false
        }
    }

    suspend fun getIncidents(): Result<List<Incident>> {
        val localData = incidentDAO.getAllIncidents()
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

                incidentDAO.refreshIncidents(incidents)

                Result.success(incidents)
            } else {
                handleErrorResponse(localData, response.errorBody()?.string())
            }
        } catch (e: IOException) {
            handleNetworkAndLocalDBFailure(localData, IncidentsRepositoryError.GetIncidentsError)
        } catch (e: Exception) {
            Result.failure(e.message?.let { RepositoryError.CustomError(it) }
                ?: RepositoryError.UnknownError)
        }
    }

    suspend fun createIncident(name: String, description: String): Result<CreateIncidentResponse> {
        return try {
            val response = incidentsService.createIncident(CreateIncidentRequest(name, description))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBodyString = response.errorBody()?.string()
                handleErrorResponse(null, errorBodyString)
            }
        } catch (e: IOException) {
            handleNetworkAndLocalDBFailure(null, RepositoryError.NetworkError)
        } catch (e: Exception) {
            Result.failure(e.message?.let { RepositoryError.CustomError(it) }
                ?: RepositoryError.UnknownError)
        }
    }

    suspend fun markAsViewed(incidentId: String) {
        incidentDAO.updateIncidentViewedStatus(incidentId, true)
    }


}
