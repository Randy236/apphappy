package com.example.happyj.ui.util

import com.example.happyj.data.ReservaCanchaDto
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
@Epic("Happy Jump")
@Feature("Utilidades de tiempo")
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

    @Test
    fun localDateDesdeCampoApi_formato_invalido() {
        assertNull(localDateDesdeCampoApi("no-es-fecha"))
    }

    @Test
    fun sugerenciaInicioFinCancha_fecha_futura() {
        val futuro = LocalDate.now().plusDays(3)
        val (ini, fin) = sugerenciaInicioFinCancha(futuro)
        assertEquals("09:00", ini)
        assertEquals("10:00", fin)
    }

    @Test
    fun sugerenciaInicioFinCancha_fecha_pasada() {
        val pasado = LocalDate.now().minusDays(1)
        val (ini, fin) = sugerenciaInicioFinCancha(pasado)
        assertEquals("09:00", ini)
        assertEquals("10:00", fin)
    }

    @Test
    fun inicioTurnoCanchaEsPasado_fecha_anterior_es_true() {
        val ayer = LocalDate.now().minusDays(1)
        assertEquals(true, inicioTurnoCanchaEsPasado(ayer, "10:00"))
    }

    @Test
    fun mensajeRangoCanchaInvalido_fin_mal_formateada() {
        val d = LocalDate.of(2026, 6, 10)
        assertEquals(
            "Escribe la hora de fin como 19:10 (24 horas).",
            mensajeRangoCanchaInvalido("11:00", "25:00", d),
        )
    }

    @Test
    fun horaTextoASql_acepta_hh_mm() {
        assertEquals("18:10:00", horaTextoASql("18:10"))
        assertNull(horaTextoASql("no"))
    }

    @Test
    fun minutosAHoraSql_formatea() {
        assertEquals("09:30:00", minutosAHoraSql(9 * 60 + 30))
    }

    @Test
    fun franjasHoraEnHora_genera_bloques() {
        assertEquals(listOf("09:00:00", "10:00:00", "11:00:00"), franjasHoraEnHora("09:00", "12:00"))
    }

    @Test
    fun franjasCanchaEntre_parte_rango_en_tramos() {
        val d = LocalDate.of(2026, 6, 10)
        assertEquals(listOf("11:00:00", "12:00:00", "13:00:00"), franjasCanchaEntre("11:00", "13:30", d))
    }

    @Test
    fun duracionReservaCanchaMinutos_usa_columna_o_hora() {
        val conDuracion = ReservaCanchaDto(
            id = 1,
            nombreCliente = "A",
            deporte = "Futbol",
            fecha = "2026-06-15",
            hora = "18:00:00",
            montoTotal = 1.0,
            adelanto = 0.0,
            estado = "activo",
            duracionMinutos = 30,
        )
        assertEquals(30, duracionReservaCanchaMinutos(conDuracion))
    }

    @Test
    fun inicioTurnoCanchaEsPasado_fecha_futura_es_false() {
        val futuro = LocalDate.now().plusDays(5)
        assertFalse(inicioTurnoCanchaEsPasado(futuro, "10:00"))
    }

    @Test
    fun repartirMontoSoles_reparte_con_suma_exacta() {
        val partes = repartirMontoSoles(10.0, 3)
        assertEquals(3, partes.size)
        assertEquals(10.0, partes.sum(), 0.001)
    }

    @Test
    fun slotsCanchaParaFecha_incluye_media_hora() {
        val slots = slotsCanchaParaFecha(LocalDate.of(2026, 6, 10))
        assertTrue(slots.contains("08:00:00"))
        assertTrue(slots.contains("08:30:00"))
    }

    @Test
    fun franjasHoraEnHora_fin_antes_o_igual_inicio_un_solo_bloque() {
        assertEquals(listOf("10:00:00"), franjasHoraEnHora("10:00", "10:00"))
        assertEquals(listOf("11:00:00"), franjasHoraEnHora("11:00", "10:00"))
    }

    @Test
    fun franjasHoraEnHora_hora_invalida_lista_vacia() {
        assertEquals(emptyList<String>(), franjasHoraEnHora("xx", "12:00"))
    }

    @Test
    fun franjasCanchaEntreDetalle_rango_invalido_vacio() {
        val d = LocalDate.of(2026, 6, 10)
        assertEquals(emptyList<FranjaCancha>(), franjasCanchaEntreDetalle("xx", "13:00", d))
        assertEquals(emptyList<FranjaCancha>(), franjasCanchaEntreDetalle("13:00", "11:00", d))
    }

    @Test
    fun mensajeRangoCanchaInvalido_rango_no_armable() {
        val d = LocalDate.of(2026, 6, 10)
        val msg = mensajeRangoCanchaInvalido("11:07", "12:07", d)
        assertTrue(msg == null || msg.contains("Revisa el horario"))
    }

    @Test
    fun duracionReservaCanchaMinutos_sin_columna_usa_hora() {
        val r = ReservaCanchaDto(
            id = 1,
            nombreCliente = "A",
            deporte = "Futbol",
            fecha = "2026-06-15",
            hora = "18:30:00",
            montoTotal = 1.0,
            adelanto = 0.0,
            estado = "activo",
            duracionMinutos = null,
        )
        assertEquals(30, duracionReservaCanchaMinutos(r))
    }

    @Test
    fun horaTextoASql_acepta_segundos() {
        assertEquals("18:10:00", horaTextoASql("18:10:00"))
    }

    @Test
    fun normalizarHoraApi_corta_sin_recortar() {
        assertEquals("8:00", normalizarHoraApi("8:00"))
    }

    @Test
    fun minutosDelDia_minutos_invalidos() {
        assertNull(minutosDelDia("10:99:00"))
    }

    @Test
    fun minutosAHoraSql_coerce_limites() {
        assertEquals("23:59:00", minutosAHoraSql(24 * 60))
    }

    @Test
    fun franjasCanchaEntreDetalle_media_hora_y_hora() {
        val d = LocalDate.of(2026, 6, 10)
        val franjas = franjasCanchaEntreDetalle("13:00", "14:30", d)
        assertEquals(2, franjas.size)
        assertEquals(30, franjas.last().duracionMinutos)
    }

    @Test
    fun inicioTurnoCanchaEsPasado_hora_invalida_hoy() {
        val hoy = LocalDate.now()
        assertTrue(inicioTurnoCanchaEsPasado(hoy, "no-valida"))
    }

    @Test
    fun repartirMontoSoles_con_resto_en_ultima_parte() {
        val partes = repartirMontoSoles(10.01, 3)
        assertEquals(3, partes.size)
        assertEquals(10.01, partes.sum(), 0.001)
    }

    @Test
    fun franjasCanchaEntre_mapea_solo_hora_inicio() {
        val d = LocalDate.of(2026, 6, 10)
        assertEquals(listOf("11:00:00", "12:00:00"), franjasCanchaEntre("11:00", "13:00", d))
    }
}
