package com.example.feriafind_grupo13.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Instancia única del DataStore
val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {

    // Claves
    private val isLoggedInKey = booleanPreferencesKey("is_logged_key")
    private val userEmailKey = stringPreferencesKey("user_email")
    // --- NUEVO: Clave para el Token JWT ---
    private val authTokenKey = stringPreferencesKey("auth_token")

    // --- GUARDAR DATOS ---
    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
        }
    }

    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[userEmailKey] = email
        }
    }

    // --- NUEVO: Guardar Token ---
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[authTokenKey] = token
        }
    }

    // --- OBTENER DATOS (Flows) ---
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[isLoggedInKey] ?: false
        }

    val getEmail: Flow<String?> = context.dataStore.data
        .map { prefs ->
            prefs[userEmailKey]
        }

    // --- NUEVO: Obtener Token ---
    val getAuthToken: Flow<String?> = context.dataStore.data
        .map { prefs ->
            prefs[authTokenKey]
        }

    // --- LIMPIAR SESIÓN (Logout completo) ---
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(isLoggedInKey)
            prefs.remove(userEmailKey)
            prefs.remove(authTokenKey) // Borramos también el token
        }
    }
}