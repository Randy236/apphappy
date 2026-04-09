package com.example.happyj.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("session")

class SessionRepository(private val context: Context) {
    private val keyToken = stringPreferencesKey("token")
    private val keyNombre = stringPreferencesKey("nombre")
    private val keyRol = stringPreferencesKey("rol")
    private val keyId = stringPreferencesKey("id")

    val sessionFlow: Flow<Session?> = context.dataStore.data.map { p ->
        val t = p[keyToken] ?: return@map null
        val id = p[keyId]?.toIntOrNull() ?: return@map null
        val nombre = p[keyNombre] ?: return@map null
        val rol = p[keyRol] ?: return@map null
        Session(id, nombre, rol, t)
    }

    suspend fun currentSession(): Session? = sessionFlow.first()

    suspend fun saveSession(session: Session) {
        TokenHolder.token = session.token
        context.dataStore.edit { p ->
            p[keyToken] = session.token
            p[keyId] = session.userId.toString()
            p[keyNombre] = session.nombre
            p[keyRol] = session.rol
        }
    }

    suspend fun clear() {
        TokenHolder.token = null
        context.dataStore.edit { it.clear() }
    }
}

data class Session(
    val userId: Int,
    val nombre: String,
    val rol: String,
    val token: String,
)
