package io.capibaras.abcall.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.capibaras.abcall.data.database.dao.CompanyDAO
import io.capibaras.abcall.data.database.dao.IncidentDAO
import io.capibaras.abcall.data.database.dao.UserDAO
import io.capibaras.abcall.data.database.models.Company
import io.capibaras.abcall.data.database.models.Incident
import io.capibaras.abcall.data.database.models.User

@Database(
    entities = [Company::class, User::class, Incident::class],
    version = 3,
    exportSchema = false
)
abstract class ABCallDB : RoomDatabase() {
    abstract fun companyDAO(): CompanyDAO
    abstract fun userDAO(): UserDAO
    abstract fun incidentDAO(): IncidentDAO

    companion object {
        @Volatile
        private var INSTANCE: ABCallDB? = null

        fun getDatabase(context: Context): ABCallDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ABCallDB::class.java,
                    "abcall_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
