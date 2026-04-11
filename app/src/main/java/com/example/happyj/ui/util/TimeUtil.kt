package com.example.happyj.ui.util

import com.example.happyj.data.ReservaCanchaDto
import java.time.LocalDate
import java.time.LocalTime

/**
 * Convierte el campo fecha del API a [LocalDate].
 * Node/MySQL a veces serializan como ISO con hora (`2026-04-09T00:00:00.000Z`);
 * [LocalDate.parse] solo acepta `yyyy-MM-dd` y falla en la `T`.
 */
fun localDateDesdeCampoApi(fecha: String?): LocalDate? {
    if (fecha.isNullOrBlank()) return null
    val t = fecha.trim()
    if (t.length >= 10 && t[4] == '-' && t[7] == '-') {
        return runCatching { LocalDate.parse(t.substring(0, 10)) }.getOrNull()
    }
    return runCatching { LocalDate.parse(t) }.getOrNull()
}

/** Normaliza hora del API (ej. "08:00:00") para comparar con slots. */
fun normalizarHoraApi(hora: String): String {
    val t = hora.trim()
    return if (t.length >= 8) t.take(8) else t
}

fun slotDesdeHora(h: Int): String = String.format("%02d:00:00", h)

/** Minutos desde medianoche a partir de "HH:mm" o "HH:mm:ss". */
fun minutosDelDia(hora: String): Int? {
    val n = normalizarHoraApi(hora.trim())
    val p = n.split(":")
    if (p.size < 2) return null
    val hh = p[0].toIntOrNull() ?: return null
    val mm = p.getOrNull(1)?.toIntOrNull() ?: 0
    if (hh !in 0..23 || mm !in 0..59) return null
    return hh * 60 + mm
}

fun minutosAHoraSql(min: Int): String {
    val m = min.coerceIn(0, 24 * 60 - 1)
    val hh = m / 60
    val mm = m % 60
    return String.format("%02d:%02d:00", hh, mm)
}

/** Convierte "H:mm", "HH:mm" o "HH:mm:ss" a formato SQL; null si es inválido. */
fun horaTextoASql(hora: String): String? {
    val t = hora.trim()
    val conSeg = when {
        Regex("^\\d{1,2}:\\d{2}$").matches(t) -> "$t:00"
        Regex("^\\d{1,2}:\\d{2}:\\d{2}$").matches(t) -> t
        else -> return null
    }
    return minutosDelDia(conSeg)?.let { minutosAHoraSql(it) }
}

/**
 * Bloques de 1 h desde [horaInicio] inclusive hasta [horaFin] exclusive.
 * Ej. 09:00–12:00 → 09:00, 10:00, 11:00.
 */
fun franjasHoraEnHora(horaInicio: String, horaFin: String): List<String> {
    val a = minutosDelDia(horaInicio) ?: return emptyList()
    val b = minutosDelDia(horaFin) ?: return emptyList()
    if (b <= a) return listOf(minutosAHoraSql(a))
    val out = mutableListOf<String>()
    var t = a
    while (t < b) {
        out.add(minutosAHoraSql(t))
        t += 60
    }
    return out
}

/** Duración del bloque según la hora de inicio: :30 → 30 min, :00 → 60 min. */
fun duracionCanchaMinutos(horaSql: String): Int {
    val mm = normalizarHoraApi(horaSql).split(":").getOrNull(1)?.toIntOrNull() ?: 0
    return if (mm == 30) 30 else 60
}

/** Duración real de una reserva guardada (columna opcional en API). */
fun duracionReservaCanchaMinutos(r: ReservaCanchaDto): Int {
    val d = r.duracionMinutos
    if (d == 30 || d == 60) return d
    return duracionCanchaMinutos(r.hora)
}

/** Un tramo atómico al guardar (ej. 13:00 con 30 min de juego). */
data class FranjaCancha(val horaInicioSql: String, val duracionMinutos: Int)

/**
 * Inicios de slot en la grilla (rápidos de tocar): cada 30 min (:00 y :30), todos los días.
 * Otros minutos (ej. 18:10) se eligen con «Otra hora».
 */
