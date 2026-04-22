package com.example.happyj.ui.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class TimeUtilTest {

    @Test
    fun localDateDesdeCampoApi_iso_con_zona_parsea_solo_fecha() {
        assertEquals(
            LocalDate.of(2026, 4, 9),
            localDateDesdeCampoApi("2026-04-09T00:00:00.000Z"),
        )
    }

    @Test
    fun localDateDesdeCampoApi_solo_yyyy_mm_dd() {
        assertEquals(LocalDate.of(2026, 1, 2), localDateDesdeCampoApi("2026-01-02"))
    }

    @Test
    fun localDateDesdeCampoApi_null_y_vacio() {
        assertNull(localDateDesdeCampoApi(null))
        assertNull(localDateDesdeCampoApi("   "))
    }

    @Test
    fun normalizarHoraApi_recorta_a_hh_mm_ss() {
        assertEquals("08:30:00", normalizarHoraApi("  08:30:00.123  "))
    }

    @Test
    fun minutosDelDia_validos() {
        assertEquals(0, minutosDelDia("00:00:00"))
        assertEquals(90, minutosDelDia("01:30"))
    }

    @Test
    fun minutosDelDia_invalidos() {
        assertNull(minutosDelDia("24:00"))
        assertNull(minutosDelDia("xx"))
    }

    @Test
    fun duracionCanchaMinutos_media_hora_y_hora() {
        assertEquals(30, duracionCanchaMinutos("18:30:00"))
        assertEquals(60, duracionCanchaMinutos("18:00:00"))
    }

    @Test
    fun slotDesdeHora_formatea() {
        assertEquals("09:00:00", slotDesdeHora(9))
    }

    @Test
    fun mensajeRangoCanchaInvalido_rango_valido_retorna_null() {
        val d = LocalDate.of(2026, 6, 10)
        assertNull(mensajeRangoCanchaInvalido("11:00", "13:30", d))
    }

    @Test
    fun mensajeRangoCanchaInvalido_hora_mal_formateada() {
        val d = LocalDate.of(2026, 6, 10)
        assertEquals(
            "Escribe la hora de inicio como 18:10 (24 horas).",
            mensajeRangoCanchaInvalido("25:00", "13:00", d),
        )
    }

    @Test
    fun repartirMontoSoles_n_cero_o_negativo_lista_vacia() {
        assertEquals(emptyList<Double>(), repartirMontoSoles(100.0, 0))
        assertEquals(emptyList<Double>(), repartirMontoSoles(100.0, -1))
    }

    @Test
    fun repartirMontoSoles_un_solo_elemento() {
        assertEquals(listOf(50.25), repartirMontoSoles(50.25, 1))
    }
}
