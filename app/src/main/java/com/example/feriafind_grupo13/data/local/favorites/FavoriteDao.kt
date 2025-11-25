package com.example.feriafind_grupo13.data.local.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    // Si ya es favorito, lo sobreescribe (no duplica)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE vendedorId = :vendedorId AND userEmail = :userEmail")
    suspend fun removeFavorite(vendedorId: String, userEmail: String)

    // Solo trae los favoritos de ESTE usuario
    @Query("SELECT * FROM favorites WHERE userEmail = :userEmail")
    fun getUserFavorites(userEmail: String): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE vendedorId = :vendedorId AND userEmail = :userEmail)")
    suspend fun isFavorite(vendedorId: String, userEmail: String): Boolean
}