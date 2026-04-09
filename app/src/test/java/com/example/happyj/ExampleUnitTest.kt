package com.example.happyj

import com.example.happyj.ui.util.franjasHoraEnHora
import com.example.happyj.ui.util.horaTextoASql
import com.example.happyj.ui.util.repartirMontoSoles
import org.junit.Test

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
    fun repartirMontoSoles_conserva_total_en_centavos() {
        val partes = repartirMontoSoles(100.0, 3)
        val total = partes.sum()
        assertEquals(3, partes.size)
        assertEquals(100.0, total, 0.0001)
    }
}