package com.idnp2025b.cumpliapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.idnp2025b.cumpliapp.MainActivity
import com.idnp2025b.cumpliapp.R
import com.idnp2025b.cumpliapp.domain.repository.InterfaceActividadRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EnfoqueService : Service() {

    @Inject
    lateinit var repository: InterfaceActividadRepository

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null

    private var actividadId: Int = 0
    private var tituloActividad: String = ""
    private var tiempoInicial: Long = 0L
    private var tiempoTranscurrido: Long = 0L
    private var tiempoInicio: Long = 0L

    companion object {
        const val CHANNEL_ID = "enfoque_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_ACTIVIDAD_ID = "actividad_id"
        const val EXTRA_TITULO = "titulo"
        const val EXTRA_TIEMPO_ACUMULADO = "tiempo_acumulado"

        fun startService(context: Context, actividadId: Int, titulo: String, tiempoAcumulado: Long) {
            val intent = Intent(context, EnfoqueService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ACTIVIDAD_ID, actividadId)
                putExtra(EXTRA_TITULO, titulo)
                putExtra(EXTRA_TIEMPO_ACUMULADO, tiempoAcumulado)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun pauseService(context: Context) {
            val intent = Intent(context, EnfoqueService::class.java).apply {
                action = ACTION_PAUSE
            }
            context.startService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, EnfoqueService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                actividadId = intent.getIntExtra(EXTRA_ACTIVIDAD_ID, 0)
                tituloActividad = intent.getStringExtra(EXTRA_TITULO) ?: "Actividad"
                tiempoInicial = intent.getLongExtra(EXTRA_TIEMPO_ACUMULADO, 0L)
                tiempoInicio = System.currentTimeMillis()

                startForeground(NOTIFICATION_ID, createNotification(formatTime(tiempoInicial)))
                startTimer()

                // Marcar actividad como en progreso
                serviceScope.launch(Dispatchers.IO) {
                    repository.actualizarEstadoProgreso(actividadId, true)
                }
            }
            ACTION_PAUSE -> {
                pauseTimer()
            }
            ACTION_STOP -> {
                stopTimer()
            }
        }
        return START_STICKY
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (true) {
                delay(1000)
                tiempoTranscurrido = tiempoInicial + (System.currentTimeMillis() - tiempoInicio)
                updateNotification(formatTime(tiempoTranscurrido))
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()

        serviceScope.launch(Dispatchers.IO) {
            // Guardar tiempo acumulado
            repository.actualizarTiempoAcumulado(actividadId, tiempoTranscurrido)
            repository.actualizarEstadoProgreso(actividadId, false)
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun stopTimer() {
        timerJob?.cancel()

        serviceScope.launch(Dispatchers.IO) {
            repository.actualizarTiempoAcumulado(actividadId, tiempoTranscurrido)
            repository.actualizarEstadoProgreso(actividadId, false)
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(timeText: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val pauseIntent = Intent(this, EnfoqueService::class.java).apply {
            action = ACTION_PAUSE
        }
        val pausePendingIntent = PendingIntent.getService(
            this, 0, pauseIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Modo Enfoque Activo")
            .setContentText("$tituloActividad • $timeText")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Pausar", pausePendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(timeText: String) {
        val notification = createNotification(timeText)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Modo Enfoque",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificaciones del cronómetro de tareas"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = millis / (1000 * 60 * 60)
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
    }
}