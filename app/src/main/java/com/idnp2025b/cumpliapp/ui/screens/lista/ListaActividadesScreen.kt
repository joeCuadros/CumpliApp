package com.idnp2025b.cumpliapp.ui.screens.lista

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.idnp2025b.cumpliapp.data.model.Actividad
import com.idnp2025b.cumpliapp.data.model.Categoria
import com.idnp2025b.cumpliapp.ui.components.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaActividadesScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: ListaActividadesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val countPendientes by viewModel.countPendientes.collectAsState()
    val filtroCategoria by viewModel.filtroCategoria.collectAsState()
    val ordenamiento by viewModel.ordenamiento.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()
    val soloConRecordatorio by viewModel.soloConRecordatorio.collectAsState()
    val mostrarDialogoFiltros by viewModel.mostrarDialogoFiltros.collectAsState()
    val actividadAEliminar by viewModel.actividadAEliminar.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ListaActividadesViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                // --- CAMBIO CLAVE AQUÍ: Eliminamos el relleno de la barra de estado ---
                windowInsets = WindowInsets(0.dp),
                title = {
                    Column {
                        Text("Mis Actividades", style = MaterialTheme.typography.titleLarge)
                        if (countPendientes > 0) {
                            Text(
                                text = "$countPendientes pendientes",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleDialogoFiltros() }) {
                        val hayFiltros = filtroCategoria != null || soloConRecordatorio || ordenamiento != TipoOrdenamiento.POR_PRIORIDAD
                        if (hayFiltros) {
                            BadgedBox(badge = { Badge() }) {
                                Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                            }
                        } else {
                            Icon(Icons.Default.FilterList, contentDescription = "Filtros")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar actividad")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barra de Búsqueda
            SearchBar(
                query = busqueda,
                onQueryChange = { viewModel.setBusqueda(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Filtros
            FiltrosActivosRow(
                filtroCategoria = filtroCategoria,
                ordenamiento = ordenamiento,
                soloConRecordatorio = soloConRecordatorio,
                onLimpiarFiltros = { viewModel.limpiarFiltros() }
            )

            // Contenido
            when (val state = uiState) {
                is ListaUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ListaUiState.Empty -> {
                    EmptyState(mensaje = "No tienes actividades pendientes", icono = Icons.Default.CheckCircle)
                }
                is ListaUiState.EmptySearch -> {
                    EmptyState(mensaje = "No se encontraron resultados", icono = Icons.Default.Search)
                }
                is ListaUiState.Success -> {
                    ActividadesLista(
                        actividades = state.actividades,
                        onActividadClick = { onNavigateToEdit(it) },
                        onCompletarClick = { viewModel.marcarComoCompletada(it) },
                        onEliminarSwipe = { viewModel.mostrarDialogoEliminar(it) }
                    )
                }
            }
        }
    }

    DialogoFiltros(
        mostrar = mostrarDialogoFiltros,
        filtroActual = filtroCategoria,
        ordenamientoActual = ordenamiento,
        soloConRecordatorio = soloConRecordatorio,
        onDismiss = { viewModel.toggleDialogoFiltros() },
        onFiltroSeleccionado = { viewModel.setFiltroCategoria(it) },
        onOrdenamientoSeleccionado = { viewModel.setOrdenamiento(it) },
        onRecordatorioChange = { viewModel.setSoloConRecordatorio(it) },
        onLimpiarFiltros = { viewModel.limpiarFiltros() }
    )

    DialogoConfirmacion(
        mostrar = actividadAEliminar != null,
        titulo = "Eliminar actividad",
        mensaje = "¿Estás seguro de que deseas eliminar \"${actividadAEliminar?.titulo}\"?",
        onConfirmar = { actividadAEliminar?.let { viewModel.eliminarActividad(it) } },
        onDismiss = { viewModel.ocultarDialogoEliminar() },
        textoConfirmar = "Eliminar",
        textoCancelar = "Cancelar"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActividadesLista(
    actividades: List<Actividad>,
    onActividadClick: (Int) -> Unit,
    onCompletarClick: (Actividad) -> Unit,
    onEliminarSwipe: (Actividad) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        // Ajuste fino: Reducimos el espacio superior a 4dp
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = actividades, key = { it.id }) { actividad ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onEliminarSwipe(actividad)
                        false
                    } else false
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    val color by animateColorAsState(
                        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                            Color(0xFFFFEBEE)
                        else Color.Transparent,
                        label = "color"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color, shape = CardDefaults.shape)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color(0xFFB71C1C)
                        )
                    }
                },
                content = {
                    ActividadCard(
                        actividad = actividad,
                        onActividadClick = { onActividadClick(actividad.id) },
                        onCompletarClick = { onCompletarClick(actividad) },
                        onEliminarClick = { onEliminarSwipe(actividad) }
                    )
                }
            )
        }
    }
}

@Composable
private fun FiltrosActivosRow(
    filtroCategoria: Categoria?,
    ordenamiento: TipoOrdenamiento,
    soloConRecordatorio: Boolean,
    onLimpiarFiltros: () -> Unit
) {
    val hayFiltros = filtroCategoria != null ||
            ordenamiento != TipoOrdenamiento.POR_PRIORIDAD ||
            soloConRecordatorio

    if (hayFiltros) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filtros:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            if (filtroCategoria != null) AssistChip(onClick = {}, label = { Text(filtroCategoria.displayName) })
            if (ordenamiento == TipoOrdenamiento.POR_FECHA) AssistChip(onClick = {}, label = { Text("Por fecha") })
            if (soloConRecordatorio) AssistChip(onClick = {}, label = { Text("🔔") })
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onLimpiarFiltros, contentPadding = PaddingValues(0.dp)) {
                Text("Limpiar", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}