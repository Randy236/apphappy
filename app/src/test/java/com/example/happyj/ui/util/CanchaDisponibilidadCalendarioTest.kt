package com.example.happyj.ui.util

import com.example.happyj.data.ReservaCanchaDto
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CanchaDisponibilidadCalendarioTest {

    private fun reserva(id: Int, hora: String) = ReservaCanchaDto(
        id = id,
        nombreCliente = "Test",
        deporte = "Futbol",
        fecha = "2030-06-15",
        hora = hora,
        montoTotal = 80.0,
        adelanto = 40.0,
        estado = "activo",
        duracionMinutos = 60,
    )

    /** Fecha futura: todos los slots de grilla cuentan como “no pasados”. */
    private val diaFuturo = LocalDate.of(2030, 6, 15)

    @Test
    fun estado_sin_reservas_es_libre() {
        assertEquals(
            EstadoDisponibilidadDiaCancha.Libre,
            estadoDisponibilidadDiaCancha(diaFuturo, emptyList()),
        )
    }

    @Test
    fun estado_dia_pasado_es_pasado() {
        val pasado = LocalDate.of(2020, 1, 1)
        assertEquals(
            EstadoDisponibilidadDiaCancha.Pasado,
            estadoDisponibilidadDiaCancha(pasado, emptyList()),
        )
    }

    @Test
    fun estado_parcial_cuando_solo_parte_de_slots_ocupada() {
        val reservas = listOf(reserva(1, "18:00:00"))
        assertEquals(
            EstadoDisponibilidadDiaCancha.Parcial,
            estadoDisponibilidadDiaCancha(diaFuturo, reservas),
        )
    }
}
