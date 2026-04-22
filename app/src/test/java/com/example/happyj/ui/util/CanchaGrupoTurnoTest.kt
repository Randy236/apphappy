package com.example.happyj.ui.util

import com.example.happyj.data.ReservaCanchaDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class CanchaGrupoTurnoTest {

    private fun reserva(
        id: Int,
        hora: String,
        cliente: String = "Ana",
        deporte: String = "Futbol",
        duracion: Int? = null,
        estado: String = "activo",
    ) = ReservaCanchaDto(
        id = id,
        nombreCliente = cliente,
        deporte = deporte,
        fecha = "2026-06-15",
        hora = hora,
        montoTotal = 100.0,
        adelanto = 50.0,
        estado = estado,
        duracionMinutos = duracion,
    )

    @Test
    fun reservaQueSolapaSlot_encuentra_reserva_que_cubre_inicio() {
        val r = reserva(1, "18:00:00")
        val hit = reservaQueSolapaSlot(listOf(r), "18:00:00")
        assertSame(r, hit)
    }

    @Test
    fun reservaQueSolapaSlot_ignora_canceladas() {
        val r = reserva(1, "18:00:00", estado = "cancelado")
        assertNull(reservaQueSolapaSlot(listOf(r), "18:00:00"))
    }

    @Test
    fun agruparReservasCanchaContiguas_un_grupo_mismo_cliente_y_deporte() {
        val a = reserva(1, "11:00:00")
        val b = reserva(2, "12:00:00")
        val grupos = agruparReservasCanchaContiguas(listOf(b, a))
        assertEquals(1, grupos.size)
        assertEquals(2, grupos[0].segmentos.size)
        assertEquals(120, grupos[0].duracionTotalMinutos)
    }

    @Test
    fun agruparReservasCanchaContiguas_separa_si_distinto_cliente() {
        val a = reserva(1, "11:00:00", cliente = "Ana")
        val b = reserva(2, "12:00:00", cliente = "Luis")
        val grupos = agruparReservasCanchaContiguas(listOf(a, b))
        assertEquals(2, grupos.size)
    }

    @Test
    fun grupoTurnoCancha_expone_hora_fin_texto() {
        val a = reserva(1, "11:00:00")
        val b = reserva(2, "12:00:00")
        val g = GrupoTurnoCancha(listOf(a, b))
        assertEquals("11:00", g.horaInicioTexto)
        assertEquals("13:00", g.horaFinTexto)
    }

    @Test
    fun formatearDuracionTotalCancha_varios_formatos() {
        assertEquals("1 h 30 min", formatearDuracionTotalCancha(90))
        assertEquals("2 h", formatearDuracionTotalCancha(120))
        assertEquals("45 min", formatearDuracionTotalCancha(45))
    }
}
