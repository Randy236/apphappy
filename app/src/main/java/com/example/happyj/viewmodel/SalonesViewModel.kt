package com.example.happyj.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.happyj.data.ApiExceptionMapper
import com.example.happyj.data.CancelacionBody
import com.example.happyj.data.NetworkModule
import com.example.happyj.data.ReservaSalonCreate
import com.example.happyj.data.ReservaSalonDto
import com.example.happyj.notifications.CobrarPendienteScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SalonesViewModel(application: Application) : AndroidViewModel(application) {
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    val salones = listOf(
        "Ex Salón de Pinturas",
        "Salón Principal",
        "Salón de Eventos Grande",
        "Salón Laser",
    )

    private val _salonSel = MutableStateFlow<String?>(null)
    val salonSeleccionado: StateFlow<String?> = _salonSel.asStateFlow()

    private val _fecha = MutableStateFlow(LocalDate.now())
    val fecha: StateFlow<LocalDate> = _fecha.asStateFlow()

    private val _reservas = MutableStateFlow<List<ReservaSalonDto>>(emptyList())
    val reservas: StateFlow<List<ReservaSalonDto>> = _reservas.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private suspend fun refrescarListaSalon() {
        val s = _salonSel.value ?: return
        val d = _fecha.value.format(fmt)
        _reservas.value = NetworkModule.api.listReservasSalones(s, d, d)
    }

    fun elegirSalon(nombre: String) {
        _salonSel.value = nombre
        cargar()
    }

    fun salirSalon() {
        _salonSel.value = null
    }

    fun irDiaAnterior() {
        _fecha.value = _fecha.value.minusDays(1)
        cargar()
    }

    fun irDiaSiguiente() {
        _fecha.value = _fecha.value.plusDays(1)
        cargar()
    }

    fun elegirFecha(d: LocalDate) {
        _fecha.value = d
        cargar()
    }

    fun irHoy() {
        _fecha.value = LocalDate.now()
        cargar()
    }

    fun irSemanaAnterior() {
        _fecha.value = _fecha.value.minusWeeks(1)
        cargar()
    }

    fun irSemanaSiguiente() {
        _fecha.value = _fecha.value.plusWeeks(1)
        cargar()
    }

    fun cargar() {
        val s = _salonSel.value ?: return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val d = _fecha.value.format(fmt)
                _reservas.value = NetworkModule.api.listReservasSalones(s, d, d)
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudieron cargar las reservas de este salón.",
                )
            } finally {
                _loading.value = false
            }
        }
    }

    fun crearReserva(body: ReservaSalonCreate, onOk: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val dto = NetworkModule.api.crearReservaSalon(body)
                CobrarPendienteScheduler.scheduleSalonIfPendiente(
                    getApplication<android.app.Application>().applicationContext,
                    dto,
                )
                refrescarListaSalon()
                onOk()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudo guardar la reserva del salón.",
                )
            } finally {
                _loading.value = false
            }
        }
    }

    fun cobrarSaldoSalon(id: Int, onOk: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                NetworkModule.api.cobrarSaldoSalon(id)
                CobrarPendienteScheduler.cancelSalon(
                    getApplication<android.app.Application>().applicationContext,
                    id,
                )
                refrescarListaSalon()
                onOk()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudo registrar el pago completo del salón.",
                )
            } finally {
                _loading.value = false
            }
        }
    }

    fun cancelarReservaSalon(id: Int, motivo: String, onOk: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                NetworkModule.api.cancelarReservaSalon(id, CancelacionBody(motivo))
                CobrarPendienteScheduler.cancelSalon(
                    getApplication<android.app.Application>().applicationContext,
                    id,
                )
                refrescarListaSalon()
                onOk()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudo cancelar la reserva del salón.",
                )
            } finally {
                _loading.value = false
            }
        }
    }
}
