package com.example.happyj.ui.util

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
