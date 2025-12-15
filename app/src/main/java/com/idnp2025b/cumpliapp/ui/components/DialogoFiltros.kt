package com.idnp2025b.cumpliapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.idnp2025b.cumpliapp.data.model.Categoria
import com.idnp2025b.cumpliapp.ui.screens.lista.TipoOrdenamiento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoFiltros(
    mostrar: Boolean,
    filtroActual: Categoria?,
    ordenamientoActual: TipoOrdenamiento,
    soloConRecordatorio: Boolean,
    onDismiss: () -> Unit,
    onFiltroSeleccionado: (Categoria?) -> Unit,
    onOrdenamientoSeleccionado: (TipoOrdenamiento) -> Unit,
    onRecordatorioChange: (Boolean) -> Unit,
    onLimpiarFiltros: () -> Unit
) {
    if (mostrar) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Configurar filtros") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()), // Hacemos scrollable por si pantallas pequeñas
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- Sección Ordenamiento ---
                    Text(
                        "Ordenar por:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = ordenamientoActual == TipoOrdenamiento.POR_PRIORIDAD,
                            onClick = { onOrdenamientoSeleccionado(TipoOrdenamiento.POR_PRIORIDAD) },
                            label = { Text("Prioridad") },
                            leadingIcon = if (ordenamientoActual == TipoOrdenamiento.POR_PRIORIDAD) {
                                { Icon(androidx.compose.material.icons.Icons.Default.Check, null) }
                            } else null
                        )
                        FilterChip(
                            selected = ordenamientoActual == TipoOrdenamiento.POR_FECHA,
                            onClick = { onOrdenamientoSeleccionado(TipoOrdenamiento.POR_FECHA) },
                            label = { Text("Fecha") },
                            leadingIcon = if (ordenamientoActual == TipoOrdenamiento.POR_FECHA) {
                                { Icon(androidx.compose.material.icons.Icons.Default.Check, null) }
                            } else null
                        )
                    }

                    HorizontalDivider()

                    // --- Sección Recordatorio ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Solo con recordatorio",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Switch(
                            checked = soloConRecordatorio,
                            onCheckedChange = onRecordatorioChange
                        )
                    }

                    HorizontalDivider()

                    // --- Sección Categorías (Lo que faltaba) ---
                    Text(
                        "Filtrar por categoría:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Opción "Todas"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = filtroActual == null,
                            onClick = { onFiltroSeleccionado(null) }
                        )
                        Text(
                            text = "Todas",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    // Opciones dinámicas según el Enum Categoria
                    Categoria.entries.forEach { categoria ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = filtroActual == categoria,
                                onClick = { onFiltroSeleccionado(categoria) }
                            )
                            Text(
                                text = categoria.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Listo")
                }
            },
            dismissButton = {
                TextButton(onClick = onLimpiarFiltros) {
                    Text("Limpiar todo")
                }
            }
        )
    }
}