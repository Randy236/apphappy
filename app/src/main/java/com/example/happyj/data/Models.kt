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
    @SerializedName("motivo_cancelacion") val motivoCancelacion: String? = null,
    /** 30 o 60; null = inferir por hora (compatibilidad). */
    @SerializedName("duracion_minutos") val duracionMinutos: Int? = null,
)

data class ReservaCanchaCreate(
    val nombreCliente: String,
    val deporte: String,
    val fecha: String,
    val hora: String,
    val montoTotal: Double,
    val adelanto: Double,
    val duracionMinutos: Int? = null,
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
    /** 0 = activa, 1 = cancelada (MySQL TINYINT vía JSON). */
    @SerializedName("cancelada") val cancelada: Int = 0,
    @SerializedName("motivo_cancelacion") val motivoCancelacion: String? = null,
    val salon: String,
    val fecha: String,
) {
    fun salonActivo(): Boolean = cancelada == 0
}

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

data class CancelacionCanchaReporte(
    val id: Int,
    val nombreCliente: String,
    val deporte: String?,
    val fecha: String,
    val hora: String,
    @SerializedName("motivo_cancelacion") val motivoCancelacion: String? = null,
)

data class CancelacionSalonReporte(
    val id: Int,
    val nombreCliente: String,
    val salon: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val tipoEvento: String? = null,
    @SerializedName("motivo_cancelacion") val motivoCancelacion: String? = null,
)

data class CancelacionesReporteResponse(
    val rango: RangoFechas,
    val cancha: List<CancelacionCanchaReporte> = emptyList(),
    val salones: List<CancelacionSalonReporte> = emptyList(),
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

/** Motivo obligatorio al cancelar una reserva (texto del cliente). */
data class CancelacionBody(
    val motivo: String,
)

data class ApiErrorBody(val error: String?)