fun slotsCanchaParaFecha(@Suppress("UNUSED_PARAMETER") fecha: LocalDate): List<String> {
    val out = mutableListOf<String>()
    var m = 8 * 60
    val ultimoInicio = 21 * 60 + 30
    while (m <= ultimoInicio) {
        out.add(minutosAHoraSql(m))
        m += 30
    }
    return out
}

/**
 * Parte el rango [horaInicio, horaFin) en tramos de 30 o 60 min hasta cubrirlo exactamente
 * (ej. 11:00–13:30 → 11:00 60 min + 12:00 60 min + 13:00 30 min).
 */
fun franjasCanchaEntreDetalle(
    horaInicio: String,
    horaFin: String,
    @Suppress("UNUSED_PARAMETER") fecha: LocalDate,
): List<FranjaCancha> {
    val a = minutosDelDia(horaInicio) ?: return emptyList()
    val b = minutosDelDia(horaFin) ?: return emptyList()
    if (b <= a) return emptyList()
    val out = mutableListOf<FranjaCancha>()
    var t = a
    while (t < b) {
        val remaining = b - t
        if (remaining < 30) return emptyList()
        val slotSql = minutosAHoraSql(t)
        val mmPart = t % 60
        val d = when {
            remaining == 30 -> 30
            mmPart == 30 -> 30
            remaining >= 60 -> 60
            else -> return emptyList()
        }
        if (t + d > b) return emptyList()
        out.add(FranjaCancha(slotSql, d))
        t += d
    }
    return out
}

fun franjasCanchaEntre(
    horaInicio: String,
    horaFin: String,
    fecha: LocalDate,
): List<String> = franjasCanchaEntreDetalle(horaInicio, horaFin, fecha).map { it.horaInicioSql }

/**
 * Sugerencia de inicio/fin (1 h) al abrir «Otra hora»: hoy = al menos ~10 min adelante;
 * sin forzar solo horas en punto (útil si se animan a última hora).
 */
fun sugerenciaInicioFinCancha(fecha: LocalDate): Pair<String, String> {
    val hoy = LocalDate.now()
    if (fecha.isBefore(hoy)) return "09:00" to "10:00"
    val minInicio: Int = if (fecha.isEqual(hoy)) {
        val now = LocalTime.now()
        val nm = now.hour * 60 + now.minute + 10
        maxOf(8 * 60, nm)
    } else {
        9 * 60
    }
    val s = minInicio.coerceIn(8 * 60, 21 * 60)
    val e = s + 60
    return if (e > 22 * 60) {
        val s2 = 21 * 60
        minutosAHoraSql(s2).take(5) to minutosAHoraSql(s2 + 60).take(5)
    } else {
        minutosAHoraSql(s).take(5) to minutosAHoraSql(e).take(5)
    }
}

/** null si el rango es coherente con las reglas de la cancha para esa fecha. */
fun mensajeRangoCanchaInvalido(horaInicio: String, horaFin: String, fecha: LocalDate): String? {
    val ini = horaTextoASql(horaInicio) ?: return "Escribe la hora de inicio como 18:10 (24 horas)."
    val fin = horaTextoASql(horaFin) ?: return "Escribe la hora de fin como 19:10 (24 horas)."
    val franjas = franjasCanchaEntreDetalle(ini.take(5), fin.take(5), fecha)
    if (franjas.isEmpty()) {
        return "Revisa el horario: usa 24 h (ej. 13:30, no 1:30). Debe poder armarse con medias horas y horas completas (ej. 11:00–13:30)."
    }
    return null
}

/** Reparte [total] en [n] partes iguales (centavos) para que la suma sea exacta. */
fun repartirMontoSoles(total: Double, n: Int): List<Double> {
    if (n <= 0) return emptyList()
    if (n == 1) return listOf(total)
    val centavos = kotlin.math.round(total * 100).toLong()
    val base = centavos / n
    val resto = centavos - base * n
    return List(n) { i ->
        val c = base + if (i == n - 1) resto else 0L
        c / 100.0
    }
}
