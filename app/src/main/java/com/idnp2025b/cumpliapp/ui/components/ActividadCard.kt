package com.idnp2025b.cumpliapp.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.idnp2025b.cumpliapp.data.model.Actividad
import com.idnp2025b.cumpliapp.ui.utils.esFechaProxima
import com.idnp2025b.cumpliapp.ui.utils.formatearFecha
import com.idnp2025b.cumpliapp.ui.utils.formatearTiempo

@Composable
fun ActividadCard(
    actividad: Actividad,
    onActividadClick: () -> Unit,
    onCompletarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onEnfoqueClick: () -> Unit = {}, // NUEVO
    modifier: Modifier = Modifier
) {
    // Animación para el borde cuando está en progreso
    val infiniteTransition = rememberInfiniteTransition(label = "border")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "alpha"
    )

    val borderModifier = if (actividad.enProgreso) {
        Modifier.border(
            width = 2.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                    MaterialTheme.colorScheme.tertiary.copy(alpha = alpha)
                )
            ),
            shape = CardDefaults.shape
        )
    } else Modifier

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(borderModifier)
            .clickable(onClick = onActividadClick, enabled = !actividad.completada),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (actividad.enProgreso) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (actividad.enProgreso) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = actividad.completada,
                onCheckedChange = { onCompletarClick() }
            )

            Spacer(Modifier.width(12.dp))

            // Contenido principal
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (actividad.completada) {
                        TextDecoration.LineThrough
                    } else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (actividad.descripcion.isNotBlank()) {
                    Text(
                        text = actividad.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrioridadChip(prioridad = actividad.prioridad)
                    CategoriaChip(categoria = actividad.categoria)

                    if (actividad.tieneRecordatorio) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Con recordatorio",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // NUEVO: Mostrar tiempo acumulado si existe
                if (actividad.tiempoAcumulado > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⏱️",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = formatearTiempo(actividad.tiempoAcumulado),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (esFechaProxima(actividad.fechaEntrega)) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Text(
                        text = formatearFecha(actividad.fechaEntrega),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (esFechaProxima(actividad.fechaEntrega)) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // NUEVO: Botón de Modo Enfoque
            if (!actividad.completada) {
                IconButton(
                    onClick = onEnfoqueClick,
                    modifier = Modifier.alpha(if (actividad.enProgreso) 1f else 0.7f)
                ) {
                    Icon(
                        imageVector = if (actividad.enProgreso) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (actividad.enProgreso) "Pausar" else "Iniciar enfoque",
                        tint = if (actividad.enProgreso) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            IconButton(onClick = onEliminarClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}