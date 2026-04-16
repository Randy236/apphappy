package com.example.happyj.ui.salones

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happyj.data.ReservaSalonCreate
import com.example.happyj.ui.util.horaTextoASql
import com.example.happyj.ui.util.minutosDelDia
import com.example.happyj.ui.util.minutosAHoraSql
import com.example.happyj.data.ReservaSalonDto
import com.example.happyj.ui.components.BannerMensajeImportante
import com.example.happyj.ui.components.FullscreenFormDialogScaffold
import com.example.happyj.ui.components.HappyToggleOption
import com.example.happyj.ui.components.NavegacionSemanaBar
import com.example.happyj.ui.theme.AdelantoAmarillo
import com.example.happyj.ui.theme.DisponibleGreen
import com.example.happyj.ui.theme.HappyBgBottom
import com.example.happyj.ui.theme.HappyBgMiddle
import com.example.happyj.ui.theme.HappyBgTop
import com.example.happyj.ui.theme.HappyGreen
import com.example.happyj.ui.theme.OcupadoRojo
import com.example.happyj.ui.util.normalizarHoraApi
import com.example.happyj.ui.util.slotDesdeHora
import com.example.happyj.viewmodel.SalonesViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.roundToInt

private val mesAnioFmt = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es-ES"))

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

private fun aMinutos(h: String): Int {
    val p = normalizarHoraApi(h).split(":")
    val hh = p.getOrNull(0)?.toIntOrNull() ?: 0
    val mm = p.getOrNull(1)?.toIntOrNull() ?: 0
    return hh * 60 + mm
}

private fun solapa(slotInicioMin: Int, slotFinMin: Int, res: ReservaSalonDto): Boolean {
    val a = aMinutos(res.horaInicio)
    val b = aMinutos(res.horaFin)
    return a < slotFinMin && slotInicioMin < b
}

private fun horaFinSlot(horaSlot: String): String {
    val h = horaSlot.take(2).toIntOrNull() ?: 8
    return String.format("%02d:00", (h + 1).coerceAtMost(23))
}

private fun salonSlotPasado(fecha: LocalDate, horaInicioSql: String): Boolean {
    val hoy = LocalDate.now()
    if (fecha.isBefore(hoy)) return true
    if (fecha.isAfter(hoy)) return false
    val ahoraMin = LocalTime.now().hour * 60 + LocalTime.now().minute
    return aMinutos(horaInicioSql) <= ahoraMin
}

private fun salonFinalizado(fecha: LocalDate, reserva: ReservaSalonDto): Boolean {
    val hoy = LocalDate.now()
    if (fecha.isBefore(hoy)) return true
    if (fecha.isAfter(hoy)) return false
    val ahoraMin = LocalTime.now().hour * 60 + LocalTime.now().minute
    return aMinutos(reserva.horaFin) <= ahoraMin
}

private fun tituloEventoReserva(r: ReservaSalonDto): String {
    return when {
        r.tipoEvento == "Cumpleanos" && !r.nombreCumpleanero.isNullOrBlank() ->
            "Cumpleaños ${r.nombreCumpleanero}"
        r.tipoEvento == "Cumpleanos" -> "Cumpleaños"
        else -> r.nombreCliente.ifBlank { "Evento privado" }
    }
}

private fun porcentajeAdelanto(r: ReservaSalonDto): Int {
    if (r.precioTotal <= 0) return 0
    return ((r.adelanto / r.precioTotal) * 100).roundToInt().coerceIn(0, 100)
}

private data class SalonTarjetaInfo(
    val nombre: String,
    val icono: ImageVector,
    val fondoIcono: Color,
    val tintIcono: Color,
)

