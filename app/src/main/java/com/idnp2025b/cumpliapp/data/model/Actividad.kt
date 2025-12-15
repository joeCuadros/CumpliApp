package com.idnp2025b.cumpliapp.data.model

import com.idnp2025b.cumpliapp.data.local.entity.ActividadEntity

data class Actividad (
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val fechaEntrega: Long,
    val tieneRecordatorio: Boolean,
    val prioridad: Prioridad,
    val categoria: Categoria,
    val completada: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()
)

fun ActividadEntity.toActividad(): Actividad {
    return Actividad(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        fechaEntrega = fechaEntrega,
        tieneRecordatorio = tieneRecordatorio,
        prioridad = prioridad,
        categoria = categoria,
        completada = completada,
        fechaCreacion = fechaCreacion
    )
}
fun Actividad.toEntity(): ActividadEntity {
    return ActividadEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        fechaEntrega = fechaEntrega,
        tieneRecordatorio = tieneRecordatorio,
        prioridad = prioridad,
        categoria = categoria,
        completada = completada,
        fechaCreacion = fechaCreacion
    )
}