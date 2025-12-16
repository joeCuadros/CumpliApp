package com.idnp2025b.cumpliapp.ui.screens.configuracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idnp2025b.cumpliapp.data.model.AppTheme
import com.idnp2025b.cumpliapp.data.model.UserPreferences
import com.idnp2025b.cumpliapp.data.local.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfiguracionViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val state: StateFlow<UserPreferences> = preferencesManager.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences(AppTheme.SYSTEM, true, 15)
        )

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesManager.saveTheme(theme)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.saveNotification(enabled)
        }
    }

    // NUEVO: Actualizar minutos de recordatorio
    fun updateRecordatorioMinutos(minutos: Int) {
        viewModelScope.launch {
            preferencesManager.saveRecordatorioMinutos(minutos)
        }
    }
}