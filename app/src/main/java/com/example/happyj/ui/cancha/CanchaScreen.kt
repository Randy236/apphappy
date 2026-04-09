package com.example.happyj.ui.cancha

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happyj.data.ReservaCanchaCreate
import com.example.happyj.data.ReservaCanchaDto
import com.example.happyj.ui.theme.AdelantoAmarillo
import com.example.happyj.ui.theme.DisponibleGreen
import com.example.happyj.ui.theme.HappyBgBottom
import com.example.happyj.ui.theme.HappyBgMiddle
import com.example.happyj.ui.theme.HappyBgTop
import com.example.happyj.ui.theme.HappyGreen
import com.example.happyj.ui.theme.OcupadoRojo
import com.example.happyj.ui.util.franjasHoraEnHora
import com.example.happyj.ui.util.normalizarHoraApi
import com.example.happyj.ui.util.repartirMontoSoles
import com.example.happyj.ui.util.slotDesdeHora
import com.example.happyj.viewmodel.CanchaViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

private val mesAnioFmt = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))

private fun nombreDiaCorto(d: LocalDate): String = when (d.dayOfWeek) {
    DayOfWeek.MONDAY -> "Lun"
    DayOfWeek.TUESDAY -> "Mar"
    DayOfWeek.WEDNESDAY -> "Mié"
    DayOfWeek.THURSDAY -> "Jue"
    DayOfWeek.FRIDAY -> "Vie"
    DayOfWeek.SATURDAY -> "Sáb"
    DayOfWeek.SUNDAY -> "Dom"
}

private fun semanaDelMes(d: LocalDate): List<LocalDate> {
    val lunes = d.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    return (0L..6L).map { lunes.plusDays(it) }
}

private fun etiquetaDeporte(api: String) = when (api) {
    "Futbol" -> "Fútbol"
    "Voley" -> "Vóley"
    else -> api
}

private fun horaFinSlot(horaSlot: String): String {
    val h = horaSlot.take(2).toIntOrNull() ?: 8
    return String.format("%02d:00", (h + 1).coerceAtMost(23))
}

private fun horaToMin(horaSql: String): Int {
    val n = normalizarHoraApi(horaSql).split(":")
    val hh = n.getOrNull(0)?.toIntOrNull() ?: 0
    val mm = n.getOrNull(1)?.toIntOrNull() ?: 0
    return hh * 60 + mm
}

private fun esSlotPasado(fecha: LocalDate, horaSlotSql: String): Boolean {
    val hoy = LocalDate.now()
    if (fecha.isBefore(hoy)) return true
    if (fecha.isAfter(hoy)) return false
    val ahoraMin = LocalTime.now().hour * 60 + LocalTime.now().minute
    return horaToMin(horaSlotSql) <= ahoraMin
}

private fun canchaFinalizada(fecha: LocalDate, horaSlotSql: String): Boolean {
    val hoy = LocalDate.now()
    if (fecha.isBefore(hoy)) return true
    if (fecha.isAfter(hoy)) return false
    val ahoraMin = LocalTime.now().hour * 60 + LocalTime.now().minute
    val finMin = horaToMin(horaSlotSql) + 60
    return finMin <= ahoraMin
}

private fun fechaCortaEs(isoFecha: String): String {
    return try {
        val d = LocalDate.parse(isoFecha)
        DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "ES"))
            .format(d)
            .replace(".", "")
            .trim()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    } catch (_: Exception) {
        isoFecha
    }
}

private enum class ModoPagoCancha {
    /** Pago completo: sin saldo pendiente (adelanto = total en servidor) */
    Cancelado,
    /** Solo adelanto: ingresa cuánto dejó */
    Adelanto,
}

