package com.example.happyj.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): LoginResponse

    @POST("auth/logout")
    suspend fun logout()

    @GET("reservas-cancha")
    suspend fun listReservasCancha(
        @Query("desde") desde: String?,
        @Query("hasta") hasta: String?,
    ): List<ReservaCanchaDto>

    @POST("reservas-cancha")
    suspend fun crearReservaCancha(@Body body: ReservaCanchaCreate): ReservaCanchaDto

    @PUT("reservas-cancha/{id}/cobrar-saldo")
    suspend fun cobrarSaldoCancha(@Path("id") id: Int): ReservaCanchaDto

    @PUT("reservas-cancha/{id}/cancelar")
    suspend fun cancelarReservaCancha(
        @Path("id") id: Int,
        @Body body: CancelacionBody,
    ): ReservaCanchaDto

    @GET("reservas-salones")
    suspend fun listReservasSalones(
        @Query("salon") salon: String?,
        @Query("desde") desde: String?,
        @Query("hasta") hasta: String?,
    ): List<ReservaSalonDto>

    @POST("reservas-salones")
    suspend fun crearReservaSalon(@Body body: ReservaSalonCreate): ReservaSalonDto

    @PUT("reservas-salones/{id}/cobrar-saldo")
    suspend fun cobrarSaldoSalon(@Path("id") id: Int): ReservaSalonDto

    @PUT("reservas-salones/{id}/cancelar")
    suspend fun cancelarReservaSalon(
        @Path("id") id: Int,
        @Body body: CancelacionBody,
    ): ReservaSalonDto

    @GET("reportes")
    suspend fun reportes(
        @Query("periodo") periodo: String,
        @Query("fecha") fecha: String?,
    ): ReportesResponse

    @GET("reportes/cancelaciones")
    suspend fun reportesCancelaciones(
        @Query("periodo") periodo: String,
        @Query("fecha") fecha: String?,
    ): CancelacionesReporteResponse

    @PUT("usuarios/{id}/pin")
    suspend fun cambiarPin(
        @Path("id") id: Int,
        @Body body: PinUpdateBody,
    ): Response<Map<String, Boolean>>
}
