package com.example.happyj.ui.util

import com.example.happyj.data.ReservaCanchaDto

/**
 * Varios registros de BD contiguos (misma persona, mismo deporte) que forman **un solo turno**
 * al reservar un rango con «Otra hora».
 */
data class GrupoTurnoCancha(val segmentos: List<ReservaCanchaDto>) {
    init {
        require(segmentos.isNotEmpty())
    }

    val inicioMin: Int
        get() = minutosDesdeHoraSql(segmentos.first().hora)

    val finMin: Int
        get() = minutosDesdeHoraSql(segmentos.last().hora) + duracionReservaCanchaMinutos(segmentos.last())

    val horaInicioTexto: String
        get() = segmentos.first().hora.trim().take(5)

    val horaFinTexto: String
        get() = formatoHoraMinutos(finMin)

    val duracionTotalMinutos: Int
        get() = (finMin - inicioMin).coerceAtLeast(0)

    val montoTotalAcumulado: Double
        get() = segmentos.sumOf { it.montoTotal }

    val adelantoAcumulado: Double
        get() = segmentos.sumOf { it.adelanto }

    val ids: List<Int>
        get() = segmentos.map { it.id }
}

fun minutosDesdeHoraSql(horaSql: String): Int {
    val t = normalizarHoraApi(horaSql).trim()
    return minutosDelDia(t) ?: minutosDelDia(t.take(5) + ":00") ?: 0
}

private fun formatoHoraMinutos(min: Int): String {
    val m = min.coerceIn(0, 24 * 60 - 1)
    val hh = m / 60
    val mm = m % 60
    return String.format("%02d:%02d", hh, mm)
}

/** Misma regla que la lista de cancha: qué reserva cubre el inicio de este slot de la grilla. */
fun reservaQueSolapaSlot(
    reservas: List<ReservaCanchaDto>,
    slotStartSql: String,
): ReservaCanchaDto? {
    val s = minutosDesdeHoraSql(slotStartSql)
    val e = s + duracionCanchaMinutos(slotStartSql)
    return reservas.find { r ->
        if (r.estado == "cancelado") return@find false
        val rs = minutosDesdeHoraSql(r.hora)
        val re = rs + duracionReservaCanchaMinutos(r)
        rs < e && s < re
    }
}

/**
 * Agrupa filas contiguas del mismo cliente y deporte (fin de una = inicio de la siguiente).
 */
fun agruparReservasCanchaContiguas(reservas: List<ReservaCanchaDto>): List<GrupoTurnoCancha> {
    val activas = reservas
        .filter { it.estado != "cancelado" }
        .sortedBy { minutosDesdeHoraSql(it.hora) }
    if (activas.isEmpty()) return emptyList()
    val grupos = mutableListOf<MutableList<ReservaCanchaDto>>()
    var actual = mutableListOf(activas.first())
    for (i in 1 until activas.size) {
        val prev = actual.last()
        val cur = activas[i]
        val prevEnd = minutosDesdeHoraSql(prev.hora) + duracionReservaCanchaMinutos(prev)
        val curStart = minutosDesdeHoraSql(cur.hora)
        val mismoCliente = prev.nombreCliente.trim().equals(cur.nombreCliente.trim(), ignoreCase = true)
        val mismoDep = prev.deporte == cur.deporte
        val contiguo = prevEnd == curStart
        if (mismoCliente && mismoDep && contiguo) {
            actual.add(cur)
        } else {
            grupos.add(actual)
            actual = mutableListOf(cur)
        }
    }
    grupos.add(actual)
    return grupos.map { GrupoTurnoCancha(it) }
}

/** Primer slot de la grilla (:00 / :30) que pertenece a este grupo (para no repetir filas). */
fun primerSlotSqlDelGrupo(
    slots: List<String>,
    reservasActivas: List<ReservaCanchaDto>,
    g: GrupoTurnoCancha,
): String? {
    val ids = g.segmentos.map { it.id }.toSet()
    return slots.firstOrNull { slot ->
        val r = reservaQueSolapaSlot(reservasActivas, slot) ?: return@firstOrNull false
        r.id in ids
    }
}

fun formatearDuracionTotalCancha(minutos: Int): String {
    val h = minutos / 60
    val m = minutos % 60
    return when {
        h > 0 && m > 0 -> "$h h $m min"
        h > 0 -> "$h h"
        else -> "$m min"
    }
}
