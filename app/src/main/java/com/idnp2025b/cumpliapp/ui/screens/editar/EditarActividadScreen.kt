package com.idnp2025b.cumpliapp.ui.screens.editar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idnp2025b.cumpliapp.ui.components.FormularioActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarActividadScreen(
    onNavigateUp: () -> Unit,
    viewModel: EditarActividadViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val titulo by viewModel.titulo.collectAsState()
    val descripcion by viewModel.descripcion.collectAsState()
    val fecha by viewModel.fechaEntrega.collectAsState()
    val prioridad by viewModel.prioridad.collectAsState()
    val categoria by viewModel.categoria.collectAsState()
    val recordatorio by viewModel.tieneRecordatorio.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is EditarActividadViewModel.UiEvent.SaveSuccess -> onNavigateUp()
                is EditarActividadViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = { Text("Editar Actividad") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.actualizarActividad() }) {
                Icon(Icons.Default.Check, contentDescription = "Guardar cambios")
            }
        }
    ) { padding ->
        // Si la fecha es 0 significa que aún no carga la data, podríamos mostrar un Loading
        if (fecha == 0L) {
            // Opcional: Box con CircularProgressIndicator
        } else {
            FormularioActividad(
                modifier = Modifier.padding(padding),
                titulo = titulo, onTituloChange = viewModel::onTituloChange,
                descripcion = descripcion, onDescripcionChange = viewModel::onDescripcionChange,
                fechaMillis = fecha, onFechaChange = viewModel::onFechaChange,
                prioridad = prioridad, onPrioridadChange = viewModel::onPrioridadChange,
                categoria = categoria, onCategoriaChange = viewModel::onCategoriaChange,
                tieneRecordatorio = recordatorio, onRecordatorioChange = viewModel::onRecordatorioChange
            )
        }
    }
}