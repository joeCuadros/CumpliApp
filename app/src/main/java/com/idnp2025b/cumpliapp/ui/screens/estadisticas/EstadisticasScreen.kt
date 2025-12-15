package com.idnp2025b.cumpliapp.ui.screens.estadisticas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    viewModel: EstadisticasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas", fontWeight = FontWeight.Bold) },
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            when (val state = uiState) {
                is EstadisticasUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                // Aquí el smart-cast convierte 'state' a 'Success' automáticamente
                is EstadisticasUiState.Success -> {

                    // Pasamos 'state' que ahora sabemos que es Success
                    ResumenGeneralCard(state)

                    if (state.datosPorCategoria.isNotEmpty()) {
                        Text(
                            "Distribución por Categoría",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        GraficoCategorias(state.datosPorCategoria)
                    } else {
                        EmptyChartMessage()
                    }
                }
            }
        }
    }
}

@Composable
fun ResumenGeneralCard(
    // CORRECCIÓN: Especificamos que debe recibir Success, no el genérico
    state: EstadisticasUiState.Success
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Progreso Total",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                // Ahora sí reconoce porcentajeProgreso
                Text(
                    "${(state.porcentajeProgreso * 100).toInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                    strokeWidth = 8.dp,
                )
                CircularProgressIndicator(
                    progress = { state.porcentajeProgreso },
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.05f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Ahora sí reconoce totalPendientes y totalCompletadas
            DatoNumerico(titulo = "Pendientes", valor = state.totalPendientes.toString())
            DatoNumerico(titulo = "Completadas", valor = state.totalCompletadas.toString())
        }
    }
}

@Composable
fun DatoNumerico(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            valor,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            titulo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GraficoCategorias(datos: List<DatoCategoria>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DonutChart(
                datos = datos,
                modifier = Modifier.size(200.dp)
            )

            Spacer(Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                datos.forEach { dato ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(dato.color)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(dato.categoria.displayName)
                        }
                        Text(
                            "${dato.cantidad} (${(dato.porcentaje * 100).toInt()}%)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DonutChart(
    datos: List<DatoCategoria>,
    modifier: Modifier = Modifier,
    chartThickness: Dp = 30.dp
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(1f, animationSpec = tween(durationMillis = 1000))
    }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = (size.minDimension - chartThickness.toPx()) / 2
        var startAngle = -90f

        datos.forEach { dato ->
            val sweepAngle = 360f * dato.porcentaje * animationProgress.value

            drawArc(
                color = dato.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = chartThickness.toPx(), cap = StrokeCap.Butt),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun EmptyChartMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Agrega actividades para ver estadísticas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}