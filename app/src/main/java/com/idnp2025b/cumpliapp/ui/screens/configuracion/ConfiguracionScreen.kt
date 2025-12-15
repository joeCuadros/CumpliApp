package com.idnp2025b.cumpliapp.ui.screens.configuracion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idnp2025b.cumpliapp.data.model.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    viewModel: ConfiguracionViewModel = hiltViewModel()
) {
    val prefs by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                // WindowInsets(0.dp) para evitar el hueco superior si no es edge-to-edge
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- SECCIÓN TEMA ---
            Text("Apariencia", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Column(Modifier.selectableGroup()) {
                ThemeOptionRow("Predeterminado del sistema", prefs.theme == AppTheme.SYSTEM) {
                    viewModel.updateTheme(AppTheme.SYSTEM)
                }
                ThemeOptionRow("Claro", prefs.theme == AppTheme.LIGHT) {
                    viewModel.updateTheme(AppTheme.LIGHT)
                }
                ThemeOptionRow("Oscuro", prefs.theme == AppTheme.DARK) {
                    viewModel.updateTheme(AppTheme.DARK)
                }
            }

            HorizontalDivider()

            // --- SECCIÓN NOTIFICACIONES ---
            Text("General", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleNotifications(!prefs.notification) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Activar notificaciones", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        if (prefs.notification) "Recibirás recordatorios de tus tareas" else "No se mostrarán alertas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = prefs.notification,
                    onCheckedChange = { viewModel.toggleNotifications(it) }
                )
            }
        }
    }
}

// Componente auxiliar para las filas de selección de tema
@Composable
fun ThemeOptionRow(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}