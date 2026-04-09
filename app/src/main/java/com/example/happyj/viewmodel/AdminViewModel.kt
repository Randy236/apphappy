package com.example.happyj.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    fun setPeriodo(p: String) {
        _periodo.value = p
        cargar()
    }

    fun setFechaRef(d: LocalDate) {
        _fechaRef.value = d
        cargar()
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
                _error.value = e.message ?: "Error"
            } finally {
                _loading.value = false
            }
        }
    }
}
