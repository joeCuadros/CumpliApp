package com.idnp2025b.cumpliapp.domain.repository

import com.idnp2025b.cumpliapp.data.model.Actividad
import com.idnp2025b.cumpliapp.data.model.Categoria
import kotlinx.coroutines.flow.Flow

interface  InterfaceActividadRepository {
    // CRUD
    suspend fun insertActividad(actividad: Actividad): Long
    suspend fun updateActividad(actividad: Actividad)
    suspend fun deleteActividad(actividad: Actividad)
    suspend fun deleteActividadById(actividadId: Int)
    suspend fun getActividadById(actividadId: Int): Actividad?
    // Consultas
    fun getActividadesPendientes(): Flow<List<Actividad>>
    fun getActividadesCompletadas(): Flow<List<Actividad>>
    fun getActividadesOrdenadasPorFecha(): Flow<List<Actividad>>
    fun getActividadesOrdenadasPorPrioridad(): Flow<List<Actividad>>
    fun getActividadesOrdenadasPorFechaCompletadas(): Flow<List<Actividad>>
    // Filtros
    fun getActividadesPorCategoriaFecha(categoria: Categoria): Flow<List<Actividad>>
    fun getActividadesPorCategoriaPriorida(categoria: Categoria): Flow<List<Actividad>>
    fun getActividadesConRecordatorio(): Flow<List<Actividad>>
    // Busqueda
    fun buscarActividades(query: String): Flow<List<Actividad>>
    // Estadísticas
    fun getCountActividadesPendientes(): Flow<Int>
    fun getCountPorCategoria(categoria: Categoria): Flow<Int>
    // Acciones
    suspend fun marcarComoCompletada(actividadId: Int, completada: Boolean)
    suspend fun deleteActividadesCompletadas()
}