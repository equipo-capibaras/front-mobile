package io.capibaras.abcall.data.network.services

import io.capibaras.abcall.data.database.models.Incident
import io.capibaras.abcall.data.network.models.CreateIncidentRequest
import io.capibaras.abcall.data.network.models.CreateIncidentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IncidentsService {
    @GET("users/me/incidents")
    suspend fun getIncidents(): Response<List<Incident>>

    @POST("incidents/mobile")
    suspend fun createIncident(@Body createIncidentRequest: CreateIncidentRequest): Response<CreateIncidentResponse>
}
