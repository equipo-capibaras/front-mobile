package io.capibaras.abcall.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey val id: String,
    val name: String,
    val status: String,
    val filedDate: String,
    val escalatedDate: String?,
    val closedDate: String?,
    val updated: Boolean?,
)
