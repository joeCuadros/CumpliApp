package com.idnp2025b.cumpliapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.idnp2025b.cumpliapp.MainActivity
import com.idnp2025b.cumpliapp.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RecordatorioWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_ACTIVIDAD_ID = "actividad_id"
        const val KEY_TITULO = "titulo"
        const val KEY_DESCRIPCION = "descripcion"
        const val CHANNEL_ID = "recordatorios_channel"
        const val NOTIFICATION_ID_BASE = 2000
    }

    override suspend fun doWork(): Result {
        val actividadId = inputData.getInt(KEY_ACTIVIDAD_ID, 0)
        val titulo = inputData.getString(KEY_TITULO) ?: "Recordatorio"
        val descripcion = inputData.getString(KEY_DESCRIPCION) ?: ""

        if (actividadId == 0) return Result.failure()

        mostrarNotificacion(actividadId, titulo, descripcion)
        return Result.success()
    }

    private fun mostrarNotificacion(actividadId: Int, titulo: String, descripcion: String) {
        createNotificationChannel()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            actividadId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("⏰ Recordatorio: $titulo")
            .setContentText(descripcion.ifBlank { "Tu tarea está próxima a vencer" })
            .setStyle(NotificationCompat.BigTextStyle().bigText(descripcion.ifBlank { "Tu tarea está próxima a vencer" }))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_BASE + actividadId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recordatorios de Tareas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de recordatorios de tus actividades"
                enableVibration(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}