package io.capibaras.abcall.data.network.services

import io.capibaras.abcall.data.database.models.Company
import retrofit2.http.GET

fun interface CompanyService {
    @GET("clients")
    suspend fun getCompanies(): List<Company>
}
