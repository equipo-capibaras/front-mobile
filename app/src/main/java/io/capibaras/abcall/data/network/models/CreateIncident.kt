package io.capibaras.abcall.data.network.models

import com.google.gson.annotations.SerializedName

enum class Channel(val channel: String)  {
    MOBILE("MOBILE"),
    WEB("WEB"),
    EMAIL("EMAIL")
}

data class CreateIncidentRequest (
    val name: String,
    val description: String,
)

data class CreateIncidentResponse (
    val id: String,
    @SerializedName("client_id") val clientId: String,
    val name: String,
    val channel: Channel,
    @SerializedName("reported_by") val reportedBy: String,
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("assigned_to") val assignedTo: String
)