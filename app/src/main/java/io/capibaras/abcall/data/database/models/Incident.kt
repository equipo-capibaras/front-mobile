package io.capibaras.abcall.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey val id: String,
    val name: String,
    val channel: String,
    val history: List<History>,
    val filedDate: String,
    val escalatedDate: String?,
    val closedDate: String?,
    val recentlyUpdated: Boolean
)

data class History(
    val seq: Int,
    val date: String,
    val action: String,
    val description: String,
)
