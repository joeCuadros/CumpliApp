package com.idnp2025b.cumpliapp.ui.screens.crear

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idnp2025b.cumpliapp.ui.components.FormularioActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearActividadScreen(
    onNavigateUp: () -> Unit,
    viewModel: CrearActividadViewModel = hiltViewModel()
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
                is CrearActividadViewModel.UiEvent.SaveSuccess -> onNavigateUp()
                is CrearActividadViewModel.UiEvent.ShowSnackbar -> {
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
                title = { Text("Nueva Actividad") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.guardarActividad() }) {
                Icon(Icons.Default.Save, contentDescription = "Guardar")
            }
        }
    ) { padding ->
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