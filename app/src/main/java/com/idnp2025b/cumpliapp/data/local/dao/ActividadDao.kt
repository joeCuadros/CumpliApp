package com.idnp2025b.cumpliapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.idnp2025b.cumpliapp.data.local.entity.ActividadEntity
import com.idnp2025b.cumpliapp.data.model.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    // INSERTAR
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActividad(actividad: ActividadEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActividades(actividades: List<ActividadEntity>)

    // ACTUALIZAR
    @Update
    suspend fun updateActividad(actividad: ActividadEntity)

    // ELIMINAR
    @Delete
    suspend fun deleteActividad(actividad: ActividadEntity)

    @Query("DELETE FROM actividades WHERE id = :actividadId")
    suspend fun deleteActividadById(actividadId: Int)

    @Query("DELETE FROM actividades WHERE completada = 1")
    suspend fun deleteActividadesCompletadas()

    // CONSULTAS BASICAS
    @Query("SELECT * FROM actividades WHERE id = :actividadId")
    suspend fun getActividadById(actividadId: Int): ActividadEntity?

    @Query("SELECT * FROM actividades WHERE id = :actividadId")
    fun getActividadByIdFlow(actividadId: Int): Flow<ActividadEntity?>

    @Query("SELECT * FROM actividades WHERE completada = 0")
    fun getActividadesPendientes(): Flow<List<ActividadEntity>>

    @Query("SELECT * FROM actividades WHERE completada = 1")
    fun getActividadesCompletadas(): Flow<List<ActividadEntity>>

    // ORDENAR POR FECHA COMPLETADA
    @Query("""
        SELECT * FROM actividades 
        WHERE completada = 1 
        ORDER BY fechaEntrega ASC
    """)
    fun getActividadesOrdenadasPorFechaCompletadas(): Flow<List<ActividadEntity>>
    // ORDENAR POR FECHA PENDIENTE
    @Query("""
        SELECT * FROM actividades 
        WHERE completada = 0 
        ORDER BY fechaEntrega ASC
    """)
    fun getActividadesOrdenadasPorFecha(): Flow<List<ActividadEntity>>

    // ORDENAR POR PRIORIDAD Y FECHA
    @Query("""
        SELECT * FROM actividades 
        WHERE completada = 0 
        ORDER BY prioridad ASC, fechaEntrega ASC
    """)
    fun getActividadesOrdenadasPorPrioridad(): Flow<List<ActividadEntity>>

    // FILTRAR POR CATEGORIA
    @Query("""
        SELECT * FROM actividades 
        WHERE categoria = :categoria AND completada = 0
        ORDER BY fechaEntrega ASC
    """)
    fun getActividadesPorCategoriaFecha(categoria: Categoria): Flow<List<ActividadEntity>>
    @Query("""
        SELECT * FROM actividades 
        WHERE categoria = :categoria AND completada = 0
        ORDER BY prioridad ASC, fechaEntrega ASC
    """)
    fun getActividadesPorCategoriaPrioridad(categoria: Categoria): Flow<List<ActividadEntity>>

    // ACTIVIDADES CON RECORDATORIO
    @Query("""
        SELECT * FROM actividades 
        WHERE tieneRecordatorio = 1 AND completada = 0
        ORDER BY fechaEntrega ASC
    """)
    fun getActividadesConRecordatorio(): Flow<List<ActividadEntity>>

    // BUSQUEDA
    @Query("""
        SELECT * FROM actividades 
        WHERE (titulo LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%')
        AND completada = 0
        ORDER BY prioridad ASC, fechaEntrega ASC
    """)
    fun buscarActividades(query: String): Flow<List<ActividadEntity>>

    // ESTADISTICAS
    @Query("SELECT COUNT(*) FROM actividades WHERE completada = 0")
    fun getCountActividadesPendientes(): Flow<Int>

    @Query("SELECT COUNT(*) FROM actividades WHERE categoria = :categoria AND completada = 0")
    fun getCountPorCategoria(categoria: Categoria): Flow<Int>

    // MARCAR COMO COMPLETADA
    @Query("UPDATE actividades SET completada = :completada WHERE id = :actividadId")
    suspend fun marcarComoCompletada(actividadId: Int, completada: Boolean)
}