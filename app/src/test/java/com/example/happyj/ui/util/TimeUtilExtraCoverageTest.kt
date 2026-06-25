package com.example.happyj.ui.util

import com.example.happyj.data.ReservaCanchaDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

/** Casos borde adicionales para acercar ui.util al 100 % en SonarCloud. */
class TimeUtilExtraCoverageTest {

    private fun reserva(
        id: Int,
        hora: String,
        duracion: Int? = 60,
        estado: String = "activo",
    ) = ReservaCanchaDto(
        id = id,
        nombreCliente = "Ana",
        deporte = "Futbol",
        fecha = "2026-06-15",
        hora = hora,
        montoTotal = 80.0,
        adelanto = 40.0,
        estado = estado,
        duracionMinutos = duracion,
    )

    @Test
    fun duracionReservaCanchaMinutos_duracion_invalida_usa_hora() {
        assertEquals(60, duracionReservaCanchaMinutos(reserva(1, "18:00:00", duracion = 45)))
    }

    @Test
    fun duracionReservaCanchaMinutos_usa_columna_30_y_60() {
        assertEquals(30, duracionReservaCanchaMinutos(reserva(1, "18:30:00", duracion = 30)))
        assertEquals(60, duracionReservaCanchaMinutos(reserva(2, "18:00:00", duracion = 60)))
    }

    @Test
    fun franjasCanchaEntreDetalle_tramo_muy_corto_no_armable() {
        val d = LocalDate.of(2026, 6, 10)
        assertEquals(emptyList<FranjaCancha>(), franjasCanchaEntreDetalle("11:00", "11:15", d))
    }

    @Test
    fun franjasCanchaEntreDetalle_hora_tramo_excede_fin_retorna_vacio() {
        val d = LocalDate.of(2026, 6, 10)
        assertEquals(emptyList<FranjaCancha>(), franjasCanchaEntreDetalle("10:00", "10:50", d))
    }

    @Test
    fun franjasCanchaEntreDetalle_inicio_media_hora_y_dos_horas() {
        val d = LocalDate.of(2026, 6, 10)
        val franjas = franjasCanchaEntreDetalle("11:30", "13:30", d)
        assertEquals(3, franjas.size)
        assertEquals("11:30:00", franjas[0].horaInicioSql)
        assertEquals(30, franjas[0].duracionMinutos)
        assertEquals(60, franjas[1].duracionMinutos)
        assertEquals(30, franjas[2].duracionMinutos)
    }

    @Test
    fun minutosDesdeHoraSql_fallback_hh_mm() {
        assertEquals(9 * 60 + 30, minutosDesdeHoraSql("09:30"))
    }

    @Test
    fun minutosDesdeHoraSql_hora_invalida_retorna_cero() {
        assertEquals(0, minutosDesdeHoraSql("no-es-hora"))
    }

    @Test
    fun reservaQueSolapaSlot_sin_coincidencia_null() {
        assertNull(reservaQueSolapaSlot(listOf(reserva(1, "08:00:00")), "20:00:00"))
    }

    @Test
    fun reservaQueSolapaSlot_slot_media_hora_solapa() {
        val r = reserva(1, "18:00:00", duracion = 60)
        assertEquals(r, reservaQueSolapaSlot(listOf(r), "18:30:00"))
    }

    @Test
    fun formatearDuracionTotalCancha_solo_minutos() {
        assertEquals("0 min", formatearDuracionTotalCancha(0))
    }

    @Test
    fun sugerenciaInicioFinCancha_fecha_futura_lejana() {
        val (ini, fin) = sugerenciaInicioFinCancha(LocalDate.of(2030, 1, 15))
        assertEquals("09:00", ini)
        assertEquals("10:00", fin)
    }

    @Test
    fun sugerenciaInicioFinCancha_hoy_devuelve_par_valido() {
        val (ini, fin) = sugerenciaInicioFinCancha(LocalDate.now())
        assertTrue(Regex("""\d{2}:\d{2}""").matches(ini))
        assertTrue(Regex("""\d{2}:\d{2}""").matches(fin))
    }

    @Test
    fun sugerenciaInicioFinCancha_tarde_no_pasa_de_22h() {
        val hoy = LocalDate.of(2026, 6, 10)
        val (ini, fin) = sugerenciaInicioFinCanchaInterna(
            fecha = hoy,
            hoy = hoy,
            ahora = LocalTime.of(21, 29),
        )
        assertEquals("21:00", ini)
        assertEquals("22:00", fin)
    }

