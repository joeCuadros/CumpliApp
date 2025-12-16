package com.idnp2025b.cumpliapp.ui.screens.lista

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.idnp2025b.cumpliapp.data.model.Actividad
import com.idnp2025b.cumpliapp.data.model.Categoria
import com.idnp2025b.cumpliapp.domain.repository.InterfaceActividadRepository
import com.idnp2025b.cumpliapp.service.EnfoqueService
import com.idnp2025b.cumpliapp.util.RecordatorioManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListaActividadesViewModel @Inject constructor(
    application: Application,
    private val repository: InterfaceActividadRepository,
    private val recordatorioManager: RecordatorioManager
) : AndroidViewModel(application) {

    private val context = application.applicationContext

    // Estados de filtros y ordenamiento
    private val _filtroCategoria = MutableStateFlow<Categoria?>(null)
    val filtroCategoria = _filtroCategoria.asStateFlow()

    private val _ordenamiento = MutableStateFlow(TipoOrdenamiento.POR_PRIORIDAD)
    val ordenamiento = _ordenamiento.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda = _busqueda.asStateFlow()

    private val _soloConRecordatorio = MutableStateFlow(false)
    val soloConRecordatorio = _soloConRecordatorio.asStateFlow()

    // Diálogos
    private val _mostrarDialogoFiltros = MutableStateFlow(false)
    val mostrarDialogoFiltros = _mostrarDialogoFiltros.asStateFlow()

    private val _actividadAEliminar = MutableStateFlow<Actividad?>(null)
    val actividadAEliminar = _actividadAEliminar.asStateFlow()

    // Lista de actividades con todos los filtros combinados
    val actividades: StateFlow<List<Actividad>> = combine(
        _filtroCategoria,
        _ordenamiento,
        _busqueda,
        _soloConRecordatorio
    ) { categoria, orden, busqueda, soloRecordatorio ->
        FiltrosCombinados(categoria, orden, busqueda, soloRecordatorio)
    }.flatMapLatest { filtros ->
        obtenerActividadesConFiltros(filtros)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Contador de pendientes
    val countPendientes: StateFlow<Int> = repository.getCountActividadesPendientes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Estado de UI
    val uiState: StateFlow<ListaUiState> = actividades.map { lista ->
        when {
            lista.isEmpty() && _busqueda.value.isBlank() -> ListaUiState.Empty
            lista.isEmpty() && _busqueda.value.isNotBlank() -> ListaUiState.EmptySearch
            else -> ListaUiState.Success(lista)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListaUiState.Loading
    )

    // Eventos
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private fun obtenerActividadesConFiltros(filtros: FiltrosCombinados): Flow<List<Actividad>> {
        if (filtros.busqueda.isNotBlank()) {
            return when (filtros.ordenamiento) {
                TipoOrdenamiento.POR_FECHA -> repository.buscarActividadesFecha(filtros.busqueda)
                TipoOrdenamiento.POR_PRIORIDAD -> repository.buscarActividadesPrioridad(filtros.busqueda)
            }
        }

        if (filtros.soloConRecordatorio) {
            return repository.getActividadesConRecordatorio()
        }

        if (filtros.categoria != null) {
            return when (filtros.ordenamiento) {
                TipoOrdenamiento.POR_FECHA ->
                    repository.getActividadesPorCategoriaFecha(filtros.categoria)
                TipoOrdenamiento.POR_PRIORIDAD ->
                    repository.getActividadesPorCategoriaPrioridad(filtros.categoria)
            }
        }

        return when (filtros.ordenamiento) {
            TipoOrdenamiento.POR_FECHA -> repository.getActividadesOrdenadasPorFecha()
            TipoOrdenamiento.POR_PRIORIDAD -> repository.getActividadesOrdenadasPorPrioridad()
        }
    }

    // ============ ACCIONES ============

    fun setFiltroCategoria(categoria: Categoria?) {
        _filtroCategoria.value = categoria
    }

    fun setOrdenamiento(tipo: TipoOrdenamiento) {
        _ordenamiento.value = tipo
    }

    fun setBusqueda(query: String) {
        _busqueda.value = query
    }

    fun setSoloConRecordatorio(solo: Boolean) {
        _soloConRecordatorio.value = solo
    }

    fun toggleDialogoFiltros() {
        _mostrarDialogoFiltros.value = !_mostrarDialogoFiltros.value
    }

    fun limpiarFiltros() {
        _filtroCategoria.value = null
        _ordenamiento.value = TipoOrdenamiento.POR_PRIORIDAD
        _busqueda.value = ""
        _soloConRecordatorio.value = false
        _mostrarDialogoFiltros.value = false
    }

    fun marcarComoCompletada(actividad: Actividad) {
        viewModelScope.launch {
            repository.marcarComoCompletada(actividad.id, !actividad.completada)

            // Cancelar recordatorio si se completa
            if (!actividad.completada) {
                recordatorioManager.cancelarRecordatorio(actividad.id)
            }

            val mensaje = if (!actividad.completada) {
                "Actividad completada ✓"
            } else {
                "Actividad marcada como pendiente"
            }
            _eventFlow.emit(UiEvent.ShowSnackbar(mensaje))
        }
    }

    fun mostrarDialogoEliminar(actividad: Actividad) {
        _actividadAEliminar.value = actividad
    }

    fun ocultarDialogoEliminar() {
        _actividadAEliminar.value = null
    }

    fun eliminarActividad(actividad: Actividad) {
        viewModelScope.launch {
            repository.deleteActividad(actividad)
            recordatorioManager.cancelarRecordatorio(actividad.id)
            _actividadAEliminar.value = null
            _eventFlow.emit(UiEvent.ShowSnackbar("Actividad eliminada"))
        }
    }

    // NUEVO: Manejo del Modo Enfoque
    fun toggleModoEnfoque(actividad: Actividad) {
        viewModelScope.launch {
            if (actividad.enProgreso) {
                // Pausar el servicio
                EnfoqueService.pauseService(context)
                _eventFlow.emit(UiEvent.ShowSnackbar("Modo enfoque pausado"))
            } else {
                // Verificar si hay otra actividad en progreso
                val actividadEnProgreso = repository.getActividadEnProgreso()
                if (actividadEnProgreso != null && actividadEnProgreso.id != actividad.id) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Ya hay una actividad en modo enfoque"))
                    return@launch
                }

                // Iniciar el servicio
                EnfoqueService.startService(
                    context,
                    actividad.id,
                    actividad.titulo,
                    actividad.tiempoAcumulado
                )
                _eventFlow.emit(UiEvent.ShowSnackbar("Modo enfoque iniciado"))
            }
        }
    }

    // Eventos de UI
    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

// Data classes
data class FiltrosCombinados(
    val categoria: Categoria?,
    val ordenamiento: TipoOrdenamiento,
    val busqueda: String,
    val soloConRecordatorio: Boolean
)

enum class TipoOrdenamiento {
    POR_FECHA,
    POR_PRIORIDAD
}

sealed class ListaUiState {
    object Loading : ListaUiState()
    object Empty : ListaUiState()
    object EmptySearch : ListaUiState()
    data class Success(val actividades: List<Actividad>) : ListaUiState()
}