package com.idnp2025b.cumpliapp.data.model

data class UserPreferences(
    val theme: AppTheme,
    val notification: Boolean // true = activadas, false = desactivadas
)