package com.idnp2025b.cumpliapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.idnp2025b.cumpliapp.data.model.AppTheme
import com.idnp2025b.cumpliapp.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extensión para crear una única instancia de DataStore ligada al Contexto
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    // Definimos las claves (Keys) para guardar los valores
    private object Keys {
        val THEME = stringPreferencesKey("app_theme")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            // Lógica para leer el tema
            val themeName = preferences[Keys.THEME] ?: AppTheme.SYSTEM.name
            val theme = try {
                AppTheme.valueOf(themeName)
            } catch (e: Exception) {
                AppTheme.SYSTEM
            }

            // Lógica para leer notificaciones
            val notificationsEnabled = preferences[Keys.NOTIFICATIONS_ENABLED] ?: true

            UserPreferences(theme, notificationsEnabled)
        }

    // Función para guardar el Tema
    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[Keys.THEME] = theme.name
        }
    }

    // Función para guardar estado de Notificaciones
    suspend fun saveNotification(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
}