private fun infoSalon(nombre: String): SalonTarjetaInfo = when (nombre) {
    "Ex Salón de Pinturas" -> SalonTarjetaInfo(
        nombre, Icons.Outlined.Palette, Color(0xFFF3E5F5), Color(0xFF7B1FA2),
    )
    "Salón Principal" -> SalonTarjetaInfo(
        nombre, Icons.Outlined.EmojiEvents, Color(0xFFFFF9C4), Color(0xFFF9A825),
    )
    "Salón de Eventos Grande" -> SalonTarjetaInfo(
        nombre, Icons.Outlined.Celebration, Color(0xFFFCE4EC), Color(0xFFE91E63),
    )
    "Salón Laser" -> SalonTarjetaInfo(
        nombre, Icons.Outlined.Bolt, Color(0xFFE8F5E9), Color(0xFF2E7D32),
    )
    else -> SalonTarjetaInfo(
        nombre, Icons.Outlined.Celebration, Color(0xFFF5F5F5), HappyGreen,
    )
}

private enum class ModoPagoSalon {
    Cancelado,
    Adelanto,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalonesScreen(
    modifier: Modifier = Modifier,
    viewModel: SalonesViewModel,
    /** Administrador: solo ve calendario y ocupación; no registra reservas. */
    soloConsulta: Boolean = false,
) {
    val salon by viewModel.salonSeleccionado.collectAsStateWithLifecycle()
    val fecha by viewModel.fecha.collectAsStateWithLifecycle()
    val reservas by viewModel.reservas.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    if (salon == null) {
        ListaSalonesPantalla(viewModel.salones, modifier, soloConsulta) { viewModel.elegirSalon(it) }
        return
    }

    LaunchedEffect(salon, fecha) { viewModel.cargar() }

    var mostrarForm by remember { mutableStateOf(false) }
    var slotSel by remember { mutableStateOf<String?>(null) }
    var detalle by remember { mutableStateOf<ReservaSalonDto?>(null) }
    var reservaParaCancelarSalon by remember { mutableStateOf<ReservaSalonDto?>(null) }
    var avisoHorario by remember { mutableStateOf<String?>(null) }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    val slots = remember { (8..21).map { slotDesdeHora(it) } }
    val diasSemana = remember(fecha) { semanaDelMes(fecha) }

    fun primeraFranjaLibre(): String {
        return slots.firstOrNull { horaSlot ->
            val h = horaSlot.take(2).toIntOrNull() ?: 8
            val ini = h * 60
            val fin = ini + 60
            !salonSlotPasado(fecha, horaSlot) &&
                reservas.none { r -> r.salonActivo() && solapa(ini, fin, r) }
        } ?: "09:00:00"
    }

    Column(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(HappyBgTop, HappyBgMiddle, HappyBgBottom))),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { viewModel.salirSalon() }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = Color(0xFF1A1A2E))
            }
            Text(
                text = salon!!,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1A1A2E),
            )
            if (!soloConsulta) {
                IconButton(
                    onClick = {
                        slotSel = primeraFranjaLibre()
                        mostrarForm = true
                    },
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Nueva reserva", tint = HappyGreen)
                }
            }
        }

        if (soloConsulta) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE3F2FD),
            ) {
                Text(
                    "Vista solo lectura. Las reservas de salones las registran los trabajadores.",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp,
                    color = Color(0xFF1565C0),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                fecha.format(mesAnioFmt).replaceFirstChar { it.uppercase() },
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333),
            )
            IconButton(onClick = { mostrarDatePicker = true }) {
                Icon(
                    Icons.Outlined.CalendarMonth,
                    contentDescription = "Abrir calendario para elegir fecha",
                    tint = HappyGreen,
                )
            }
        }

        NavegacionSemanaBar(
            onSemanaAnterior = { viewModel.irSemanaAnterior() },
            onSemanaSiguiente = { viewModel.irSemanaSiguiente() },
            modifier = Modifier.padding(bottom = 2.dp),
            textoAyuda = "Las flechas cambian la semana. Abajo eliges el día; el calendario arriba te lleva a otra fecha.",
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
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
                        .padding(vertical = 10.dp, horizontal = 6.dp),
                ) {
                    Text(
                        nombreDiaCorto(dia),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (sel) Color.White else Color(0xFF424242),
                    )
                    Text(
                        "${dia.dayOfMonth}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sel) Color.White else Color(0xFF1A1A2E),
                    )
                }
            }
        }

        TextButton(onClick = { viewModel.irHoy() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Ir a hoy", color = HappyGreen, fontWeight = FontWeight.SemiBold)
        }

        if (loading && !mostrarForm) {
            CircularProgressIndicator(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                color = HappyGreen,
            )
        }
        error?.let {
            BannerMensajeImportante(
                titulo = null,
                mensaje = it,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
        avisoHorario?.let {
            Text(it, color = Color(0xFF8D6E63), modifier = Modifier.padding(horizontal = 20.dp))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(slots) { horaSlot ->
                val h = horaSlot.take(2).toIntOrNull() ?: 8
                val slotIni = h * 60
                val slotFin = slotIni + 60
                val res = reservas.filter { it.salonActivo() }.find { solapa(slotIni, slotFin, it) }
                SlotSalonCard(
                    horaInicio = horaSlot.take(5),
                    horaFin = horaFinSlot(horaSlot),
                    reserva = res,
                    finalizado = res?.let { salonFinalizado(fecha, it) } ?: false,
                    soloConsulta = soloConsulta,
                    onClick = {
                        if (res == null) {
                            if (!soloConsulta) {
                                if (salonSlotPasado(fecha, horaSlot)) {
                                    avisoHorario = "No se puede reservar en horas pasadas."
                                } else {
                                    avisoHorario = null
                                    slotSel = horaSlot
                                    mostrarForm = true
                                }
                            }
                        } else {
                            detalle = res
                        }
                    },
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }

        if (mostrarDatePicker) {
            val zone = ZoneId.systemDefault()
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = fecha.atStartOfDay(zone).toInstant().toEpochMilli(),
            )
            DatePickerDialog(
                onDismissRequest = { mostrarDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { ms ->
                                val d = Instant.ofEpochMilli(ms).atZone(zone).toLocalDate()
                                viewModel.elegirFecha(d)
                            }
                            mostrarDatePicker = false
                        },
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDatePicker = false }) {
                        Text("Cancelar")
                    }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }

    if (!soloConsulta && mostrarForm && salon != null && slotSel != null) {
        FormReservaSalonPantalla(
            salon = salon!!,
            fecha = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE),
            horaInicioSlot = slotSel!!,
            guardando = loading,
            onDismiss = { mostrarForm = false },
            onConfirm = { body ->
                viewModel.crearReserva(body) { mostrarForm = false }
            },
        )
    }

    detalle?.let { d ->
        val cancelada = !d.salonActivo()
        val saldoPend = !cancelada && d.adelanto < d.precioTotal - 1e-6
        AlertDialog(
            onDismissRequest = { detalle = null },
            title = { Text(if (cancelada) "Reserva cancelada" else "Reserva") },
            text = {
                Column {
                    Text("Cliente: ${d.nombreCliente}")
                    Text("Evento: ${d.tipoEvento}")
                    d.nombreCumpleanero?.let { Text("Cumpleañero: $it") }
                    Text("${d.horaInicio.take(5)} – ${d.horaFin.take(5)}")
                    Text("Niños: ${d.numeroNinos}")
                    Text("Total: S/ ${d.precioTotal}")
                    Text("Adelanto: S/ ${d.adelanto}")
                    if (cancelada) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Motivo (cliente): ${d.motivoCancelacion ?: "—"}",
                            color = Color(0xFF795548),
                            fontSize = 13.sp,
                        )
                    }
                    if (saldoPend) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Saldo pendiente: S/ ${"%.2f".format(d.precioTotal - d.adelanto)}",
                            color = Color(0xFF795548),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            },
            dismissButton = {
                if (!soloConsulta && saldoPend) {
                    TextButton(
                        onClick = {
                            viewModel.cobrarSaldoSalon(d.id) { detalle = null }
                        },
                    ) {
                        Text("Marcar como pagado", color = HappyGreen, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Row {
                    if (!soloConsulta && !cancelada) {
                        TextButton(
                            onClick = {
                                reservaParaCancelarSalon = d
                                detalle = null
                            },
                        ) {
                            Text("Cancelar reserva", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(onClick = { detalle = null }) { Text("Cerrar") }
                }
            },
        )
    }

    reservaParaCancelarSalon?.let { rs ->
        var motivo by remember(rs.id) { mutableStateOf("") }
        val motivoOk = motivo.trim().length >= 2
        AlertDialog(
            onDismissRequest = { reservaParaCancelarSalon = null },
            title = { Text("Cancelar reserva") },
            text = {
                Column {
                    Text("Cliente: ${rs.nombreCliente}", fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Escribe el motivo que dio el cliente (obligatorio, mín. 2 caracteres).",
                        fontSize = 13.sp,
                        color = Color(0xFF757575),
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Motivo") },
                        minLines = 2,
                        maxLines = 4,
                        singleLine = false,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { reservaParaCancelarSalon = null }) { Text("Volver") }
            },
            confirmButton = {
                TextButton(
                    enabled = motivoOk,
                    onClick = {
                        viewModel.cancelarReservaSalon(rs.id, motivo.trim()) {
                            reservaParaCancelarSalon = null
                        }
                    },
                ) {
                    Text("Confirmar cancelación", color = MaterialTheme.colorScheme.error)
                }
            },
        )
    }
}

@Composable
private fun ListaSalonesPantalla(
    salones: List<String>,
    modifier: Modifier = Modifier,
    soloConsulta: Boolean = false,
    onPick: (String) -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(HappyBgTop, HappyBgMiddle, HappyBgBottom))),
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                "Salones",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF1A1A2E),
            )
            if (soloConsulta) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Consulta de ocupación (sin registro de reservas)",
                    fontSize = 13.sp,
                    color = Color(0xFF757575),
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(salones) { nombre ->
                val info = infoSalon(nombre)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPick(nombre) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(info.fondoIcono),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(info.icono, null, tint = info.tintIcono, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(info.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A2E))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Ver horarios y reservas",
                                fontSize = 13.sp,
                                color = Color(0xFF9E9E9E),
                            )
                        }
                        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, tint = Color(0xFFBDBDBD))
                    }
                }
            }
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SlotSalonCard(
    horaInicio: String,
    horaFin: String,
    reserva: ReservaSalonDto?,
    finalizado: Boolean = false,
    soloConsulta: Boolean = false,
    onClick: () -> Unit,
) {
    val libre = reserva == null
    val conAdelanto = reserva != null && reserva.adelanto > 0 && reserva.adelanto < reserva.precioTotal
    val saldoPendiente = reserva != null && reserva.adelanto < reserva.precioTotal
    val accent = when {
        finalizado -> Color(0xFF78909C)
        libre -> DisponibleGreen
        conAdelanto -> AdelantoAmarillo
        else -> OcupadoRojo
    }
    val badgeBg = when {
        finalizado -> Color(0xFFECEFF1)
        libre -> Color(0xFFE8F5E9)
        conAdelanto -> Color(0xFFFFF8E1)
        else -> Color(0xFFFFEBEE)
    }
    val badgeLabel = when {
        finalizado && saldoPendiente -> "Finalizado (saldo pendiente)"
        finalizado -> "Finalizado"
        libre -> "Disponible"
        conAdelanto -> "Adelanto (${porcentajeAdelanto(reserva!!)}%)"
        else -> "Ocupado"
    }
    val subtitulo = when {
        libre && soloConsulta -> "Disponible"
        libre -> "Libre para reservar"
        conAdelanto -> tituloEventoReserva(reserva!!)
        else -> tituloEventoReserva(reserva!!)
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
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accent),
            )
            Column(Modifier.padding(horizontal = 14.dp, vertical = 14.dp)) {
                Text(
                    "$horaInicio - $horaFin",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A2E),
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(50), color = badgeBg) {
                        Row(
                            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                Modifier
                                    .size(6.dp)
                                    .background(accent, CircleShape),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                badgeLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accent,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        subtitulo,
                        fontSize = 13.sp,
                        color = if (libre) Color(0xFF9E9E9E) else Color(0xFF616161),
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (libre && !soloConsulta) {
                        Icon(Icons.Outlined.AddCircleOutline, null, tint = HappyGreen, modifier = Modifier.size(26.dp))
                    }
                }
                if (finalizado && !libre) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (saldoPendiente) "Evento finalizado, falta cobrar saldo." else "Evento finalizado y cancelado.",
                        fontSize = 12.sp,
                        color = Color(0xFF607D8B),
                    )
                }
            }
        }
    }
}

