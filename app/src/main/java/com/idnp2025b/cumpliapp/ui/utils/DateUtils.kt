package com.idnp2025b.cumpliapp.ui.utils

import java.text.SimpleDateFormat
import java.util.*

fun formatearFecha(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun esFechaProxima(timestamp: Long): Boolean {
    val diferencia = timestamp - System.currentTimeMillis()
    val unDiaEnMillis = 24 * 60 * 60 * 1000
    return diferencia in 0..unDiaEnMillis
}

fun esFechaVencida(timestamp: Long): Boolean {
    return timestamp < System.currentTimeMillis()
}