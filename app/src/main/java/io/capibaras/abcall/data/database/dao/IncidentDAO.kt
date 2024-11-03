package io.capibaras.abcall.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.capibaras.abcall.data.database.models.Incident

@Dao
interface IncidentDAO {
    @Query("SELECT * FROM incidents")
    suspend fun getAllIncidents(): List<Incident>

    @Query("SELECT * FROM incidents WHERE id = :id")
    suspend fun getIncident(id: String): Incident?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncidents(incidents: List<Incident>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: Incident)

    @Query("DELETE FROM incidents")
    suspend fun deleteAllIncidents()

    @Query("UPDATE incidents SET isViewed = :isViewed WHERE id = :id")
    suspend fun updateIncidentViewedStatus(id: String, isViewed: Boolean)

    @Transaction
    suspend fun refreshIncidents(incidents: List<Incident>) {
        try {
            val currentIncidents = getAllIncidents().associateBy { it.id }

            val updatedIncidents = incidents.map { newIncident ->
                val localIncident = currentIncidents[newIncident.id]
                newIncident.copy(
                    isViewed = localIncident?.isViewed ?: true
                )
            }

            deleteAllIncidents()
            insertIncidents(updatedIncidents)
        } catch (e: Exception) {
            throw e
        }
    }
}