    @Test
    fun mensajeRangoCanchaInvalido_inicio_invalido() {
        val msg = mensajeRangoCanchaInvalido("xx", "12:00", LocalDate.of(2026, 6, 10))
        assertTrue(msg!!.contains("inicio"))
    }

    @Test
    fun mensajeRangoCanchaInvalido_rango_no_armable() {
        val d = LocalDate.of(2026, 6, 10)
        val msg = mensajeRangoCanchaInvalido("10:00", "10:50", d)
        assertNotNull(msg)
        assertTrue(msg!!.contains("Revisa el horario"))
    }

    @Test
    fun franjasCanchaEntreDetalle_tramo_30_minutos() {
        val d = LocalDate.of(2026, 6, 10)
        val franjas = franjasCanchaEntreDetalle("14:30", "15:00", d)
        assertEquals(1, franjas.size)
        assertEquals(30, franjas[0].duracionMinutos)
    }

    @Test
    fun localDateDesdeCampoApi_sin_guiones_estandar() {
        assertNull(localDateDesdeCampoApi("20260615"))
    }

    @Test
    fun localDateDesdeCampoApi_formato_corto_valido() {
        assertEquals(LocalDate.of(2026, 4, 9), localDateDesdeCampoApi("2026-4-9"))
    }

    @Test
    fun localDateDesdeCampoApi_fecha_iso_invalida() {
        assertNull(localDateDesdeCampoApi("2026-13-45"))
    }

    @Test
    fun inicioTurnoCanchaEsPasado_fecha_futura_false() {
        assertFalse(inicioTurnoCanchaEsPasado(LocalDate.now().plusDays(2), "08:00"))
    }

    @Test
    fun inicioTurnoCanchaEsPasado_hoy_con_reloj_fijo() {
        val hoy = LocalDate.of(2026, 6, 10)
        assertTrue(inicioTurnoCanchaEsPasadoInterna(hoy, hoy, LocalTime.of(15, 0), "10:00"))
        assertFalse(inicioTurnoCanchaEsPasadoInterna(hoy, hoy, LocalTime.of(10, 0), "15:00"))
    }

    @Test
    fun inicioTurnoCanchaEsPasado_hoy_hora_invalida_true() {
        assertTrue(inicioTurnoCanchaEsPasado(LocalDate.now(), "no-valida"))
    }

    @Test
    fun agruparReservasCanchaContiguas_una_sola_reserva() {
        val grupos = agruparReservasCanchaContiguas(listOf(reserva(1, "11:00:00")))
        assertEquals(1, grupos.size)
        assertEquals(60, grupos[0].duracionTotalMinutos)
    }

    @Test
    fun grupoTurnoCancha_fin_media_hora() {
        val g = GrupoTurnoCancha(listOf(reserva(1, "18:30:00", duracion = 30)))
        assertEquals("18:30", g.horaInicioTexto)
        assertEquals("19:00", g.horaFinTexto)
    }

    @Test
    fun estadoDisponibilidad_hoy_sin_reservas_es_libre_o_pasado() {
        val hoy = LocalDate.now()
        val estado = estadoDisponibilidadDiaCancha(hoy, emptyList())
        assertTrue(
            estado == EstadoDisponibilidadDiaCancha.Libre ||
                estado == EstadoDisponibilidadDiaCancha.Pasado,
        )
    }

    @Test
    fun estadoDisponibilidad_futuro_todos_slots_ocupados_lleno() {
        val dia = LocalDate.of(2030, 8, 20)
        val slots = slotsCanchaParaFecha(dia)
        val reservas = slots.mapIndexed { i, h -> reserva(i + 1, h) }
        assertEquals(EstadoDisponibilidadDiaCancha.Lleno, estadoDisponibilidadDiaCancha(dia, reservas))
    }

    @Test
    fun minutosDelDia_solo_un_segmento_null() {
        assertNull(minutosDelDia("10"))
    }

    @Test
    fun repartirMontoSoles_tres_partes_con_decimales() {
        val partes = repartirMontoSoles(100.01, 3)
        assertEquals(3, partes.size)
        assertEquals(100.01, partes.sum(), 0.0001)
    }
}
