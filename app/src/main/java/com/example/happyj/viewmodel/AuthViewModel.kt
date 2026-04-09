package com.example.happyj.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.happyj.data.ApiErrorBody
import com.example.happyj.data.LoginBody
import com.example.happyj.data.NetworkModule
import com.example.happyj.data.Session
import com.example.happyj.data.SessionRepository
import com.google.gson.Gson
import retrofit2.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = SessionRepository(application)

    val session: StateFlow<Session?> = repo.sessionFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null,
    )

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    fun login(nombre: String, pin: String) {
        viewModelScope.launch {
            _loginError.value = null
            try {
                val r = NetworkModule.api.login(LoginBody(nombre.trim(), pin))
                repo.saveSession(
                    Session(
                        userId = r.usuario.id,
                        nombre = r.usuario.nombre,
                        rol = r.usuario.rol,
                        token = r.token,
                    ),
                )
            } catch (e: Exception) {
                _loginError.value = parseLoginError(e)
            }
        }
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    fun logout() {
        viewModelScope.launch {
            try {
                NetworkModule.api.logout()
            } catch (_: Exception) {
                // Sin red o sesión ya inválida: igual limpiamos local
            }
            repo.clear()
        }
    }
}

private const val MSG_USUARIO_ACTIVO_OTRO = "Este usuario ya está activo en otro dispositivo"

private fun parseLoginError(e: Exception): String {
    if (e is HttpException) {
        val raw = e.response()?.errorBody()?.string()
        if (raw != null) {
            try {
                val msg = Gson().fromJson(raw, ApiErrorBody::class.java).error
                if (!msg.isNullOrBlank()) return msg
            } catch (_: Exception) {
                // ignorar parse
            }
        }
        if (e.code() == 409) return MSG_USUARIO_ACTIVO_OTRO
    }
    return e.message ?: "Error de conexión"
}
