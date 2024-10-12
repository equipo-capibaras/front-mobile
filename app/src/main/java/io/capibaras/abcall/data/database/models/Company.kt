package io.capibaras.abcall.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companies")
data class Company(
    @PrimaryKey val id: String,
    val name: String
)
