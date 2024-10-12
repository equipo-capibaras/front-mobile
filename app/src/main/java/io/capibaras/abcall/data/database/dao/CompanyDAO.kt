package io.capibaras.abcall.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.capibaras.abcall.data.database.models.Company

@Dao
interface CompanyDAO {
    @Query("SELECT * FROM companies")
    suspend fun getAllCompanies(): List<Company>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanies(companies: List<Company>)

    @Query("DELETE FROM companies")
    suspend fun deleteAllCompanies()

    @Transaction
    suspend fun refreshCompanies(companies: List<Company>) {
        deleteAllCompanies()
        insertCompanies(companies)
    }
}