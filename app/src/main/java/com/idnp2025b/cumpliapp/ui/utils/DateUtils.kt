package com.idnp2025b.cumpliapp.ui.utils

import java.text.SimpleDateFormat
import java.util.*

fun formatearFecha(timestamp: Long): String {
    val ahora = Calendar.getInstance()
    val fecha = Calendar.getInstance().apply { timeInMillis = timestamp }

    val sdfHora = SimpleDateFormat("HH:mm", Locale.getDefault())
    val sdfFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    return when {
        // Hoy
        ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
                ahora.get(Calendar.DAY_OF_YEAR) == fecha.get(Calendar.DAY_OF_YEAR) -> {
            "Hoy a las ${sdfHora.format(Date(timestamp))}"
        }
        // Mañana
        ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
                ahora.get(Calendar.DAY_OF_YEAR) + 1 == fecha.get(Calendar.DAY_OF_YEAR) -> {
            "Mañana a las ${sdfHora.format(Date(timestamp))}"
        }
        // Ayer
        ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
                ahora.get(Calendar.DAY_OF_YEAR) - 1 == fecha.get(Calendar.DAY_OF_YEAR) -> {
            "Ayer a las ${sdfHora.format(Date(timestamp))}"
        }
        // Otra fecha
        else -> {
            "${sdfFecha.format(Date(timestamp))} a las ${sdfHora.format(Date(timestamp))}"
        }
    }
}

fun esFechaProxima(timestamp: Long): Boolean {
    val diferencia = timestamp - System.currentTimeMillis()
    // Considera "próxima" si está en las siguientes 24 horas
    val unDiaEnMillis = 24 * 60 * 60 * 1000
    return diferencia in 0..unDiaEnMillis
}

fun esFechaVencida(timestamp: Long): Boolean {
    return timestamp < System.currentTimeMillis()
}

fun esHoy(timestamp: Long): Boolean {
    val ahora = Calendar.getInstance()
    val fecha = Calendar.getInstance().apply { timeInMillis = timestamp }

    return ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
            ahora.get(Calendar.DAY_OF_YEAR) == fecha.get(Calendar.DAY_OF_YEAR)
}

// NUEVO: Formatear tiempo acumulado
fun formatearTiempo(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = millis / (1000 * 60 * 60)

    return when {
        hours > 0 -> String.format("%dh %dm", hours, minutes)
        minutes > 0 -> String.format("%dm %ds", minutes, seconds)
        else -> String.format("%ds", seconds)
    }
}

fun formatearTiempoCompleto(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = millis / (1000 * 60 * 60)
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}