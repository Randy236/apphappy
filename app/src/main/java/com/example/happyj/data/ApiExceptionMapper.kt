package com.example.happyj.data

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Convierte excepciones de red y HTTP en textos claros para personas
 * que no manejan mensajes técnicos (sin códigos HTTP ni trazas).
 */
object ApiExceptionMapper {

    private val gson = Gson()

    /** Texto breve para mostrar tras errores de conexión (p. ej. en el login). */
    fun consejoAccesoAlSistema(): String =
        "El celular y el ordenador del local deben estar en la misma Wi‑Fi. " +
            "Pide a quien administra el sistema que confirme la dirección y que el programa del servidor esté abierto."

    fun esProblemaDeConexion(e: Throwable): Boolean {
        var t: Throwable? = e
        var depth = 0
        while (t != null && depth++ < 6) {
            when (t) {
                is UnknownHostException,
                is ConnectException,
                is SocketTimeoutException,
                -> return true
                is IOException -> {
                    val m = t.message.orEmpty()
                    if (m.contains("Failed to connect", ignoreCase = true) ||
                        m.contains("ECONNREFUSED", ignoreCase = true) ||
                        m.contains("Network is unreachable", ignoreCase = true) ||
                        m.contains("timeout", ignoreCase = true)
                    ) {
                        return true
                    }
                }
            }
            t = t.cause
        }
        return false
    }

    /**
     * @param cuandoFalla Frase corta en español si no hay detalle del servidor
     * (ej. "No se pudieron cargar las reservas").
     */
    fun mensajeUsuario(e: Exception, cuandoFalla: String): String {
        mensajeRedSiAplica(e)?.let { return it }
        if (e is HttpException) {
            mensajeCuerpoHttp(e)?.let { return it }
            return mensajePorCodigoHttp(e.code(), cuandoFalla)
        }
        val m = e.message?.trim().orEmpty()
        if (m.isNotEmpty() && m.length < 180 && !m.contains("http", ignoreCase = true) &&
            !m.contains("JsonReader", ignoreCase = true)
        ) {
            return m
        }
        return cuandoFalla
    }

    private fun mensajeRedSiAplica(e: Throwable): String? {
        var t: Throwable? = e
        var depth = 0
        while (t != null && depth++ < 6) {
            when (t) {
                is UnknownHostException ->
                    return "No hay conexión: revisa el Wi‑Fi o la dirección del servidor."
                is ConnectException ->
                    return "No se pudo conectar. Comprueba que el ordenador del local esté encendido y el programa del servidor abierto."
                is SocketTimeoutException ->
                    return "El servidor tardó en responder (Wi‑Fi lenta o el PC del local ocupado). Espera unos segundos e inténtalo otra vez."
                is IOException -> {
                    val msg = t.message.orEmpty()
                    if (msg.contains("Failed to connect", ignoreCase = true) ||
                        msg.contains("ECONNREFUSED", ignoreCase = true) ||
                        msg.contains("Network is unreachable", ignoreCase = true)
                    ) {
                        return "No se llegó al servidor. Revisa la Wi‑Fi y que el programa del local esté en marcha."
                    }
                    if (msg.contains("timeout", ignoreCase = true)) {
                        return "Tiempo de espera agotado. Comprueba la Wi‑Fi e inténtalo de nuevo en un momento."
                    }
                }
            }
            t = t.cause
        }
        return null
    }

    private fun mensajeCuerpoHttp(e: HttpException): String? {
        val raw = try {
            e.response()?.errorBody()?.string()
        } catch (_: Exception) {
            null
        } ?: return null
        if (raw.isBlank()) return null
        return try {
            gson.fromJson(raw, ApiErrorBody::class.java)?.error?.trim()?.takeIf { it.isNotEmpty() }
        } catch (_: Exception) {
            raw.trim().takeIf { it.length in 1..300 }
        }
    }

    private fun mensajePorCodigoHttp(code: Int, cuandoFalla: String): String =
        when (code) {
            400 -> "$cuandoFalla Revisa los datos e inténtalo de nuevo."
            401 -> "No se pudo verificar tu acceso. Si ya estabas dentro, vuelve a iniciar sesión."
            403 -> "Esta acción no está permitida con tu usuario."
            404 -> "No encontramos ese dato. Actualiza la pantalla por si ya cambió."
            408, 504 -> "El servidor tardó demasiado en responder. Inténtalo otra vez."
            409 -> "$cuandoFalla Puede que el horario ya esté ocupado o haya un conflicto."
            413 -> "Los datos enviados son demasiado grandes."
            429 -> "Demasiados intentos seguidos. Espera un momento e inténtalo de nuevo."
            in 500..599 ->
                "El servidor tuvo un problema. Inténtalo más tarde o avisa a quien administra el sistema."
            else -> cuandoFalla
        }

    /** Lee `{ "error": "..." }` de un cuerpo ya obtenido (p. ej. `Response.errorBody()`). */
    fun mensajeDesdeJsonCuerpo(json: String?): String? {
        if (json.isNullOrBlank()) return null
        return try {
            gson.fromJson(json, ApiErrorBody::class.java)?.error?.trim()?.takeIf { it.isNotEmpty() }
        } catch (_: Exception) {
            null
        }
    }
}
