package com.example.happyj.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.happyj.data.ApiExceptionMapper
import com.example.happyj.data.CancelacionBody
import com.example.happyj.data.NetworkModule
import com.example.happyj.data.ReservaCanchaCreate
import com.example.happyj.data.ReservaCanchaDto
import com.example.happyj.notifications.CobrarPendienteScheduler
import com.example.happyj.ui.util.EstadoDisponibilidadDiaCancha
import com.example.happyj.ui.util.estadoDisponibilidadDiaCancha
import com.example.happyj.ui.util.localDateDesdeCampoApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CanchaViewModel(application: Application) : AndroidViewModel(application) {
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    private val _fecha = MutableStateFlow(LocalDate.now())
    val fecha: StateFlow<LocalDate> = _fecha.asStateFlow()

    private val _reservas = MutableStateFlow<List<ReservaCanchaDto>>(emptyList())
    val reservas: StateFlow<List<ReservaCanchaDto>> = _reservas.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Mapa día → estado para el calendario con colores (mes cargado al abrir el diálogo). */
    private val _estadoDiasMesCalendario = MutableStateFlow<Map<LocalDate, EstadoDisponibilidadDiaCancha>>(emptyMap())
    val estadoDiasMesCalendario: StateFlow<Map<LocalDate, EstadoDisponibilidadDiaCancha>> =
        _estadoDiasMesCalendario.asStateFlow()

    private val _loadingCalendarioMes = MutableStateFlow(false)
    val loadingCalendarioMes: StateFlow<Boolean> = _loadingCalendarioMes.asStateFlow()

    private val _errorCalendarioMes = MutableStateFlow<String?>(null)
    val errorCalendarioMes: StateFlow<String?> = _errorCalendarioMes.asStateFlow()

    private suspend fun refrescarListaCancha() {
        val d = _fecha.value.format(fmt)
        _reservas.value = NetworkModule.api.listReservasCancha(d, d)
    }

    fun irDiaAnterior() {
        _fecha.value = _fecha.value.minusDays(1)
        cargar()
    }

    fun irDiaSiguiente() {
        _fecha.value = _fecha.value.plusDays(1)
        cargar()
    }

    fun irHoy() {
        _fecha.value = LocalDate.now()
        cargar()
    }

    /** Mueve la fecha una semana atrás (la fila de días muestra la semana que contiene ese día). */
    fun irSemanaAnterior() {
        _fecha.value = _fecha.value.minusWeeks(1)
        cargar()
    }

    /** Mueve la fecha una semana adelante. */
    fun irSemanaSiguiente() {
        _fecha.value = _fecha.value.plusWeeks(1)
        cargar()
    }

    fun elegirFecha(d: LocalDate) {
        _fecha.value = d
        cargar()
    }

    /** Carga reservas del rango del mes y calcula libre/parcial/lleno por día para el calendario. */
    fun cargarDisponibilidadMesCalendario(ym: YearMonth) {
        viewModelScope.launch {
            _loadingCalendarioMes.value = true
            _errorCalendarioMes.value = null
            try {
                val desde = ym.atDay(1).format(fmt)
                val hasta = ym.atEndOfMonth().format(fmt)
                val lista = NetworkModule.api.listReservasCancha(desde, hasta)
                val porFecha = lista
                    .mapNotNull { r -> localDateDesdeCampoApi(r.fecha)?.let { d -> d to r } }
                    .groupBy({ it.first }, { it.second })
                val map = buildMap {
                    for (d in 1..ym.lengthOfMonth()) {
                        val fecha = ym.atDay(d)
                        put(fecha, estadoDisponibilidadDiaCancha(fecha, porFecha[fecha].orEmpty()))
                    }
                }
                _estadoDiasMesCalendario.value = map
            } catch (e: Exception) {
                _estadoDiasMesCalendario.value = emptyMap()
                _errorCalendarioMes.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudo cargar la disponibilidad del mes.",
                )
            } finally {
                _loadingCalendarioMes.value = false
            }
        }
    }

    fun cargar() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                refrescarListaCancha()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudieron cargar las reservas de la cancha.",
                )
            } finally {
                _loading.value = false
            }
        }
    }

    fun crearReserva(
        body: ReservaCanchaCreate,
        onOk: () -> Unit,
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val dto = NetworkModule.api.crearReservaCancha(body)
                CobrarPendienteScheduler.scheduleCanchaIfPendiente(
                    getApplication<android.app.Application>().applicationContext,
                    dto,
                )
                refrescarListaCancha()
                onOk()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudo guardar la reserva de la cancha.",
                )
            } finally {
                _loading.value = false
            }
        }
    }

    /** Varias horas seguidas (misma persona): una fila por franja horaria. */
    fun crearReservasCancha(
        cuerpos: List<ReservaCanchaCreate>,
        onOk: () -> Unit,
    ) {
        if (cuerpos.isEmpty()) return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val ctx = getApplication<android.app.Application>().applicationContext
                for (body in cuerpos) {
                    val dto = NetworkModule.api.crearReservaCancha(body)
                    CobrarPendienteScheduler.scheduleCanchaIfPendiente(ctx, dto)
                }
                refrescarListaCancha()
                onOk()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudieron guardar una o más reservas. Puede que algún horario ya esté ocupado.",
                )
            } finally {
                _loading.value = false
            }
        }
    }

    fun cobrarSaldoCancha(id: Int, onOk: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                NetworkModule.api.cobrarSaldoCancha(id)
                CobrarPendienteScheduler.cancelCancha(
                    getApplication<android.app.Application>().applicationContext,
                    id,
                )
                refrescarListaCancha()
                onOk()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudo registrar el pago completo de la cancha.",
                )
            } finally {
                _loading.value = false
            }
        }
    }

    fun cancelarReservaCancha(id: Int, motivo: String, onOk: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                NetworkModule.api.cancelarReservaCancha(id, CancelacionBody(motivo))
                CobrarPendienteScheduler.cancelCancha(
                    getApplication<android.app.Application>().applicationContext,
                    id,
                )
                refrescarListaCancha()
                onOk()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudo cancelar la reserva de la cancha.",
                )
            } finally {
                _loading.value = false
            }
        }
    }

    /** Mismo motivo para todas las franjas de un turno largo («Otra hora» en varios tramos). */
    fun cancelarGrupoCancha(ids: List<Int>, motivo: String, onOk: () -> Unit) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val ctx = getApplication<android.app.Application>().applicationContext
                val body = CancelacionBody(motivo)
                for (id in ids) {
                    NetworkModule.api.cancelarReservaCancha(id, body)
                    CobrarPendienteScheduler.cancelCancha(ctx, id)
                }
                refrescarListaCancha()
                onOk()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudieron cancelar todas las franjas de la reserva.",
                )
            } finally {
                _loading.value = false
            }
        }
    }

    /** Marca pagado el saldo pendiente de cada franja del grupo. */
    fun cobrarSaldoGrupoCancha(ids: List<Int>, onOk: () -> Unit) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val ctx = getApplication<android.app.Application>().applicationContext
                for (id in ids) {
                    NetworkModule.api.cobrarSaldoCancha(id)
                    CobrarPendienteScheduler.cancelCancha(ctx, id)
                }
                refrescarListaCancha()
                onOk()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudo registrar el pago en una o más franjas.",
                )
            } finally {
                _loading.value = false
            }
        }
    }
}
