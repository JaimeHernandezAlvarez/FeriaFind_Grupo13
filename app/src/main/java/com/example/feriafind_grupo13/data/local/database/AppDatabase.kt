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
import org.mindrot.jbcrypt.BCrypt

@Database(
    entities = [UserEntity::class, FavoriteEntity::class],
    version = 4, // Mantenemos la versión 4 para soportar los cambios recientes
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
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
                    // Callback robusto que funciona tanto al crear como al migrar destructivamente
                    .addCallback(object : Callback() {
                        // Se ejecuta la primera vez que se crea la DB
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            poblarBaseDeDatos(context)
                        }

                        // Se ejecuta si Room tiene que borrar la DB por cambio de versión
                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            super.onDestructiveMigration(db)
                            poblarBaseDeDatos(context)
                        }
                    })
                    .fallbackToDestructiveMigration() // Permite borrar si cambia la versión sin migración manual
                    .build()

                INSTANCE = instance
                instance
            }
        }

        // Lógica separada para insertar los datos semilla
        private fun poblarBaseDeDatos(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                // Obtenemos una nueva instancia para asegurar que el DAO esté listo
                val dao = getInstance(context).userDao()

                // Solo insertamos si está vacía (para evitar duplicados si se llama accidentalmente)
                if (dao.count() == 0) {

                    // Importante: Hasheamos las contraseñas para que el Login funcione
                    // (UserRepository usa BCrypt.checkpw, así que esto es necesario)
                    val passAdmin = BCrypt.hashpw("Admin123!", BCrypt.gensalt())
                    val passVicente = BCrypt.hashpw("12345678", BCrypt.gensalt())

                    val seed = listOf(
                        UserEntity(
                            nombre = "Admin Duoc",
                            email = "admin@duoc.cl",
                            password = passAdmin,
                            descripcion = "Cuenta de Administrador",
                            horario = "Siempre activo",
                            fotoUri = null
                        ),
                        UserEntity(
                            nombre = "Vicente Cruz",
                            email = "vcruz@duoc.cl", // Minúsculas para consistencia en login
                            password = passVicente,
                            descripcion = "Usuario de prueba estándar",
                            horario = "9:00 - 18:00",
                            fotoUri = null
                        )
                    )

                    seed.forEach { dao.insertUser(it) }
                }
            }
        }
    }
}