@Composable
fun CanchaScreen(
    modifier: Modifier = Modifier,
    viewModel: CanchaViewModel,
) {
    val fecha by viewModel.fecha.collectAsStateWithLifecycle()
    val reservas by viewModel.reservas.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.cargar() }

    var slotSel by remember { mutableStateOf<String?>(null) }
    var mostrarForm by remember { mutableStateOf(false) }
    var mostrarDetalle by remember { mutableStateOf<ReservaCanchaDto?>(null) }
    var avisoHorario by remember { mutableStateOf<String?>(null) }

    val slots = remember { (8..21).map { slotDesdeHora(it) } }
    val diasSemana = remember(fecha) { semanaDelMes(fecha) }
    val listState = rememberLazyListState()

    Column(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(HappyBgTop, HappyBgMiddle, HappyBgBottom))),
    ) {
        Text(
            text = "Cancha Principal",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val mesTitulo = fecha.format(mesAnioFmt).replaceFirstChar { it.uppercase() }
            Text(
                text = mesTitulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333),
            )
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = HappyGreen)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            diasSemana.forEach { dia ->
                val sel = dia == fecha
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (sel) HappyGreen else Color(0xFFEEEEEE))
                        .clickable { viewModel.elegirFecha(dia) }
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = nombreDiaCorto(dia),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (sel) Color.White else Color(0xFF424242),
                    )
                    Text(
                        text = "${dia.dayOfMonth}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sel) Color.White else Color(0xFF1A1A2E),
                    )
                }
            }
        }

        TextButtonCompact(
            onClick = { viewModel.irHoy() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 4.dp),
        )

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                color = HappyGreen,
                strokeWidth = 2.dp,
            )
        }

        error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 20.dp),
                fontSize = 13.sp,
            )
        }
        avisoHorario?.let {
            Text(
                it,
                color = Color(0xFF8D6E63),
                modifier = Modifier.padding(horizontal = 20.dp),
                fontSize = 13.sp,
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(slots) { horaSlot ->
                val r = reservas.find { normalizarHoraApi(it.hora) == horaSlot }
                val finalizada = r != null && canchaFinalizada(fecha, r.hora)
                SlotCardCancha(
                    horaInicio = horaSlot.take(5),
                    horaFin = horaFinSlot(horaSlot),
                    reserva = r,
                    finalizada = finalizada,
                    onClick = {
                        if (r == null) {
                            if (esSlotPasado(fecha, horaSlot)) {
                                avisoHorario = "No se puede reservar en horas pasadas."
                            } else {
                                avisoHorario = null
                                slotSel = horaSlot
                                mostrarForm = true
                            }
                        } else {
                            mostrarDetalle = r
                        }
                    },
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (mostrarForm && slotSel != null) {
        FormReservaCanchaDialog(
            fecha = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE),
            hora = slotSel!!,
            onDismiss = { mostrarForm = false },
            onConfirm = { lista ->
                viewModel.crearReservasCancha(lista) {
                    mostrarForm = false
                }
            },
        )
    }

    mostrarDetalle?.let { d ->
        val saldoPend = d.adelanto < d.montoTotal - 1e-6
        AlertDialog(
            onDismissRequest = { mostrarDetalle = null },
            title = { Text("Reserva") },
            text = {
                Column {
                    Text("Cliente: ${d.nombreCliente}")
                    Text("Deporte: ${etiquetaDeporte(d.deporte)}")
                    Text("Hora: ${d.hora.take(5)}")
                    Text("Total: S/ ${d.montoTotal}")
                    Text("Adelanto: S/ ${d.adelanto}")
                    Text("Estado: ${d.estado}")
                    if (saldoPend) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Saldo pendiente: S/ ${"%.2f".format(d.montoTotal - d.adelanto)}",
                            color = Color(0xFF795548),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            },
            dismissButton = {
                if (saldoPend) {
                    TextButton(
                        onClick = {
                            viewModel.cobrarSaldoCancha(d.id) { mostrarDetalle = null }
                        },
                    ) {
                        Text("Marcar como pagado", color = HappyGreen, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDetalle = null }) { Text("Cerrar") }
            },
        )
    }
}

@Composable
private fun TextButtonCompact(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text("Ir a hoy", color = HappyGreen, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SlotCardCancha(
    horaInicio: String,
    horaFin: String,
    reserva: ReservaCanchaDto?,
    finalizada: Boolean = false,
    onClick: () -> Unit,
) {
    val disponible = reserva == null
    val conAdelanto = reserva?.estado == "con_adelanto"
    val saldoPendiente = reserva != null && reserva.adelanto < reserva.montoTotal
    val accent = when {
        finalizada -> Color(0xFF78909C)
        disponible -> DisponibleGreen
        conAdelanto -> AdelantoAmarillo
        else -> OcupadoRojo
    }
    val badgeBg = when {
        finalizada -> Color(0xFFECEFF1)
        disponible -> Color(0xFFE8F5E9)
        conAdelanto -> Color(0xFFFFF8E1)
        else -> Color(0xFFFFEBEE)
    }
    val badgeTexto = when {
        finalizada && saldoPendiente -> "Finalizado (saldo pendiente)"
        finalizada -> "Finalizado"
        disponible -> "Disponible"
        conAdelanto -> "Adelanto"
        else -> "Ocupado"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accent),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
            ) {
                Text(
                    text = "$horaInicio – $horaFin",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A2E),
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = badgeBg,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(accent, CircleShape),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = badgeTexto,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accent,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (disponible) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Toca para registrar reserva",
                            fontSize = 13.sp,
                            color = Color(0xFF9E9E9E),
                        )
                        Icon(
                            Icons.Outlined.AddCircleOutline,
                            contentDescription = null,
                            tint = HappyGreen,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = null,
                                tint = Color(0xFF757575),
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = reserva!!.nombreCliente,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1A1A2E),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = etiquetaDeporte(reserva.deporte),
                                    fontSize = 13.sp,
                                    color = Color(0xFF757575),
                                )
                            }
                        }
                        Icon(
                            Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFFBDBDBD),
                        )
                    }
                    if (finalizada) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (saldoPendiente) "Reserva finalizada, falta cobrar saldo." else "Reserva finalizada y cancelada.",
                            fontSize = 12.sp,
                            color = Color(0xFF607D8B),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormReservaCanchaDialog(
    fecha: String,
    hora: String,
    onDismiss: () -> Unit,
    onConfirm: (List<ReservaCanchaCreate>) -> Unit,
) {
    var nombre by remember { mutableStateOf("") }
    var deporte by remember { mutableStateOf("Futbol") }
    var monto by remember { mutableStateOf("30") }
    var modoPago by remember { mutableStateOf(ModoPagoCancha.Cancelado) }
    var adelantoIngresado by remember { mutableStateOf("") }
    var horaIniText by remember(hora) { mutableStateOf(hora.take(5)) }
    var horaFinText by remember(hora) { mutableStateOf(horaFinSlot(hora).take(5)) }
    val scroll = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF1A1A2E),
                        )
                    }
                    Text(
                        text = "Registrar Reserva",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1A1A2E),
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scroll)
                        .padding(horizontal = 20.dp),
                ) {
                    Text(
                        "Fecha",
                        fontSize = 13.sp,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(8.dp))
                    InfoCaja(
                        modifier = Modifier.fillMaxWidth(),
                        icon = { Icon(Icons.Outlined.CalendarMonth, null, tint = HappyGreen, modifier = Modifier.size(20.dp)) },
                        texto = fechaCortaEs(fecha),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Horario (puedes alargar varias horas seguidas)",
                        fontSize = 13.sp,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        "Ej. 09:00 a 12:00 = 3 bloques de cancha. El monto se reparte entre cada hora.",
                        fontSize = 11.sp,
                        color = Color(0xFF9E9E9E),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedTextField(
                            value = horaIniText,
                            onValueChange = { v ->
                                horaIniText = v.filter { c -> c.isDigit() || c == ':' }.take(5)
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Inicio (HH:mm)") },
                            singleLine = true,
                            placeholder = { Text("09:00") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HappyGreen),
                        )
                        OutlinedTextField(
                            value = horaFinText,
                            onValueChange = { v ->
                                horaFinText = v.filter { c -> c.isDigit() || c == ':' }.take(5)
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Fin (HH:mm)") },
                            singleLine = true,
                            placeholder = { Text("12:00") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HappyGreen),
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("Nombre del Cliente", fontSize = 13.sp, color = Color(0xFF757575), fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Ej. Carlos Mendoza") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HappyGreen,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                        ),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Deporte", fontSize = 13.sp, color = Color(0xFF757575), fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        DeporteOpcion(
                            texto = "Fútbol",
                            seleccionado = deporte == "Futbol",
                            modifier = Modifier.weight(1f),
                            onClick = { deporte = "Futbol" },
                        )
                        DeporteOpcion(
                            texto = "Vóley",
                            seleccionado = deporte == "Voley",
                            modifier = Modifier.weight(1f),
                            onClick = { deporte = "Voley" },
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Monto total (S/)", fontSize = 13.sp, color = Color(0xFF757575), fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = monto,
                        onValueChange = { monto = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        prefix = { Text("S/ ", color = Color(0xFF757575), fontWeight = FontWeight.SemiBold) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HappyGreen,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                        ),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Forma de pago", fontSize = 13.sp, color = Color(0xFF757575), fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Cancelado = pago completo. Adelanto = dejó una parte y falta saldo en local.",
                        fontSize = 11.sp,
                        color = Color(0xFF9E9E9E),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        ModoPagoOpcion(
                            texto = "Cancelado",
                            subtitulo = "Pago completo",
                            seleccionado = modoPago == ModoPagoCancha.Cancelado,
                            modifier = Modifier.weight(1f),
                            onClick = { modoPago = ModoPagoCancha.Cancelado },
                        )
                        ModoPagoOpcion(
                            texto = "Adelanto",
                            subtitulo = "Parcial",
                            seleccionado = modoPago == ModoPagoCancha.Adelanto,
                            modifier = Modifier.weight(1f),
                            onClick = { modoPago = ModoPagoCancha.Adelanto },
                        )
                    }
                    if (modoPago == ModoPagoCancha.Adelanto) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Adelanto (S/) — fondo amarillo",
                            fontSize = 13.sp,
                            color = Color(0xFF795548),
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = adelantoIngresado,
                            onValueChange = { adelantoIngresado = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            prefix = { Text("S/ ", color = Color(0xFF795548), fontWeight = FontWeight.SemiBold) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFC107),
                                unfocusedBorderColor = Color(0xFFFFE082),
                                focusedContainerColor = Color(0xFFFFFDE7),
                                unfocusedContainerColor = Color(0xFFFFFDE7),
                            ),
                        )
                    }
                    val totalNum = monto.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val adelNum = when (modoPago) {
                        ModoPagoCancha.Cancelado -> totalNum
                        ModoPagoCancha.Adelanto -> adelantoIngresado.replace(",", ".").toDoubleOrNull() ?: 0.0
                    }
                    val saldoPendiente = (totalNum - adelNum).coerceAtLeast(0.0)
                    if (modoPago == ModoPagoCancha.Adelanto && totalNum > 0 && saldoPendiente > 0) {
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "Saldo pendiente a pagar en local: S/ ${"%.2f".format(saldoPendiente)}",
                                modifier = Modifier.padding(14.dp),
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                Button(
                    onClick = {
                        val mt = monto.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (nombre.isBlank() || mt <= 0) return@Button
                        val adEnviarTotal = when (modoPago) {
                            ModoPagoCancha.Cancelado -> mt
                            ModoPagoCancha.Adelanto -> {
                                val a = adelantoIngresado.replace(",", ".").toDoubleOrNull() ?: 0.0
                                if (a <= 0 || a > mt) return@Button
                                a
                            }
                        }
                        val franjas = franjasHoraEnHora(horaIniText, horaFinText)
                        if (franjas.isEmpty()) return@Button
                        val fechaSel = runCatching { LocalDate.parse(fecha) }.getOrNull() ?: return@Button
                        if (franjas.any { esSlotPasado(fechaSel, it) }) return@Button
                        val n = franjas.size
                        val montos = repartirMontoSoles(mt, n)
                        val adelantos = repartirMontoSoles(adEnviarTotal, n)
                        val lista = franjas.mapIndexed { i, h ->
                            ReservaCanchaCreate(
                                nombreCliente = nombre.trim(),
                                deporte = deporte,
                                fecha = fecha,
                                hora = h,
                                montoTotal = montos[i],
                                adelanto = adelantos[i],
                            )
                        }
                        onConfirm(lista)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HappyGreen),
                ) {
                    Text("Guardar Reserva", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun InfoCaja(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    texto: String,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE8F5E9),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            icon()
            Text(texto, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A2E))
        }
    }
}

@Composable
private fun DeporteOpcion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (seleccionado) Color.White else Color(0xFFE8F5E9)
    Box(
        modifier = modifier
            .shadow(if (seleccionado) 4.dp else 0.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            texto,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
            color = Color(0xFF1A1A2E),
        )
    }
}

@Composable
private fun ModoPagoOpcion(
    texto: String,
    subtitulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (seleccionado) Color.White else Color(0xFFE8F5E9)
    Column(
        modifier = modifier
            .shadow(if (seleccionado) 4.dp else 0.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(texto, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A1A2E))
        Text(subtitulo, fontSize = 11.sp, color = Color(0xFF757575))
    }
}
