package com.example.happyj.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.happyj.data.ApiExceptionMapper
import com.example.happyj.data.NetworkModule
import com.example.happyj.data.ReservaCanchaDto
import com.example.happyj.data.ReservaSalonDto
import com.example.happyj.ui.util.localDateDesdeCampoApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class LineaHistorial(
    val fecha: LocalDate,
    val etiquetaTipo: String,
    val titulo: String,
    val detalle: String,
    val orden: Long,
)

class PerfilViewModel(application: Application) : AndroidViewModel(application) {
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    private val _lineas = MutableStateFlow<List<LineaHistorial>>(emptyList())
    val lineasHistorial: StateFlow<List<LineaHistorial>> = _lineas.asStateFlow()

    private val _cargandoHistorial = MutableStateFlow(false)
    val cargandoHistorial: StateFlow<Boolean> = _cargandoHistorial.asStateFlow()

    private val _errorHistorial = MutableStateFlow<String?>(null)
    val errorHistorial: StateFlow<String?> = _errorHistorial.asStateFlow()

    fun cargarHistorial() {
        viewModelScope.launch {
            _cargandoHistorial.value = true
            _errorHistorial.value = null
            try {
                val desde = LocalDate.now().minusMonths(12)
                val hasta = LocalDate.now().plusMonths(12)
                val d0 = desde.format(fmt)
                val d1 = hasta.format(fmt)
                val cancha = NetworkModule.api.listReservasCancha(d0, d1)
                val salones = NetworkModule.api.listReservasSalones(null, d0, d1)
                _lineas.value = combinarYOrdenar(cancha, salones)
            } catch (e: Exception) {
                _errorHistorial.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudo cargar el historial de reservas.",
                )
            } finally {
                _cargandoHistorial.value = false
            }
        }
    }

    private fun combinarYOrdenar(
        cancha: List<ReservaCanchaDto>,
        salones: List<ReservaSalonDto>,
    ): List<LineaHistorial> {
        val out = mutableListOf<LineaHistorial>()
        for (r in cancha) {
            val fd = localDateDesdeCampoApi(r.fecha) ?: continue
            val t = runCatching {
                LocalTime.parse(r.hora.take(8))
            }.getOrNull() ?: LocalTime.MIDNIGHT
            val orden = fd.toEpochDay() * 86400L + t.toSecondOfDay().toLong()
            out.add(
                LineaHistorial(
                    fecha = fd,
                    etiquetaTipo = "Cancha",
                    titulo = "${r.nombreCliente} · ${r.deporte}",
                    detalle = "${r.fecha} · ${r.hora.take(5)} · S/ ${"%.2f".format(r.montoTotal)}",
                    orden = orden,
                ),
            )
        }
        for (r in salones) {
            val fd = localDateDesdeCampoApi(r.fecha) ?: continue
            val t = runCatching {
                LocalTime.parse(r.horaInicio.take(8))
            }.getOrNull() ?: LocalTime.MIDNIGHT
            val orden = fd.toEpochDay() * 86400L + t.toSecondOfDay().toLong()
            val sub = buildString {
                append(r.salon)
                append(" · ")
                append(r.tipoEvento)
                if (r.tipoEvento == "Cumpleanos" && !r.nombreCumpleanero.isNullOrBlank()) {
                    append(" (")
                    append(r.nombreCumpleanero)
                    append(")")
                }
            }
            out.add(
                LineaHistorial(
                    fecha = fd,
                    etiquetaTipo = "Salón",
                    titulo = sub,
                    detalle = "${r.horaInicio.take(5)}–${r.horaFin.take(5)} · S/ ${"%.2f".format(r.precioTotal)}",
                    orden = orden,
                ),
            )
        }
        return out.sortedByDescending { it.orden }
    }
}
