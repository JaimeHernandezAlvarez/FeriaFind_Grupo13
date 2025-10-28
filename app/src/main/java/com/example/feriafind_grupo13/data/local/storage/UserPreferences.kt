package com.example.feriafind_grupo13.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey // <-- IMPORTAR
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences (private val context: Context){
    private val isLoggedInKey = booleanPreferencesKey("is_logged_key")

    // --- NUEVO: Clave para el email ---
    private val userEmailKey = stringPreferencesKey("user_email")

    suspend fun setLoggedIn(value: Boolean){
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
        }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[isLoggedInKey] ?: false
        }

    // --- NUEVO: Guardar email ---
    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[userEmailKey] = email
        }
    }

    // --- NUEVO: Obtener email ---
    val getEmail: Flow<String?> = context.dataStore.data
        .map { prefs ->
            prefs[userEmailKey]
        }

    // --- NUEVO: Limpiar sesión (para un futuro logout) ---
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(isLoggedInKey)
            prefs.remove(userEmailKey)
        }
    }
}