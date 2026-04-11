package com.example.happyj.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.happyj.data.ReservaCanchaDto
import com.example.happyj.data.ReservaSalonDto
import com.example.happyj.ui.util.duracionReservaCanchaMinutos
import com.example.happyj.ui.util.localDateDesdeCampoApi
import com.example.happyj.work.CobrarPendienteWorker
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object CobrarPendienteScheduler {
    private const val WM_CANCHA = "cobrar_cancha_"
    private const val WM_SALON = "cobrar_salon_"

    fun scheduleCanchaIfPendiente(context: Context, dto: ReservaCanchaDto) {
        if (dto.adelanto >= dto.montoTotal - 1e-6) return
        val zone = ZoneId.systemDefault()
        val horaNorm = dto.hora.trim().take(8)
        val dMin = duracionReservaCanchaMinutos(dto)
        val end = try {
            val ld = localDateDesdeCampoApi(dto.fecha) ?: return
            ld.atTime(LocalTime.parse(horaNorm))
                .plusMinutes(dMin.toLong())
                .atZone(zone)
        } catch (_: Exception) {
            return
        }
        val delayMs = end.toInstant().toEpochMilli() - System.currentTimeMillis()
        if (delayMs <= 0L) return
        enqueue(
            context,
            WM_CANCHA + dto.id,
            delayMs,
            saldo = dto.montoTotal - dto.adelanto,
            cliente = dto.nombreCliente,
            detalle = "Cancha ${dto.hora.take(5)}",
        )
    }

    fun scheduleSalonIfPendiente(context: Context, dto: ReservaSalonDto) {
        if (dto.adelanto >= dto.precioTotal - 1e-6) return
        val zone = ZoneId.systemDefault()
        val finNorm = dto.horaFin.trim().take(8)
        val end = try {
            val ld = localDateDesdeCampoApi(dto.fecha) ?: return
            ld.atTime(LocalTime.parse(finNorm))
                .atZone(zone)
        } catch (_: Exception) {
            return
        }
        val delayMs = end.toInstant().toEpochMilli() - System.currentTimeMillis()
        if (delayMs <= 0L) return
        enqueue(
            context,
            WM_SALON + dto.id,
            delayMs,
            saldo = dto.precioTotal - dto.adelanto,
            cliente = dto.nombreCliente,
            detalle = dto.salon,
        )
    }

    private fun enqueue(
        context: Context,
        workName: String,
        delayMs: Long,
        saldo: Double,
        cliente: String,
        detalle: String,
    ) {
        val saldoTxt = "%.2f".format(abs(saldo))
        val body = "$cliente — $detalle. Falta cobrar S/ $saldoTxt."
        val data = workDataOf(
            CobrarPendienteWorker.KEY_TITLE to "Falta cobrar",
            CobrarPendienteWorker.KEY_BODY to body,
        )
        val req = OneTimeWorkRequestBuilder<CobrarPendienteWorker>()
            .setInitialDelay(delayMs.coerceAtLeast(1_000L), TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            req,
        )
    }

    fun cancelCancha(context: Context, id: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(WM_CANCHA + id)
    }

    fun cancelSalon(context: Context, id: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(WM_SALON + id)
    }
}
