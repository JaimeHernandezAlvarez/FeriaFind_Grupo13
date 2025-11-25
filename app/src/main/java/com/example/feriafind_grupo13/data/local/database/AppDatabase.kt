package com.example.feriafind_grupo13.data.local.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.feriafind_grupo13.data.local.favorites.FavoriteDao
import com.example.feriafind_grupo13.data.local.favorites.FavoriteEntity
import com.example.feriafind_grupo13.data.local.user.UserDao
import com.example.feriafind_grupo13.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// IMPORTANTE: version = 3 para incluir la nueva tabla de favoritos
@Database(
    entities = [UserEntity::class, FavoriteEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    // Exponemos el DAO de favoritos para que el Repository/ViewModel lo usen
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "ui_navegacion.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Callback para poblar datos iniciales si la DB está vacía
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).userDao()

                                // Datos de prueba iniciales (Seed)
                                val seed = listOf(
                                    UserEntity(
                                        nombre = "Admin Duoc",
                                        email = "admin@duoc.cl",
                                        password = "Admin123!",
                                        descripcion = "Admin",
                                        horario = "N/A",
                                        fotoUri = null
                                    ),
                                    UserEntity(
                                        nombre = "Vicente Cruz",
                                        email = "Vcruz@duoc.cl",
                                        password = "12345678",
                                        descripcion = "El vicente mas de prueba que nunca",
                                        horario = "N/A",
                                        fotoUri = null
                                    )
                                )

                                if (dao.count() == 0) {
                                    seed.forEach { dao.insertUser(it) }
                                }
                            }
                        }
                    })
                    // Importante: Si cambias de versión 2 a 3 y no hay migración manual,
                    // esto borra la DB y la crea de nuevo (ideal para desarrollo).
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}