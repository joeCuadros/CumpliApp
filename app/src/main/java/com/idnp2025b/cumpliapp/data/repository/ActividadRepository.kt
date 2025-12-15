package com.idnp2025b.cumpliapp.data.repository

import com.idnp2025b.cumpliapp.data.local.dao.ActividadDao
import com.idnp2025b.cumpliapp.data.model.Actividad
import com.idnp2025b.cumpliapp.data.model.Categoria
import com.idnp2025b.cumpliapp.data.model.toActividad
import com.idnp2025b.cumpliapp.data.model.toEntity
import com.idnp2025b.cumpliapp.domain.repository.InterfaceActividadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ActividadRepository  @Inject constructor(
    private val actividadDao: ActividadDao
) : InterfaceActividadRepository{
    // CRUD
    override suspend fun insertActividad(actividad: Actividad): Long {
        return actividadDao.insertActividad(actividad.toEntity())
    }

    override suspend fun updateActividad(actividad: Actividad) {
        actividadDao.updateActividad(actividad.toEntity())
    }

    override suspend fun deleteActividad(actividad: Actividad) {
        actividadDao.deleteActividad(actividad.toEntity())
    }

    override suspend fun deleteActividadById(actividadId: Int) {
        actividadDao.deleteActividadById(actividadId)
    }

    override suspend fun getActividadById(actividadId: Int): Actividad? {
        return actividadDao.getActividadById(actividadId)?.toActividad()
    }
    // Consultas
    override fun getActividadesPendientes(): Flow<List<Actividad>> {
        return actividadDao.getActividadesPendientes()
            .map { list -> list.map { it.toActividad() } }
    }

    override fun getActividadesCompletadas(): Flow<List<Actividad>> {
        return actividadDao.getActividadesCompletadas()
            .map { list -> list.map { it.toActividad() } }
    }

    override fun getActividadesOrdenadasPorFecha(): Flow<List<Actividad>> {
        return actividadDao.getActividadesOrdenadasPorFecha()
            .map { list -> list.map { it.toActividad() } }
    }

    override fun getActividadesOrdenadasPorPrioridad(): Flow<List<Actividad>> {
        return actividadDao.getActividadesOrdenadasPorPrioridad()
            .map { list -> list.map { it.toActividad() } }
    }

    override fun getActividadesOrdenadasPorFechaCompletadas(): Flow<List<Actividad>> {
        return actividadDao.getActividadesOrdenadasPorFechaCompletadas()
            .map { list -> list.map { it.toActividad() } }
    }
    // Filtros
    override fun getActividadesPorCategoriaFecha(categoria: Categoria): Flow<List<Actividad>> {
        return actividadDao.getActividadesPorCategoriaFecha(categoria)
            .map { list -> list.map { it.toActividad() } }
    }

    override fun getActividadesPorCategoriaPriorida(categoria: Categoria): Flow<List<Actividad>> {
        return actividadDao.getActividadesPorCategoriaPrioridad(categoria)
            .map { list -> list.map { it.toActividad() } }
    }

    override fun getActividadesConRecordatorio(): Flow<List<Actividad>> {
        return actividadDao.getActividadesConRecordatorio()
            .map { list -> list.map { it.toActividad() } }
    }
    // Busqueda
    override fun buscarActividadesPrioridad(query: String): Flow<List<Actividad>> {
        return actividadDao.buscarActividadesPrioridad(query)
            .map { list -> list.map { it.toActividad() } }
    }
    override fun buscarActividadesFecha(query: String): Flow<List<Actividad>> {
        return actividadDao.buscarActividadesFecha(query)
            .map { list -> list.map { it.toActividad() } }
    }
    // Estadísticas
    override fun getCountActividadesPendientes(): Flow<Int> {
        return actividadDao.getCountActividadesPendientes()
    }

    override fun getCountPorCategoria(categoria: Categoria): Flow<Int> {
        return actividadDao.getCountPorCategoria(categoria)
    }
    // Acciones
    override suspend fun marcarComoCompletada(actividadId: Int, completada: Boolean) {
        actividadDao.marcarComoCompletada(actividadId, completada)
    }

    override suspend fun deleteActividadesCompletadas() {
        actividadDao.deleteActividadesCompletadas()
    }
}