@Composable
private fun FormReservaSalonPantalla(
    salon: String,
    fecha: String,
    horaInicioSlot: String,
    guardando: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (ReservaSalonCreate) -> Unit,
) {
    val horasPorSlot = remember(horaInicioSlot) {
        val t = horaInicioSlot.trim()
        val iniNorm = when {
            t.length >= 8 -> t.take(8)
            t.length >= 5 -> "${t.take(5)}:00"
            else -> "09:00:00"
        }
        val startMin = minutosDelDia(iniNorm) ?: 9 * 60
        val endMin = (startMin + 60).coerceAtMost(23 * 60 + 59)
        Pair(
            minutosAHoraSql(startMin).take(5),
            minutosAHoraSql(endMin).take(5),
        )
    }
    var horaIniText by remember(horaInicioSlot) { mutableStateOf(horasPorSlot.first) }
    var horaFinText by remember(horaInicioSlot) { mutableStateOf(horasPorSlot.second) }

    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Cumpleanos") }
    var cumple by remember { mutableStateOf("") }
    var zona by remember { mutableStateOf(salon) }
    var ninos by remember { mutableIntStateOf(0) }
    var precio by remember { mutableStateOf("450") }
    var modoPago by remember { mutableStateOf(ModoPagoSalon.Cancelado) }
    var adelantoIngresado by remember { mutableStateOf("") }
    val scroll = rememberScrollState()

    FullscreenFormDialogScaffold(
        guardando = guardando,
        onDismissRequest = onDismiss,
        title = "Nueva Reserva",
        scrollState = scroll,
        content = {
                    Text("Detalles del Evento", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = HappyGreen)
                    Spacer(Modifier.height(12.dp))
                    Text("Nombre del cliente", fontSize = 13.sp, color = Color(0xFF757575))
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej. María Pérez") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HappyGreen),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Tipo de evento", fontSize = 13.sp, color = Color(0xFF757575))
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HappyToggleOption(
                            text = "Cumpleaños",
                            selected = tipo == "Cumpleanos",
                            modifier = Modifier.weight(1f),
                            onClick = { tipo = "Cumpleanos" },
                        )
                        HappyToggleOption(
                            text = "Otro",
                            selected = tipo == "Otro",
                            modifier = Modifier.weight(1f),
                            onClick = { tipo = "Otro" },
                        )
                    }
                    if (tipo == "Cumpleanos") {
                        Spacer(Modifier.height(12.dp))
                        Text("Nombre del cumpleañero", fontSize = 13.sp, color = Color(0xFF757575))
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = cumple,
                            onValueChange = { cumple = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HappyGreen),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Zona del local", fontSize = 13.sp, color = Color(0xFF757575))
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = zona,
                        onValueChange = { zona = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HappyGreen),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Nº de niños", fontSize = 13.sp, color = Color(0xFF757575))
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (ninos > 0) ninos-- },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFEEEEEE), CircleShape),
                        ) {
                            Text("−", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "$ninos",
                            modifier = Modifier.padding(horizontal = 24.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        IconButton(
                            onClick = { ninos++ },
                            modifier = Modifier
                                .size(44.dp)
                                .background(HappyGreen.copy(alpha = 0.15f), CircleShape),
                        ) {
                            Text("+", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = HappyGreen)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Horario (inicio y fin)", fontSize = 13.sp, color = Color(0xFF757575))
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = horaIniText,
                            onValueChange = { horaIniText = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Inicio") },
                            placeholder = { Text("09:00") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HappyGreen),
                        )
                        OutlinedTextField(
                            value = horaFinText,
                            onValueChange = { horaFinText = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Fin") },
                            placeholder = { Text("13:00") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HappyGreen),
                        )
                    }
                    Text(
                        "Formato 24 h (ej. 09:00 a 14:00). Ajusta la duración del cumpleaños o evento.",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(top = 6.dp),
                    )
                    Spacer(Modifier.height(20.dp))
                    Text("Pagos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = HappyGreen)
                    Spacer(Modifier.height(12.dp))
                    Text("Precio total (S/)", fontSize = 13.sp, color = Color(0xFF757575))
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        prefix = { Text("S/ ", fontWeight = FontWeight.SemiBold, color = Color(0xFF757575)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HappyGreen),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Forma de pago", fontSize = 13.sp, color = Color(0xFF757575))
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HappyToggleOption(
                            text = "Cancelado",
                            selected = modoPago == ModoPagoSalon.Cancelado,
                            modifier = Modifier.weight(1f),
                            onClick = { modoPago = ModoPagoSalon.Cancelado },
                        )
                        HappyToggleOption(
                            text = "Adelanto",
                            selected = modoPago == ModoPagoSalon.Adelanto,
                            modifier = Modifier.weight(1f),
                            onClick = { modoPago = ModoPagoSalon.Adelanto },
                        )
                    }
                    if (modoPago == ModoPagoSalon.Adelanto) {
                        Spacer(Modifier.height(12.dp))
                        Text("Monto adelanto (S/)", fontSize = 13.sp, color = Color(0xFF795548))
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = adelantoIngresado,
                            onValueChange = { adelantoIngresado = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            prefix = { Text("S/ ", fontWeight = FontWeight.SemiBold) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFC107),
                                unfocusedBorderColor = Color(0xFFFFE082),
                                focusedContainerColor = Color(0xFFFFFDE7),
                                unfocusedContainerColor = Color(0xFFFFFDE7),
                            ),
                        )
                    }
                    val totalNum = precio.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val adelNum = when (modoPago) {
                        ModoPagoSalon.Cancelado -> totalNum
                        ModoPagoSalon.Adelanto -> adelantoIngresado.replace(",", ".").toDoubleOrNull() ?: 0.0
                    }
                    val saldo = (totalNum - adelNum).coerceAtLeast(0.0)
                    if (modoPago == ModoPagoSalon.Adelanto && totalNum > 0 && saldo > 0) {
                        Spacer(Modifier.height(12.dp))
                        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFE8F5E9), modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Saldo pendiente a pagar en local: S/ ${"%.2f".format(saldo)}",
                                Modifier.padding(14.dp),
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
        },
        bottomBar = {
                Button(
                    onClick = {
                        if (guardando) return@Button
                        val pr = precio.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (nombre.isBlank() || zona.isBlank() || pr <= 0) return@Button
                        if (tipo == "Cumpleanos" && cumple.isBlank()) return@Button
                        val ini = horaTextoASql(horaIniText) ?: return@Button
                        val fin = horaTextoASql(horaFinText) ?: return@Button
                        val mIni = minutosDelDia(ini) ?: return@Button
                        val mFin = minutosDelDia(fin) ?: return@Button
                        if (mFin <= mIni) return@Button
                        val fechaSel = runCatching { LocalDate.parse(fecha) }.getOrNull() ?: return@Button
                        if (salonSlotPasado(fechaSel, ini)) return@Button
                        val adEnviar = when (modoPago) {
                            ModoPagoSalon.Cancelado -> pr
                            ModoPagoSalon.Adelanto -> {
                                val a = adelantoIngresado.replace(",", ".").toDoubleOrNull() ?: 0.0
                                if (a <= 0 || a > pr) return@Button
                                a
                            }
                        }
                        onConfirm(
                            ReservaSalonCreate(
                                nombreCliente = nombre.trim(),
                                tipoEvento = tipo,
                                nombreCumpleanero = if (tipo == "Cumpleanos") cumple.trim() else null,
                                zona = zona.trim(),
                                numeroNinos = ninos,
                                horaInicio = ini,
                                horaFin = fin,
                                precioTotal = pr,
                                adelanto = adEnviar,
                                salon = salon,
                                fecha = fecha,
                            ),
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    enabled = !guardando,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HappyGreen),
                ) {
                    Text("Guardar Reserva", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White)
                }
        },
    )
}
