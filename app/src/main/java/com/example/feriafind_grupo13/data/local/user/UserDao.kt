package com.example.feriafind_grupo13.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun findUserByCredentials(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findUserByEmail(email: String): UserEntity?

    // --- MÉT-ODO AÑADIDO ---
    /**
     * Cuenta cuántos usuarios hay en la tabla.
     */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}