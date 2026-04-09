package com.example.happyj.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.happyj.notifications.CobrarPendienteNotifier

class CobrarPendienteWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val titulo = inputData.getString(KEY_TITLE) ?: return Result.success()
        val cuerpo = inputData.getString(KEY_BODY) ?: return Result.success()
        CobrarPendienteNotifier.show(applicationContext, titulo, cuerpo)
        return Result.success()
    }

    companion object {
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
    }
}
