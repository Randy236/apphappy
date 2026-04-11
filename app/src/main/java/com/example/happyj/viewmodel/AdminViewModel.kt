package com.example.happyj.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.happyj.data.ApiExceptionMapper
import com.example.happyj.data.CancelacionesReporteResponse
import com.example.happyj.data.NetworkModule
import com.example.happyj.data.ReportesResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    private val _periodo = MutableStateFlow("semanal")
    val periodo: StateFlow<String> = _periodo.asStateFlow()

    private val _fechaRef = MutableStateFlow(LocalDate.now())
    val fechaRef: StateFlow<LocalDate> = _fechaRef.asStateFlow()

    private val _reporte = MutableStateFlow<ReportesResponse?>(null)
    val reporte: StateFlow<ReportesResponse?> = _reporte.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _mostrarCancelaciones = MutableStateFlow(false)
    val mostrarCancelaciones: StateFlow<Boolean> = _mostrarCancelaciones.asStateFlow()

    private val _cancelacionesLoading = MutableStateFlow(false)
    val cancelacionesLoading: StateFlow<Boolean> = _cancelacionesLoading.asStateFlow()

    private val _cancelaciones = MutableStateFlow<CancelacionesReporteResponse?>(null)
    val cancelaciones: StateFlow<CancelacionesReporteResponse?> = _cancelaciones.asStateFlow()

    private val _cancelacionesError = MutableStateFlow<String?>(null)
    val cancelacionesError: StateFlow<String?> = _cancelacionesError.asStateFlow()

    fun setPeriodo(p: String) {
        _periodo.value = p
        cargar()
        if (_mostrarCancelaciones.value) cargarCancelaciones()
    }

    fun setFechaRef(d: LocalDate) {
        _fechaRef.value = d
        cargar()
        if (_mostrarCancelaciones.value) cargarCancelaciones()
    }

    fun cargar() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _reporte.value = NetworkModule.api.reportes(
                    _periodo.value,
                    _fechaRef.value.format(fmt),
                )
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudieron cargar los reportes.",
                )
            } finally {
                _loading.value = false
            }
        }
    }

    fun abrirPanelCancelaciones() {
        _mostrarCancelaciones.value = true
        cargarCancelaciones()
    }

    fun cerrarPanelCancelaciones() {
        _mostrarCancelaciones.value = false
        _cancelaciones.value = null
        _cancelacionesError.value = null
    }

    fun cargarCancelaciones() {
        viewModelScope.launch {
            _cancelacionesLoading.value = true
            _cancelacionesError.value = null
            try {
                _cancelaciones.value = NetworkModule.api.reportesCancelaciones(
                    _periodo.value,
                    _fechaRef.value.format(fmt),
                )
            } catch (e: Exception) {
                _cancelacionesError.value = ApiExceptionMapper.mensajeUsuario(
                    e,
                    "No se pudieron cargar las cancelaciones.",
                )
                _cancelaciones.value = null
            } finally {
                _cancelacionesLoading.value = false
            }
        }
    }
}
