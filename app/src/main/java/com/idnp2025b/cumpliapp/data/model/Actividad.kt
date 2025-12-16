// Actividad.kt - ACTUALIZADO
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
    val fechaCreacion: Long = System.currentTimeMillis(),
    val tiempoAcumulado: Long = 0L, // NUEVO: tiempo en milisegundos
    val enProgreso: Boolean = false // NUEVO: si está en modo enfoque activo
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
        fechaCreacion = fechaCreacion,
        tiempoAcumulado = tiempoAcumulado,
        enProgreso = enProgreso
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
        fechaCreacion = fechaCreacion,
        tiempoAcumulado = tiempoAcumulado,
        enProgreso = enProgreso
    )
}