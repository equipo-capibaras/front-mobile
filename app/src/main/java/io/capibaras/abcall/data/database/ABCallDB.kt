package io.capibaras.abcall.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.capibaras.abcall.data.database.dao.CompanyDAO
import io.capibaras.abcall.data.database.models.Company

@Database(entities = [Company::class], version = 1)
abstract class ABCallDB : RoomDatabase() {
    abstract fun companyDAO(): CompanyDAO

    companion object {
        @Volatile
        private var INSTANCE: ABCallDB? = null

        fun getDatabase(context: Context): ABCallDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ABCallDB::class.java,
                    "abcall_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
