// UserPreferences.kt - ACTUALIZADO
package com.idnp2025b.cumpliapp.data.model

data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val notification: Boolean = true,
    val recordatorioMinutos: Int = 15 // NUEVO: minutos de anticipación
)