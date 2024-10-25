package io.capibaras.abcall.data.network.services

import io.capibaras.abcall.data.database.models.Incident
import retrofit2.Response
import retrofit2.http.GET

interface IncidentsService {
    @GET("users/me/incidents")
    suspend fun getIncidents(): Response<List<Incident>>
}
