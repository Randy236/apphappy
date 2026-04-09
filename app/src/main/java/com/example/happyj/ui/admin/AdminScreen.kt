package com.example.happyj.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happyj.data.ReportesResponse
import com.example.happyj.data.ResumenGeneral
import com.example.happyj.ui.theme.HappyGreen
import com.example.happyj.viewmodel.AdminViewModel
import java.time.format.DateTimeFormatter

private val df = DateTimeFormatter.ISO_LOCAL_DATE

private val FondoPantalla = Color(0xFFFAFAFA)
private val TextoTitulo = Color(0xFF1A1A2E)
private val AzulCancha = Color(0xFF1565C0)
private val MoradoPinturas = Color(0xFF7B1FA2)
private val VerdeLaser = Color(0xFF2E7D32)
private val NaranjaPrincipal = Color(0xFFF57C00)
private val RojoGrande = Color(0xFFD32F2F)

private data class PieSlice(
    val label: String,
    val value: Double,
    val color: Color,
)

@Composable
fun AdminScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminViewModel,
) {
    val periodo by viewModel.periodo.collectAsStateWithLifecycle()
    val fechaRef by viewModel.fechaRef.collectAsStateWithLifecycle()
    val reporte by viewModel.reporte.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.cargar() }

    Column(
        modifier
            .fillMaxSize()
            .background(FondoPantalla)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.BarChart,
                contentDescription = null,
                tint = HappyGreen,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    "Reportes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextoTitulo,
                )
                Text(
                    "Ingresos totales y reservas de salones",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Periodo", style = MaterialTheme.typography.labelLarge, color = Color(0xFF616161))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                "diario" to "Hoy",
                "semanal" to "Semana",
                "mensual" to "Mes",
            ).forEach { (k, label) ->
                FilterChip(
                    selected = periodo == k,
                    onClick = { viewModel.setPeriodo(k) },
                    label = { Text(label) },
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { viewModel.setFechaRef(fechaRef.minusDays(1)) }) {
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, "Anterior")
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Outlined.CalendarMonth, null, tint = HappyGreen, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(6.dp))
                        Text(
                            "Referencia: ${fechaRef.format(df)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Text(
                        "Ajusta el día base del filtro (semana o mes según el periodo)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9E9E9E),
                    )
                }
                IconButton(onClick = { viewModel.setFechaRef(fechaRef.plusDays(1)) }) {
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, "Siguiente")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally), color = HappyGreen)
            Spacer(Modifier.height(8.dp))
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }

        reporte?.let { r ->
            Text(
                "Rango: ${r.rango.inicio} → ${r.rango.fin}",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF757575),
                modifier = Modifier.padding(bottom = 12.dp),
            )

            val res = resumenEfectivo(r)

            val slices = slicesEfectivos(r)
            PieCard(
                titulo = "Ingresos por área",
                slices = slices,
            )

            Spacer(Modifier.height(12.dp))
            MetricCardLarga("Ingresos totales (${labelPeriodo(periodo)})", "S/ %.2f".format(res.ingresosTotales))

            Spacer(Modifier.height(16.dp))
            Text("Extra", fontWeight = FontWeight.SemiBold, color = TextoTitulo)
            Spacer(Modifier.height(6.dp))
            MetricCardLarga("Total reservas cancha", "${res.totalReservasCancha}")
            MetricCardLarga("Total reservas salones", "${res.totalReservasSalones}")
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun resumenEfectivo(r: ReportesResponse): ResumenGeneral {
    r.resumen?.let { return it }
    return ResumenGeneral(
        ingresosTotales = r.cancha.ingresosPeriodo + r.salones.ingresosTotalPeriodo,
        totalReservasCancha = r.cancha.totalReservas,
        totalReservasSalones = r.salones.porSalon.sumOf { it.reservas },
        totalAdelantos = 0.0,
    )
}

@Composable
private fun MetricCardLarga(titulo: String, valor: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(titulo, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF616161), modifier = Modifier.weight(1f))
            Text(valor, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = HappyGreen)
        }
    }
}

private fun labelPeriodo(periodo: String): String = when (periodo) {
    "diario" -> "hoy"
    "semanal" -> "semana"
    "mensual" -> "mes"
    else -> "periodo"
}

private fun slicesEfectivos(r: ReportesResponse): List<PieSlice> {
    val mapaSalones = r.salones.porSalon.associateBy { it.salon }
    val pinturas = mapaSalones["Ex Salón de Pinturas"]?.ingresos ?: 0.0
    val laser = mapaSalones["Salón Laser"]?.ingresos ?: 0.0
    val principal = mapaSalones["Salón Principal"]?.ingresos ?: 0.0
    val grande = mapaSalones["Salón de Eventos Grande"]?.ingresos ?: 0.0

    return listOf(
        PieSlice("Cancha", r.cancha.ingresosPeriodo, AzulCancha),
        PieSlice("Ex Pinturas", pinturas, MoradoPinturas),
        PieSlice("Laser", laser, VerdeLaser),
        PieSlice("Principal", principal, NaranjaPrincipal),
        PieSlice("Grande", grande, RojoGrande),
    )
}

@Composable
private fun PieCard(
    titulo: String,
    slices: List<PieSlice>,
    strokeWidth: Dp = 26.dp,
) {
    val total = slices.sumOf { it.value }.takeIf { it > 0 } ?: 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, fontWeight = FontWeight.Bold, color = TextoTitulo)
            Spacer(Modifier.height(12.dp))

            if (total <= 0.0) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                ) {
                    drawArc(
                        color = Color(0xFFDDDDDD),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt),
                    )
                }
                Text(
                    "Sin datos para el periodo seleccionado",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF757575),
                )
                return@Column
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            ) {
                var start = -90f
                slices.forEach { s ->
                    val sweep = ((s.value / total) * 360.0).toFloat()
                    if (sweep <= 0f) return@forEach
                    drawArc(
                        color = s.color,
                        startAngle = start,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt),
                    )
                    start += sweep
                }
            }

            Spacer(Modifier.height(10.dp))
            Text("Leyenda", fontWeight = FontWeight.SemiBold, color = TextoTitulo)
            Spacer(Modifier.height(6.dp))
            slices.forEach { s ->
                val pct = ((s.value / total) * 100.0).toInt().coerceIn(0, 100)
                LegendRow(
                    color = s.color,
                    label = s.label,
                    text = "S/ %.0f (%d%%)".format(s.value, pct),
                )
            }
        }
    }
}

@Composable
private fun LegendRow(
    color: Color,
    label: String,
    text: String,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(12.dp),
                shape = RoundedCornerShape(3.dp),
                color = color,
                content = {},
            )
            Spacer(Modifier.width(10.dp))
            Text(label, fontWeight = FontWeight.Medium, color = TextoTitulo)
        }
        Text(text, color = Color(0xFF616161), fontWeight = FontWeight.SemiBold)
    }
}
