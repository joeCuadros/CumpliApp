package com.idnp2025b.cumpliapp.ui.screens.completadas

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idnp2025b.cumpliapp.data.model.Actividad
import com.idnp2025b.cumpliapp.ui.components.ActividadCard
import com.idnp2025b.cumpliapp.ui.components.DialogoConfirmacion
import com.idnp2025b.cumpliapp.ui.components.EmptyState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletadasScreen(
    viewModel: CompletadasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val actividadAEliminar by viewModel.actividadAEliminar.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CompletadasViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial Completado") },
                windowInsets = WindowInsets(0.dp) // Mantenemos consistencia visual
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is CompletadasViewModel.CompletadasUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CompletadasViewModel.CompletadasUiState.Empty -> {
                    EmptyState(
                        mensaje = "Aún no has completado ninguna actividad",
                        icono = Icons.Default.CheckCircle
                    )
                }
                is CompletadasViewModel.CompletadasUiState.Success -> {
                    ListaCompletadas(
                        actividades = state.actividades,
                        onReactivar = { viewModel.reactivarActividad(it) },
                        onEliminar = { viewModel.mostrarDialogoEliminar(it) }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para borrado definitivo
    DialogoConfirmacion(
        mostrar = actividadAEliminar != null,
        titulo = "Eliminar definitivamente",
        mensaje = "¿Borrar esta actividad del historial? No podrás recuperarla.",
        onConfirmar = {
            actividadAEliminar?.let { viewModel.eliminarActividadDefinitivamente(it) }
        },
        onDismiss = { viewModel.ocultarDialogoEliminar() },
        textoConfirmar = "Borrar"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListaCompletadas(
    actividades: List<Actividad>,
    onReactivar: (Actividad) -> Unit,
    onEliminar: (Actividad) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = actividades, key = { it.id }) { actividad ->

            // Swipe to Delete (Deslizar para borrar)
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onEliminar(actividad)
                        false
                    } else false
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    val color by animateColorAsState(
                        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                            MaterialTheme.colorScheme.errorContainer
                        else Color.Transparent,
                        label = "color"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color, CardDefaults.shape)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                },
                content = {
                    // Reutilizamos ActividadCard
                    // onActividadClick está vacío porque en completadas usualmente no se edita,
                    // pero podrías navegar a editar si quisieras.
                    ActividadCard(
                        actividad = actividad,
                        onActividadClick = { },
                        onCompletarClick = { onReactivar(actividad) }, // Esto la reactiva
                        onEliminarClick = { onEliminar(actividad) }
                    )
                }
            )
        }
    }
}