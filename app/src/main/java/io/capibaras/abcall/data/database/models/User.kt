package io.capibaras.abcall.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val clientId: String,
    val name: String,
    val email: String
)
