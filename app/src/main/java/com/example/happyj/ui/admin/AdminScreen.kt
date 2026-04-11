package com.example.happyj.ui.admin

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happyj.data.CancelacionCanchaReporte
import com.example.happyj.data.CancelacionSalonReporte
import com.example.happyj.data.CancelacionesReporteResponse
import com.example.happyj.data.ReportesResponse
import com.example.happyj.export.VentasReportePdfWriter
import com.example.happyj.data.ResumenGeneral
import com.example.happyj.ui.theme.HappyBgBottom
import com.example.happyj.ui.theme.HappyBgMiddle
import com.example.happyj.ui.theme.HappyBgTop
import com.example.happyj.ui.components.BannerMensajeImportante
import com.example.happyj.ui.theme.HappyGreen
import com.example.happyj.viewmodel.AdminViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val df = DateTimeFormatter.ISO_LOCAL_DATE

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
    val mostrarCancelaciones by viewModel.mostrarCancelaciones.collectAsStateWithLifecycle()
    val cancelacionesLoading by viewModel.cancelacionesLoading.collectAsStateWithLifecycle()
    val cancelaciones by viewModel.cancelaciones.collectAsStateWithLifecycle()
    val cancelacionesError by viewModel.cancelacionesError.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.cargar() }

    val ctx = LocalContext.current
    var reporteParaPdf by remember { mutableStateOf<ReportesResponse?>(null) }
    val crearPdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf"),
    ) { uri ->
        val r = reporteParaPdf
        reporteParaPdf = null
        if (uri == null || r == null) return@rememberLauncherForActivityResult
        try {
            ctx.contentResolver.openOutputStream(uri)?.use { out ->
                VentasReportePdfWriter.write(r, r.periodo, out)
            } ?: run {
                Toast.makeText(ctx, "No se pudo abrir el archivo para escribir.", Toast.LENGTH_LONG).show()
                return@rememberLauncherForActivityResult
            }
            Toast.makeText(ctx, "PDF guardado correctamente.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(
                ctx,
                "No se pudo guardar el PDF: ${e.message ?: "error"}",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(HappyBgTop, HappyBgMiddle, HappyBgBottom))),
    ) {
    Column(
        Modifier
            .fillMaxSize()
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

        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { viewModel.abrirPanelCancelaciones() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
        ) {
            Icon(Icons.Outlined.EventBusy, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "Ver cancelaciones del período",
                fontWeight = FontWeight.SemiBold,
            )
        }
        Text(
            "Reservas de cancha o salón que el personal canceló, con el motivo que dejó el cliente.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF757575),
            modifier = Modifier.padding(top = 6.dp),
        )

        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally), color = HappyGreen)
            Spacer(Modifier.height(8.dp))
        }

        error?.let {
            BannerMensajeImportante(
                titulo = "No se cargaron los datos",
                mensaje = it,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

        reporte?.let { r ->
            Text(
                "Rango: ${r.rango.inicio} → ${r.rango.fin}",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF757575),
                modifier = Modifier.padding(bottom = 12.dp),
            )

            OutlinedButton(
                onClick = {
                    reporteParaPdf = r
                    crearPdfLauncher.launch(nombreArchivoPdfVentas(periodo, fechaRef))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = HappyGreen),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Guardar reporte en PDF",
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "Ventas del ${labelPeriodo(periodo)} (totales y detalle por área)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF757575),
                    )
                }
                }
            }

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

    if (mostrarCancelaciones) {
        DialogoCancelacionesAdmin(
            cancelacionesLoading = cancelacionesLoading,
            cancelaciones = cancelaciones,
            error = cancelacionesError,
            onCerrar = { viewModel.cerrarPanelCancelaciones() },
            onVolverACargar = { viewModel.cargarCancelaciones() },
        )
    }
    }
}

@Composable
private fun DialogoCancelacionesAdmin(
    cancelacionesLoading: Boolean,
    cancelaciones: CancelacionesReporteResponse?,
    error: String?,
    onCerrar: () -> Unit,
    onVolverACargar: () -> Unit,
) {
    Dialog(onDismissRequest = onCerrar) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp),
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Cancelaciones",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextoTitulo,
                        )
                        cancelaciones?.rango?.let { rg ->
                            Text(
                                "${rg.inicio} → ${rg.fin}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF757575),
                            )
                        }
                    }
                    TextButton(onClick = onCerrar) { Text("Cerrar") }
                }
                Spacer(Modifier.height(8.dp))
                if (cancelacionesLoading) {
                    CircularProgressIndicator(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(24.dp),
                        color = HappyGreen,
                    )
                } else {
                    error?.let {
                        BannerMensajeImportante(
                            titulo = "No se pudo cargar",
                            mensaje = it,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                        TextButton(
                            onClick = onVolverACargar,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        ) {
                            Text("Reintentar", color = HappyGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    cancelaciones?.let { data ->
                        val vacio = data.cancha.isEmpty() && data.salones.isEmpty()
                        if (vacio && error == null) {
                            Text(
                                "No hay cancelaciones registradas en este rango de fechas.",
                                color = Color(0xFF757575),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 16.dp),
                            )
                        } else if (error == null) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.heightIn(max = 400.dp),
                            ) {
                                if (data.cancha.isNotEmpty()) {
                                    item {
                                        Text(
                                            "Cancha",
                                            fontWeight = FontWeight.Bold,
                                            color = AzulCancha,
                                            fontSize = 15.sp,
                                        )
                                    }
                                    items(data.cancha) { row ->
                                        TarjetaCancelacionCancha(row)
                                    }
                                }
                                if (data.salones.isNotEmpty()) {
                                    item {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Salones",
                                            fontWeight = FontWeight.Bold,
                                            color = NaranjaPrincipal,
                                            fontSize = 15.sp,
                                        )
                                    }
                                    items(data.salones) { row ->
                                        TarjetaCancelacionSalon(row)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaCancelacionCancha(row: CancelacionCanchaReporte) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(row.nombreCliente, fontWeight = FontWeight.SemiBold, color = TextoTitulo)
            Text(
                "${row.fecha} · ${row.hora}${row.deporte?.let { " · $it" } ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF546E7A),
            )
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFBBDEFB))
            Text(
                "Motivo: ${row.motivoCancelacion?.trim()?.takeIf { it.isNotEmpty() } ?: "—"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF37474F),
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun TarjetaCancelacionSalon(row: CancelacionSalonReporte) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(row.nombreCliente, fontWeight = FontWeight.SemiBold, color = TextoTitulo)
            Text(
                "${row.fecha} · ${row.horaInicio}–${row.horaFin}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF795548),
            )
            row.tipoEvento?.takeIf { it.isNotBlank() }?.let { tipo ->
                Text(
                    tipo.replace("Cumpleanos", "Cumpleaños"),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8D6E63),
                )
            }
            Text(
                row.salon,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFE65100),
                fontWeight = FontWeight.Medium,
            )
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFFFE0B2))
            Text(
                "Motivo: ${row.motivoCancelacion?.trim()?.takeIf { it.isNotEmpty() } ?: "—"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF4E342E),
                lineHeight = 18.sp,
            )
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

private fun nombreArchivoPdfVentas(periodo: String, fechaRef: LocalDate): String {
    val p = when (periodo) {
        "diario" -> "dia"
        "semanal" -> "semana"
        "mensual" -> "mes"
        else -> "periodo"
    }
    return "HappyJump_ventas_${p}_${fechaRef}.pdf"
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
