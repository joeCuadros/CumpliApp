package com.idnp2025b.cumpliapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.idnp2025b.cumpliapp.ui.navigation.Navigation
import com.idnp2025b.cumpliapp.ui.theme.CumpliAppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.idnp2025b.cumpliapp.data.model.AppTheme
import com.idnp2025b.cumpliapp.data.local.preferences.PreferencesManager
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs by preferencesManager.preferencesFlow.collectAsState(initial = null)
            // Lógica para decidir si activamos el modo oscuro
            val isDarkTheme = when (prefs?.theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                else -> isSystemInDarkTheme() // SYSTEM o null
            }

            CumpliAppTheme(
                darkTheme = isDarkTheme
            ) {
                Navigation()
            }
        }
    }
}