package com.example.happyj.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.happyj.data.ApiExceptionMapper
import com.example.happyj.data.LoginBody
import com.example.happyj.data.NetworkModule
import com.example.happyj.data.Session
import com.example.happyj.data.SessionRepository
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

private const val MSG_USUARIO_ACTIVO_OTRO =
    "Este usuario ya inició sesión en otro celular o tablet. Cierra sesión allí o pide a un administrador que reinicie el acceso."

private fun parseLoginError(e: Exception): String {
    if (e is HttpException && e.code() == 409) return MSG_USUARIO_ACTIVO_OTRO
    val base = ApiExceptionMapper.mensajeUsuario(
        e,
        "No se pudo iniciar sesión. Inténtalo otra vez.",
    )
    return if (ApiExceptionMapper.esProblemaDeConexion(e)) {
        "$base\n\n${ApiExceptionMapper.consejoAccesoAlSistema()}"
    } else {
        base
    }
}
