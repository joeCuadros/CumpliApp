package com.idnp2025b.cumpliapp.data.local.database

import androidx.room.TypeConverter
import com.idnp2025b.cumpliapp.data.model.Categoria
import com.idnp2025b.cumpliapp.data.model.Prioridad

class Converters {
    @TypeConverter
    fun fromPrioridad(prioridad: Prioridad): Int {
        return prioridad.valor
    }
    @TypeConverter
    fun toPrioridad(valor: Int): Prioridad {
        return Prioridad.fromValor(valor)
    }

    @TypeConverter
    fun fromCategoria(categoria: Categoria): String {
        return categoria.name
    }

    @TypeConverter
    fun toCategoria(name: String): Categoria {
        return Categoria.fromString(name)
    }
}