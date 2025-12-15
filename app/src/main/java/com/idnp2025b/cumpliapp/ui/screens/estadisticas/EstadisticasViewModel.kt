package com.idnp2025b.cumpliapp.ui.screens.estadisticas

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idnp2025b.cumpliapp.data.model.Actividad
import com.idnp2025b.cumpliapp.data.model.Categoria
import com.idnp2025b.cumpliapp.domain.repository.InterfaceActividadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EstadisticasViewModel @Inject constructor(
    repository: InterfaceActividadRepository
) : ViewModel() {

    val uiState: StateFlow<EstadisticasUiState> = combine(
        repository.getActividadesPendientes(),
        repository.getActividadesCompletadas()
    ) { pendientes, completadas ->
        calcularEstadisticas(pendientes, completadas)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EstadisticasUiState.Loading
    )

    private fun calcularEstadisticas(
        pendientes: List<Actividad>,
        completadas: List<Actividad>
    ): EstadisticasUiState {
        val totalPendientes = pendientes.size
        val totalCompletadas = completadas.size
        val totalGlobal = totalPendientes + totalCompletadas

        val porcentajeProgreso = if (totalGlobal > 0) {
            (totalCompletadas.toFloat() / totalGlobal.toFloat())
        } else 0f

        val todas = pendientes + completadas
        val porCategoria = todas
            .groupBy { it.categoria }
            .mapValues { entry -> entry.value.size }

        val datosGrafico = porCategoria.map { (cat, cantidad) ->
            DatoCategoria(
                categoria = cat,
                cantidad = cantidad,
                color = getColorPorCategoria(cat),
                porcentaje = if (totalGlobal > 0) cantidad.toFloat() / totalGlobal else 0f
            )
        }.sortedByDescending { it.cantidad }

        return EstadisticasUiState.Success(
            totalPendientes = totalPendientes,
            totalCompletadas = totalCompletadas,
            porcentajeProgreso = porcentajeProgreso,
            datosPorCategoria = datosGrafico
        )
    }

    private fun getColorPorCategoria(categoria: Categoria): Color {
        return when (categoria) {
            Categoria.UNIVERSIDAD -> Color(0xFF42A5F5)
            Categoria.CASA -> Color(0xFF66BB6A)
            Categoria.TRABAJO -> Color(0xFFFFA726)
            Categoria.OTROS -> Color(0xFFAB47BC)
        }
    }
}

// Estados de la UI
sealed class EstadisticasUiState {
    object Loading : EstadisticasUiState()
    // AQUÍ ESTÁN LAS VARIABLES QUE NO ENCONTRABA
    data class Success(
        val totalPendientes: Int,
        val totalCompletadas: Int,
        val porcentajeProgreso: Float,
        val datosPorCategoria: List<DatoCategoria>
    ) : EstadisticasUiState()
}

data class DatoCategoria(
    val categoria: Categoria,
    val cantidad: Int,
    val color: Color,
    val porcentaje: Float
)