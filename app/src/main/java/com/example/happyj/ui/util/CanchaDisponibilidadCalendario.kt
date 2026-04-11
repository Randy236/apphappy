package com.example.happyj.ui.util

import com.example.happyj.data.ReservaCanchaDto
import java.time.LocalDate
import java.time.LocalTime

/** Estado de la grilla rápida de la cancha para un día (solo tramos aún reservables cuentan). */
enum class EstadoDisponibilidadDiaCancha {
    /** Todo el día ya pasó: sin tramos futuros en la grilla. */
    Pasado,

    /** Hay al menos un slot libre y ninguno ocupado en tramos futuros. */
    Libre,

    /** Hay slots libres y ocupados entre los tramos futuros. */
    Parcial,

    /** Todos los tramos futuros de la grilla están ocupados. */
    Lleno,
}

private fun horaToMin(horaSql: String): Int {
    val n = normalizarHoraApi(horaSql).split(":")
    val hh = n.getOrNull(0)?.toIntOrNull() ?: 0
    val mm = n.getOrNull(1)?.toIntOrNull() ?: 0
    return hh * 60 + mm
}

private fun reservaSolapaConSlot(
    reservas: List<ReservaCanchaDto>,
    slotStartSql: String,
): ReservaCanchaDto? {
    val s = horaToMin(slotStartSql)
    val e = s + duracionCanchaMinutos(slotStartSql)
    return reservas.find { r ->
        if (r.estado == "cancelado") return@find false
        val rs = horaToMin(r.hora)
        val re = rs + duracionReservaCanchaMinutos(r)
        rs < e && s < re
    }
}

private fun esSlotPasado(fecha: LocalDate, horaSlotSql: String): Boolean {
    val hoy = LocalDate.now()
    if (fecha.isBefore(hoy)) return true
    if (fecha.isAfter(hoy)) return false
    val ahoraMin = LocalTime.now().hour * 60 + LocalTime.now().minute
    return horaToMin(horaSlotSql) < ahoraMin
}

/**
 * Calcula si el día se ve libre, parcial o lleno en la grilla de toques (:00 / :30),
 * usando las mismas reglas que la pantalla de cancha.
 */
fun estadoDisponibilidadDiaCancha(
    fecha: LocalDate,
    reservasDelDia: List<ReservaCanchaDto>,
): EstadoDisponibilidadDiaCancha {
    val slots = slotsCanchaParaFecha(fecha)
    val relevantes = slots.filterNot { esSlotPasado(fecha, it) }
    if (relevantes.isEmpty()) return EstadoDisponibilidadDiaCancha.Pasado

    var libres = 0
    var ocupados = 0
    for (slot in relevantes) {
        if (reservaSolapaConSlot(reservasDelDia, slot) != null) ocupados++ else libres++
    }
    return when {
        ocupados == 0 -> EstadoDisponibilidadDiaCancha.Libre
        libres == 0 -> EstadoDisponibilidadDiaCancha.Lleno
        else -> EstadoDisponibilidadDiaCancha.Parcial
    }
}
