package com.idnp2025b.cumpliapp.util

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.idnp2025b.cumpliapp.data.model.Actividad
import com.idnp2025b.cumpliapp.worker.RecordatorioWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordatorioManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun programarRecordatorio(actividad: Actividad, minutosAntes: Int) {
        if (!actividad.tieneRecordatorio || actividad.completada) {
            return
        }

        val tiempoRecordatorio = actividad.fechaEntrega - (minutosAntes * 60 * 1000L)
        val delay = tiempoRecordatorio - System.currentTimeMillis()

        if (delay <= 0) {
            // La fecha ya pasó, no programar
            return
        }

        val inputData = Data.Builder()
            .putInt(RecordatorioWorker.KEY_ACTIVIDAD_ID, actividad.id)
            .putString(RecordatorioWorker.KEY_TITULO, actividad.titulo)
            .putString(RecordatorioWorker.KEY_DESCRIPCION, actividad.descripcion)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<RecordatorioWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("recordatorio_${actividad.id}")
            .build()

        workManager.enqueueUniqueWork(
            "recordatorio_${actividad.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelarRecordatorio(actividadId: Int) {
        workManager.cancelUniqueWork("recordatorio_$actividadId")
    }

    fun cancelarTodosLosRecordatorios() {
        workManager.cancelAllWorkByTag("recordatorio_")
    }
}