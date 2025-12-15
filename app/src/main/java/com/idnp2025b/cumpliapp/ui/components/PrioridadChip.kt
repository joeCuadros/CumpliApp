package com.idnp2025b.cumpliapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.idnp2025b.cumpliapp.data.model.Prioridad

@Composable
fun PrioridadChip(
    prioridad: Prioridad,
    modifier: Modifier = Modifier
) {
    // Definimos pares de colores: (Fondo, Texto)
    val (backgroundColor, contentColor) = when (prioridad) {
        // Rojo pastel fondo, Rojo oscuro texto
        Prioridad.URGENTE -> Color(0xFFFFEBEE) to Color(0xFFB71C1C)

        // Naranja pastel fondo, Naranja oscuro texto
        Prioridad.ALTA -> Color(0xFFFFF3E0) to Color(0xFFE65100)

        // Amarillo/Ambar pastel fondo, Marrón dorado texto (MUCHO MÁS LEGIBLE)
        Prioridad.MEDIA -> Color(0xFFFFF8E1) to Color(0xFFBF360C)

        // Verde pastel fondo, Verde oscuro texto
        Prioridad.BAJA -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp), // Bordes un poco más redondeados
        modifier = modifier
    ) {
        Text(
            text = prioridad.name.lowercase().replaceFirstChar { it.uppercase() }, // "Media" en vez de "MEDIA"
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}