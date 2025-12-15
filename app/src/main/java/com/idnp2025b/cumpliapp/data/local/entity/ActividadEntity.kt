package com.idnp2025b.cumpliapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.idnp2025b.cumpliapp.data.model.Categoria
import com.idnp2025b.cumpliapp.data.model.Prioridad

@Entity(tableName = "actividades")
data class ActividadEntity(
    @PrimaryKey(autoGenerate = true)
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

