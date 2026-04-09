package com.example.happyj.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.happyj.data.NetworkModule
import com.example.happyj.data.ReservaCanchaCreate
import com.example.happyj.data.ReservaCanchaDto
import com.example.happyj.notifications.CobrarPendienteScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
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

    fun elegirFecha(d: LocalDate) {
        _fecha.value = d
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val d = _fecha.value.format(fmt)
                _reservas.value = NetworkModule.api.listReservasCancha(d, d)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error"
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
            _error.value = null
            try {
                NetworkModule.api.crearReservaCancha(body)
                cargar()
                onOk()
            } catch (e: Exception) {
                _error.value = e.message ?: "No se pudo registrar"
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
            _error.value = null
            try {
                val ctx = getApplication<android.app.Application>().applicationContext
                for (body in cuerpos) {
                    val dto = NetworkModule.api.crearReservaCancha(body)
                    CobrarPendienteScheduler.scheduleCanchaIfPendiente(ctx, dto)
                }
                cargar()
                onOk()
            } catch (e: Exception) {
                _error.value = e.message ?: "No se pudo registrar (¿algún horario ocupado?)"
            }
        }
    }

    fun cobrarSaldoCancha(id: Int, onOk: () -> Unit) {
        viewModelScope.launch {
            _error.value = null
            try {
                NetworkModule.api.cobrarSaldoCancha(id)
                CobrarPendienteScheduler.cancelCancha(
                    getApplication<android.app.Application>().applicationContext,
                    id,
                )
                cargar()
                onOk()
            } catch (e: Exception) {
                _error.value = e.message ?: "No se pudo registrar el cobro"
            }
        }
    }
}
