package com.example.happyj.data

import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    val id: Int,
    val nombre: String,
    val rol: String,
)

data class LoginResponse(
    val token: String,
    val usuario: UsuarioDto,
)

data class LoginBody(
    val nombre: String,
    val pin: String,
)

data class ReservaCanchaDto(
    val id: Int,
    val nombreCliente: String,
    val deporte: String,
    val fecha: String,
    val hora: String,
    val montoTotal: Double,
    val adelanto: Double,
    val estado: String,
)

data class ReservaCanchaCreate(
    val nombreCliente: String,
    val deporte: String,
    val fecha: String,
    val hora: String,
    val montoTotal: Double,
    val adelanto: Double,
)

data class ReservaSalonDto(
    val id: Int,
    val nombreCliente: String,
    val tipoEvento: String,
    val nombreCumpleanero: String?,
    val zona: String,
    @SerializedName("numeroNinos") val numeroNinos: Int,
    val horaInicio: String,
    val horaFin: String,
    val precioTotal: Double,
    val adelanto: Double,
    val salon: String,
    val fecha: String,
)

data class ReservaSalonCreate(
    val nombreCliente: String,
    val tipoEvento: String,
    val nombreCumpleanero: String?,
    val zona: String,
    val numeroNinos: Int,
    val horaInicio: String,
    val horaFin: String,
    val precioTotal: Double,
    val adelanto: Double,
    val salon: String,
    val fecha: String,
)

data class HoraOcupadaRow(
    val hora: String,
    val reservas: Int,
)

data class SalonReporteRow(
    val salon: String,
    val ingresos: Double,
    val reservas: Int,
)

data class SalonTop(
    val salon: String,
    val n: Int?,
    val total: Double?,
)

data class ResumenGeneral(
    val ingresosTotales: Double,
    val totalReservasCancha: Int,
    val totalReservasSalones: Int,
    val totalAdelantos: Double,
)

data class ComparacionIngresos(
    val cancha: Double,
    val salones: Double,
)

data class ReportesResponse(
    val periodo: String,
    val rango: RangoFechas,
    val resumen: ResumenGeneral? = null,
    val comparacionIngresos: ComparacionIngresos? = null,
    val cancha: ReporteCancha,
    val salones: ReporteSalones,
)

data class RangoFechas(
    val inicio: String,
    val fin: String,
)

data class ReporteCancha(
    val ingresosPeriodo: Double,
    val ingresosSemanales: Double,
    val ingresosMensuales: Double,
    val totalReservas: Int,
    val horariosMasOcupados: List<HoraOcupadaRow>,
)

data class ReporteSalones(
    val ingresosTotalPeriodo: Double,
    val porSalon: List<SalonReporteRow>,
    val salonMasAlquilado: SalonTop?,
    val salonMasRentable: SalonTop?,
)

data class PinUpdateBody(
    val pinActual: String?,
    val pinNuevo: String,
)

data class ApiErrorBody(val error: String?)
