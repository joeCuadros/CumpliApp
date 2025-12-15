package com.idnp2025b.cumpliapp.ui.screens.editar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idnp2025b.cumpliapp.data.model.Actividad
import com.idnp2025b.cumpliapp.data.model.Categoria
import com.idnp2025b.cumpliapp.data.model.Prioridad
import com.idnp2025b.cumpliapp.domain.repository.InterfaceActividadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarActividadViewModel @Inject constructor(
    private val repository: InterfaceActividadRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val actividadId: Int = checkNotNull(savedStateHandle["id"])

    // Estado local para saber si cargó la info
    private var _actividadCargada: Actividad? = null

    private val _titulo = MutableStateFlow("")
    val titulo = _titulo.asStateFlow()

    private val _descripcion = MutableStateFlow("")
    val descripcion = _descripcion.asStateFlow()

    private val _fechaEntrega = MutableStateFlow(0L)
    val fechaEntrega = _fechaEntrega.asStateFlow()

    private val _prioridad = MutableStateFlow(Prioridad.MEDIA)
    val prioridad = _prioridad.asStateFlow()

    private val _categoria = MutableStateFlow(Categoria.OTROS)
    val categoria = _categoria.asStateFlow()

    private val _tieneRecordatorio = MutableStateFlow(false)
    val tieneRecordatorio = _tieneRecordatorio.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        cargarActividad()
    }

    private fun cargarActividad() {
        viewModelScope.launch {
            repository.getActividadById(actividadId)?.let { actividad ->
                _actividadCargada = actividad
                _titulo.value = actividad.titulo
                _descripcion.value = actividad.descripcion
                _fechaEntrega.value = actividad.fechaEntrega
                _prioridad.value = actividad.prioridad
                _categoria.value = actividad.categoria
                _tieneRecordatorio.value = actividad.tieneRecordatorio
            } ?: run {
                _uiEvent.send(UiEvent.ShowSnackbar("Error al cargar la actividad"))
            }
        }
    }

    fun onTituloChange(text: String) { _titulo.value = text }
    fun onDescripcionChange(text: String) { _descripcion.value = text }
    fun onFechaChange(fecha: Long) { _fechaEntrega.value = fecha }
    fun onPrioridadChange(p: Prioridad) { _prioridad.value = p }
    fun onCategoriaChange(c: Categoria) { _categoria.value = c }
    fun onRecordatorioChange(checked: Boolean) { _tieneRecordatorio.value = checked }

    fun actualizarActividad() {
        if (_titulo.value.isBlank()) {
            viewModelScope.launch { _uiEvent.send(UiEvent.ShowSnackbar("El título es obligatorio")) }
            return
        }

        viewModelScope.launch {
            _actividadCargada?.let { original ->
                val actividadActualizada = original.copy(
                    titulo = _titulo.value,
                    descripcion = _descripcion.value,
                    fechaEntrega = _fechaEntrega.value,
                    prioridad = _prioridad.value,
                    categoria = _categoria.value,
                    tieneRecordatorio = _tieneRecordatorio.value
                )
                repository.updateActividad(actividadActualizada)
                _uiEvent.send(UiEvent.SaveSuccess)
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveSuccess : UiEvent()
    }
}