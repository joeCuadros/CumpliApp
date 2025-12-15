package com.idnp2025b.cumpliapp.ui.screens.configuracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idnp2025b.cumpliapp.data.model.AppTheme
import com.idnp2025b.cumpliapp.data.model.UserPreferences
import com.idnp2025b.cumpliapp.data.preferences.PreferencesManager
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

    // Estado observable de la configuración
    val state: StateFlow<UserPreferences> = preferencesManager.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            // Valores iniciales mientras carga (evita parpadeos nulos)
            initialValue = UserPreferences(AppTheme.SYSTEM, true)
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
}