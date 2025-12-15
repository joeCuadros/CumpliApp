package com.idnp2025b.cumpliapp.data.model

enum class Categoria(val displayName: String) {
    UNIVERSIDAD("Universidad"),
    CASA("Casa"),
    TRABAJO("Trabajo"),
    OTROS("Otros");

    companion object {
        fun fromString(value: String): Categoria {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: OTROS
        }
    }
}