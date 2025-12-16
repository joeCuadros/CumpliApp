package com.idnp2025b.cumpliapp.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.idnp2025b.cumpliapp.data.model.Categoria
import com.idnp2025b.cumpliapp.data.model.Prioridad
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioActividad(
    titulo: String,
    onTituloChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    fechaMillis: Long,
    onFechaChange: (Long) -> Unit,
    prioridad: Prioridad,
    onPrioridadChange: (Prioridad) -> Unit,
    categoria: Categoria,
    onCategoriaChange: (Categoria) -> Unit,
    tieneRecordatorio: Boolean,
    onRecordatorioChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        OutlinedTextField(
            value = titulo,
            onValueChange = onTituloChange,
            label = { Text("Título") },
            placeholder = { Text("Ej: Entrega de proyecto final") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // Descripción
        OutlinedTextField(
            value = descripcion,
            onValueChange = onDescripcionChange,
            label = { Text("Descripción (opcional)") },
            placeholder = { Text("Detalles adicionales...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            maxLines = 5
        )

        HorizontalDivider()

        // NUEVO: Sección de Fecha y Hora
        Text(
            "Fecha y hora de entrega",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Selector de Fecha
            FechaSelector(
                fechaMillis = fechaMillis,
                onFechaSelected = onFechaChange,
                modifier = Modifier.weight(1f)
            )

            // Selector de Hora
            HoraSelector(
                fechaMillis = fechaMillis,
                onHoraSelected = onFechaChange,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider()

        // Prioridad
        Text("Prioridad", style = MaterialTheme.typography.titleSmall)
        Text(
            "¿Qué tan urgente es esta actividad?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Prioridad.entries.forEach { p ->
                FilterChip(
                    selected = prioridad == p,
                    onClick = { onPrioridadChange(p) },
                    label = { Text(obtenerTextoPrioridad(p)) }
                )
            }
        }

        HorizontalDivider()

        // Categoría
        Text("Categoría", style = MaterialTheme.typography.titleSmall)
        Text(
            "Organiza tus actividades por tipo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column {
            val categorias = Categoria.entries
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categorias.take(2).forEach { c ->
                    FilterChip(
                        selected = categoria == c,
                        onClick = { onCategoriaChange(c) },
                        label = { Text(c.displayName) }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categorias.drop(2).forEach { c ->
                    FilterChip(
                        selected = categoria == c,
                        onClick = { onCategoriaChange(c) },
                        label = { Text(c.displayName) }
                    )
                }
            }
        }

        HorizontalDivider()

        // Recordatorio
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (tieneRecordatorio) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRecordatorioChange(!tieneRecordatorio) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Recordatorio",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        if (tieneRecordatorio) {
                            "Te avisaremos antes de la hora"
                        } else {
                            "Actívalo para recibir alertas"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = tieneRecordatorio,
                    onCheckedChange = onRecordatorioChange
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // Espacio para el FAB
    }
}

@Composable
fun FechaSelector(
    fechaMillis: Long,
    onFechaSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = fechaMillis }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val newCalendar = Calendar.getInstance()
            newCalendar.timeInMillis = fechaMillis // Mantener hora actual
            newCalendar.set(Calendar.YEAR, year)
            newCalendar.set(Calendar.MONTH, month)
            newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            onFechaSelected(newCalendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    OutlinedTextField(
        value = sdf.format(Date(fechaMillis)),
        onValueChange = {},
        label = { Text("Fecha") },
        readOnly = true,
        trailingIcon = {
            Icon(
                Icons.Default.DateRange,
                contentDescription = "Seleccionar fecha"
            )
        },
        modifier = modifier.clickable { datePickerDialog.show() },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

// NUEVO: Selector de Hora
@Composable
fun HoraSelector(
    fechaMillis: Long,
    onHoraSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = fechaMillis }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay: Int, minute: Int ->
            val newCalendar = Calendar.getInstance()
            newCalendar.timeInMillis = fechaMillis // Mantener fecha actual
            newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            newCalendar.set(Calendar.MINUTE, minute)
            newCalendar.set(Calendar.SECOND, 0)
            onHoraSelected(newCalendar.timeInMillis)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // Formato 24 horas
    )

    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

    OutlinedTextField(
        value = sdf.format(Date(fechaMillis)),
        onValueChange = {},
        label = { Text("Hora") },
        readOnly = true,
        trailingIcon = {
            Icon(
                Icons.Default.AccessTime,
                contentDescription = "Seleccionar hora"
            )
        },
        modifier = modifier.clickable { timePickerDialog.show() },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

// Helper para mostrar texto más descriptivo en Prioridad
private fun obtenerTextoPrioridad(prioridad: Prioridad): String {
    return when (prioridad) {
        Prioridad.ALTA -> "🔴 Alta"
        Prioridad.MEDIA -> "🟡 Media"
        Prioridad.BAJA -> "🟢 Baja"
        Prioridad.URGENTE -> "URGENTE"
    }
}