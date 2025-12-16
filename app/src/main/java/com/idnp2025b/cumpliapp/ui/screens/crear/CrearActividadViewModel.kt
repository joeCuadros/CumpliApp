package com.idnp2025b.cumpliapp.ui.screens.crear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idnp2025b.cumpliapp.data.local.preferences.PreferencesManager
import com.idnp2025b.cumpliapp.data.model.Actividad
import com.idnp2025b.cumpliapp.data.model.Categoria
import com.idnp2025b.cumpliapp.data.model.Prioridad
import com.idnp2025b.cumpliapp.domain.repository.InterfaceActividadRepository
import com.idnp2025b.cumpliapp.util.RecordatorioManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CrearActividadViewModel @Inject constructor(
    private val repository: InterfaceActividadRepository,
    private val recordatorioManager: RecordatorioManager,
    private val preferencesManager: PreferencesManager
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
            val actividadId = repository.insertActividad(nuevaActividad).toInt()

            // NUEVO: Programar recordatorio si está activado
            if (_tieneRecordatorio.value) {
                val prefs = preferencesManager.preferencesFlow.first()
                val actividadConId = nuevaActividad.copy(id = actividadId)
                recordatorioManager.programarRecordatorio(
                    actividadConId,
                    prefs.recordatorioMinutos
                )
            }

            _uiEvent.send(UiEvent.SaveSuccess)
        }
    }

    private fun obtenerFechaManana(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        // Hora por defecto: 8:00 AM (hora común para inicio de clases/trabajo)
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveSuccess : UiEvent()
    }
}