package io.capibaras.abcall.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.capibaras.abcall.data.database.models.User

@Dao
interface UserDAO {
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUserInfo(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("DELETE FROM users")
    suspend fun deleteUsers()

    @Transaction
    suspend fun refreshUser(newUser: User) {
        deleteUsers()
        insertUser(newUser)
    }
}