package com.idnp2025b.cumpliapp.ui.screens.crear

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
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CrearActividadViewModel @Inject constructor(
    private val repository: InterfaceActividadRepository
) : ViewModel() {

    private val _titulo = MutableStateFlow("")
    val titulo = _titulo.asStateFlow()

    private val _descripcion = MutableStateFlow("")
    val descripcion = _descripcion.asStateFlow()

    private val _fechaEntrega = MutableStateFlow(obtenerFechaManana())
    val fechaEntrega = _fechaEntrega.asStateFlow()

    private val _prioridad = MutableStateFlow(Prioridad.MEDIA)
    val prioridad = _prioridad.asStateFlow()

    private val _categoria = MutableStateFlow(Categoria.UNIVERSIDAD)
    val categoria = _categoria.asStateFlow()

    private val _tieneRecordatorio = MutableStateFlow(false)
    val tieneRecordatorio = _tieneRecordatorio.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onTituloChange(text: String) { _titulo.value = text }
    fun onDescripcionChange(text: String) { _descripcion.value = text }
    fun onFechaChange(fecha: Long) { _fechaEntrega.value = fecha }
    fun onPrioridadChange(p: Prioridad) { _prioridad.value = p }
    fun onCategoriaChange(c: Categoria) { _categoria.value = c }
    fun onRecordatorioChange(checked: Boolean) { _tieneRecordatorio.value = checked }

    fun guardarActividad() {
        if (_titulo.value.isBlank()) {
            viewModelScope.launch { _uiEvent.send(UiEvent.ShowSnackbar("El título es obligatorio")) }
            return
        }

        viewModelScope.launch {
            val nuevaActividad = Actividad(
                titulo = _titulo.value,
                descripcion = _descripcion.value,
                fechaEntrega = _fechaEntrega.value,
                prioridad = _prioridad.value,
                categoria = _categoria.value,
                tieneRecordatorio = _tieneRecordatorio.value,
                completada = false
            )
            repository.insertActividad(nuevaActividad)
            _uiEvent.send(UiEvent.SaveSuccess)
        }
    }

    private fun obtenerFechaManana(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        return calendar.timeInMillis
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveSuccess : UiEvent()
    }
}