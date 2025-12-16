// PreferencesManager.kt - ACTUALIZADO
package com.idnp2025b.cumpliapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.idnp2025b.cumpliapp.data.model.AppTheme
import com.idnp2025b.cumpliapp.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    private object Keys {
        val THEME = stringPreferencesKey("app_theme")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val RECORDATORIO_MINUTOS = intPreferencesKey("recordatorio_minutos") // NUEVO
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[Keys.THEME] ?: AppTheme.SYSTEM.name
            val theme = try {
                AppTheme.valueOf(themeName)
            } catch (e: Exception) {
                AppTheme.SYSTEM
            }

            val notificationsEnabled = preferences[Keys.NOTIFICATIONS_ENABLED] ?: true
            val recordatorioMinutos = preferences[Keys.RECORDATORIO_MINUTOS] ?: 15 // NUEVO

            UserPreferences(theme, notificationsEnabled, recordatorioMinutos)
        }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[Keys.THEME] = theme.name
        }
    }

    suspend fun saveNotification(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    // NUEVO: Guardar minutos de recordatorio
    suspend fun saveRecordatorioMinutos(minutos: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.RECORDATORIO_MINUTOS] = minutos
        }
    }
}