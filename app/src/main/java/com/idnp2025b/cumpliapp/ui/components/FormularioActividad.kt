package com.idnp2025b.cumpliapp.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // Descripción
        OutlinedTextField(
            value = descripcion,
            onValueChange = onDescripcionChange,
            label = { Text("Descripción (opcional)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            maxLines = 5
        )

        HorizontalDivider()

        // Selector de Fecha
        FechaSelector(
            fechaMillis = fechaMillis,
            onFechaSelected = onFechaChange
        )

        HorizontalDivider()

        // Prioridad
        Text("Prioridad", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Prioridad.entries.forEach { p ->
                FilterChip(
                    selected = prioridad == p,
                    onClick = { onPrioridadChange(p) },
                    label = { Text(p.name) }
                )
            }
        }

        HorizontalDivider()

        // Categoría
        Text("Categoría", style = MaterialTheme.typography.titleSmall)
        // Diseño adaptativo simple
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Recordarme antes", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = tieneRecordatorio,
                onCheckedChange = onRecordatorioChange
            )
        }

        Spacer(modifier = Modifier.height(80.dp)) // Espacio para el FAB
    }
}

@Composable
fun FechaSelector(fechaMillis: Long, onFechaSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = fechaMillis }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(year, month, dayOfMonth)
            // Mantener hora actual por defecto si se desea, o resetear a 9am
            newCalendar.set(Calendar.HOUR_OF_DAY, 9)
            newCalendar.set(Calendar.MINUTE, 0)
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
        label = { Text("Fecha de entrega") },
        readOnly = true,
        trailingIcon = { Icon(Icons.Default.DateRange, null) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}