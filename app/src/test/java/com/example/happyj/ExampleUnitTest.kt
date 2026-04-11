package com.example.happyj

import com.example.happyj.ui.util.franjasCanchaEntre
import com.example.happyj.ui.util.franjasCanchaEntreDetalle
import com.example.happyj.ui.util.franjasHoraEnHora
import com.example.happyj.ui.util.horaTextoASql
import com.example.happyj.ui.util.repartirMontoSoles
import org.junit.Test
import java.time.LocalDate

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun horaTextoASql_normaliza_hora_valida() {
        assertEquals("09:05:00", horaTextoASql("9:05"))
        assertEquals("14:30:00", horaTextoASql("14:30"))
    }

    @Test
    fun horaTextoASql_rechaza_hora_invalida() {
        assertNull(horaTextoASql("25:00"))
        assertNull(horaTextoASql("abc"))
    }

    @Test
    fun franjasHoraEnHora_genera_bloques_esperados() {
        val franjas = franjasHoraEnHora("09:00", "12:00")
        assertEquals(listOf("09:00:00", "10:00:00", "11:00:00"), franjas)
    }

    @Test
    fun franjasCanchaEntre_semana_media_hora_y_hora() {
        val miercoles = LocalDate.of(2026, 4, 8)
        val f = franjasCanchaEntre("18:30", "20:00", miercoles)
        assertEquals(listOf("18:30:00", "19:00:00"), f)
    }

    @Test
    fun franjasCanchaEntre_finDeSemana_solo_horas() {
        val sabado = LocalDate.of(2026, 4, 11)
        val f = franjasCanchaEntre("09:00", "12:00", sabado)
        assertEquals(listOf("09:00:00", "10:00:00", "11:00:00"), f)
    }

    /** Ej. jefa: 11:00 a 13:30 → 2 h + media hora final. */
    @Test
    fun franjasCanchaEntre_once_a_una_y_media() {
        val d = LocalDate.of(2026, 4, 9)
        val det = franjasCanchaEntreDetalle("11:00", "13:30", d)
        assertEquals(3, det.size)
        assertEquals("11:00:00", det[0].horaInicioSql)
        assertEquals(60, det[0].duracionMinutos)
        assertEquals("12:00:00", det[1].horaInicioSql)
        assertEquals(60, det[1].duracionMinutos)
        assertEquals("13:00:00", det[2].horaInicioSql)
        assertEquals(30, det[2].duracionMinutos)
    }

    @Test
    fun repartirMontoSoles_conserva_total_en_centavos() {
        val partes = repartirMontoSoles(100.0, 3)
        val total = partes.sum()
        assertEquals(3, partes.size)
        assertEquals(100.0, total, 0.0001)
    }
}