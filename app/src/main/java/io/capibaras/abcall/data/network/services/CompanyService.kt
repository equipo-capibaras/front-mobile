package io.capibaras.abcall.data.network.services

import io.capibaras.abcall.data.database.models.Company
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CompanyService {
    @GET("clients")
    suspend fun getCompanies(): List<Company>

    @GET("clients/{id}")
    suspend fun getCompany(@Path("id") id: String): Response<Company>
}
