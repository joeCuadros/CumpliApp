package com.idnp2025b.cumpliapp.data.model

enum class Prioridad(val valor: Int) {
    URGENTE(1),
    ALTA(2),
    MEDIA(3),
    BAJA(4);

    companion object {
        fun fromValor(valor: Int): Prioridad {
            return entries.find { it.valor == valor } ?: BAJA
        }
    }
}