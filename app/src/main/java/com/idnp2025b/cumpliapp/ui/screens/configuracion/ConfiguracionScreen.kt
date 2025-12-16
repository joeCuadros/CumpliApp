package com.idnp2025b.cumpliapp.ui.screens.configuracion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
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
    var expandedRecordatorio by remember { mutableStateOf(false) }

    val opcionesRecordatorio = listOf(
        5 to "5 minutos",
        15 to "15 minutos",
        30 to "30 minutos",
        60 to "1 hora",
        120 to "2 horas"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
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
            Text(
                "Apariencia",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

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
            Text(
                "Notificaciones",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

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

            // NUEVO: Tiempo de anticipación de recordatorios
            if (prefs.notification) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Tiempo de anticipación",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        Text(
                            "Recibe alertas antes de la fecha de entrega",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        ExposedDropdownMenuBox(
                            expanded = expandedRecordatorio,
                            onExpandedChange = { expandedRecordatorio = it }
                        ) {
                            OutlinedTextField(
                                value = opcionesRecordatorio.first { it.first == prefs.recordatorioMinutos }.second,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Notificar con") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRecordatorio) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedRecordatorio,
                                onDismissRequest = { expandedRecordatorio = false }
                            ) {
                                opcionesRecordatorio.forEach { (minutos, texto) ->
                                    DropdownMenuItem(
                                        text = { Text(texto) },
                                        onClick = {
                                            viewModel.updateRecordatorioMinutos(minutos)
                                            expandedRecordatorio = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // Información adicional
            Text(
                "Acerca de",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "CumpliApp",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "Versión 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Gestión inteligente de tareas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

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