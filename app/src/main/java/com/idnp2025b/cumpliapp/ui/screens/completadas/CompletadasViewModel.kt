package com.idnp2025b.cumpliapp.ui.screens.completadas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idnp2025b.cumpliapp.data.model.Actividad
import com.idnp2025b.cumpliapp.domain.repository.InterfaceActividadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompletadasViewModel @Inject constructor(
    private val repository: InterfaceActividadRepository
) : ViewModel() {

    // Observamos directamente la query del repositorio
    val actividadesCompletadas = repository.getActividadesOrdenadasPorFechaCompletadas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estado para el diálogo de confirmación de eliminación
    private val _actividadAEliminar = MutableStateFlow<Actividad?>(null)
    val actividadAEliminar = _actividadAEliminar.asStateFlow()

    // Eventos UI (Snackbars)
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Estado UI derivado para mostrar Loading/Empty/List
    val uiState = actividadesCompletadas.map { lista ->
        if (lista.isEmpty()) CompletadasUiState.Empty else CompletadasUiState.Success(lista)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CompletadasUiState.Loading
    )

    // --- ACCIONES ---

    fun reactivarActividad(actividad: Actividad) {
        viewModelScope.launch {
            // Al pasar 'false', la actividad deja de estar completada y vuelve a la lista principal
            repository.marcarComoCompletada(actividad.id, false)
            _uiEvent.send(UiEvent.ShowSnackbar("Actividad reactivada"))
        }
    }

    fun mostrarDialogoEliminar(actividad: Actividad) {
        _actividadAEliminar.value = actividad
    }

    fun ocultarDialogoEliminar() {
        _actividadAEliminar.value = null
    }

    fun eliminarActividadDefinitivamente(actividad: Actividad) {
        viewModelScope.launch {
            repository.deleteActividad(actividad)
            _actividadAEliminar.value = null
            _uiEvent.send(UiEvent.ShowSnackbar("Actividad eliminada definitivamente"))
        }
    }

    // Eventos
    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }

    // Estados
    sealed class CompletadasUiState {
        object Loading : CompletadasUiState()
        object Empty : CompletadasUiState()
        data class Success(val actividades: List<Actividad>) : CompletadasUiState()
    }
}