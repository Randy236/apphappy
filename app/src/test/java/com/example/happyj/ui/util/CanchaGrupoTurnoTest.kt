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
    fun grupoTurnoCancha_acumula_montos_e_ids() {
        val a = reserva(1, "11:00:00")
        val b = reserva(2, "12:00:00")
        val g = GrupoTurnoCancha(listOf(a, b))
        assertEquals(200.0, g.montoTotalAcumulado, 0.001)
        assertEquals(100.0, g.adelantoAcumulado, 0.001)
        assertEquals(listOf(1, 2), g.ids)
    }

    @Test
    fun primerSlotSqlDelGrupo_devuelve_primer_slot_del_grupo() {
        val a = reserva(1, "11:00:00")
        val b = reserva(2, "12:00:00")
        val g = GrupoTurnoCancha(listOf(a, b))
        val slots = listOf("09:00:00", "10:00:00", "11:00:00", "12:00:00")
        assertEquals("11:00:00", primerSlotSqlDelGrupo(slots, listOf(a, b), g))
    }

    @Test
    fun formatearDuracionTotalCancha_varios_formatos() {
        assertEquals("1 h 30 min", formatearDuracionTotalCancha(90))
        assertEquals("2 h", formatearDuracionTotalCancha(120))
        assertEquals("45 min", formatearDuracionTotalCancha(45))
    }

    @Test
    fun agruparReservasCanchaContiguas_lista_vacia() {
        assertEquals(emptyList<GrupoTurnoCancha>(), agruparReservasCanchaContiguas(emptyList()))
    }

    @Test
    fun agruparReservasCanchaContiguas_distinto_deporte_no_agrupa() {
        val a = reserva(1, "11:00:00", deporte = "Futbol")
        val b = reserva(2, "12:00:00", deporte = "Voley")
        assertEquals(2, agruparReservasCanchaContiguas(listOf(a, b)).size)
    }

    @Test
    fun agruparReservasCanchaContiguas_hueco_entre_horas_no_agrupa() {
        val a = reserva(1, "11:00:00")
        val b = reserva(2, "13:00:00")
        assertEquals(2, agruparReservasCanchaContiguas(listOf(a, b)).size)
    }

    @Test
    fun reservaQueSolapaSlot_detecta_solapamiento_parcial() {
        val r = reserva(1, "18:00:00", duracion = 60)
        assertSame(r, reservaQueSolapaSlot(listOf(r), "18:30:00"))
    }

    @Test
    fun minutosDesdeHoraSql_parsea_hh_mm() {
        assertEquals(11 * 60, minutosDesdeHoraSql("11:00"))
    }

    @Test
    fun primerSlotSqlDelGrupo_sin_match_retorna_null() {
        val a = reserva(1, "11:00:00")
        val g = GrupoTurnoCancha(listOf(a))
        assertNull(primerSlotSqlDelGrupo(listOf("09:00:00"), listOf(a), g))
    }

    @Test
    fun grupoTurnoCancha_un_solo_segmento() {
        val a = reserva(1, "11:00:00", duracion = 30)
        val g = GrupoTurnoCancha(listOf(a))
        assertEquals(30, g.duracionTotalMinutos)
        assertEquals("11:30", g.horaFinTexto)
    }
}
