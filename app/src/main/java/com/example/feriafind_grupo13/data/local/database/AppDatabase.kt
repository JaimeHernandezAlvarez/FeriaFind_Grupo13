package com.example.feriafind_grupo13.data.local.database

import android.content.Context                                  // Contexto para construir DB
import androidx.room.Database                                   // Anotación @Database
import androidx.room.Room                                       // Builder de DB
import androidx.room.RoomDatabase                               // Clase base de DB
import androidx.sqlite.db.SupportSQLiteDatabase                 // Tipo del callback onCreate
import com.example.feriafind_grupo13.data.local.user.UserDao         // Import del DAO de usuario
import com.example.feriafind_grupo13.data.local.user.UserEntity      // Import de la entidad de usuario
import kotlinx.coroutines.CoroutineScope                        // Para corrutinas en callback
import kotlinx.coroutines.Dispatchers                           // Dispatcher IO
import kotlinx.coroutines.launch                                // Lanzar corrutina

@Database(
    entities = [UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

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
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                // Usamos getInstance(context).userDao() para asegurar que la
                                // instancia de la DB esté lista antes de usar el DAO.
                                val dao = getInstance(context).userDao()

                                // --- INICIO DE CAMBIOS ---

                                // 1. Añadimos el campo "nombre" a los datos de precarga
                                val seed = listOf(
                                    UserEntity(
                                        nombre = "Admin Duoc", // Campo añadido
                                        email = "admin@duoc.cl",
                                        password = "Admin123!",
                                        descripcion = "Admin",
                                        horario = "N/A",
                                        fotoUri = null
                                    ),
                                    UserEntity(
                                        nombre = "Vicente Cruz", // Campo añadido
                                        email = "Vcruz@duoc.cl",
                                        password = "12345678",
                                        descripcion = "El vicente mas de prueba que nunca",
                                        horario = "N/A",
                                        fotoUri = null
                                    )
                                )

                                // 2. Usamos el mét-odo count() que acabamos de añadir al DAO
                                if (dao.count() == 0) {
                                    seed.forEach {
                                        // 3. Usamos el nombre de mét-odo correcto: insertUser
                                        dao.insertUser(it)
                                    }
                                }
                                // --- FIN DE CAMBIOS ---
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}