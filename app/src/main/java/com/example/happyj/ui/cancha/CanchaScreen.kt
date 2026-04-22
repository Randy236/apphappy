package com.example.happyj.ui.cancha

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happyj.data.ReservaCanchaCreate
import com.example.happyj.data.ReservaCanchaDto
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
import com.example.happyj.ui.theme.HappyTextSecondary
import com.example.happyj.ui.theme.OcupadoRojo
import com.example.happyj.ui.util.EstadoDisponibilidadDiaCancha
import com.example.happyj.ui.util.GrupoTurnoCancha
import com.example.happyj.ui.util.agruparReservasCanchaContiguas
import com.example.happyj.ui.util.duracionCanchaMinutos
import com.example.happyj.ui.util.formatearDuracionTotalCancha
import com.example.happyj.ui.util.duracionReservaCanchaMinutos
import com.example.happyj.ui.util.franjasCanchaEntreDetalle
import com.example.happyj.ui.util.franjasCanchaEntre
import com.example.happyj.ui.util.inicioTurnoCanchaEsPasado
import com.example.happyj.ui.util.mensajeRangoCanchaInvalido
import com.example.happyj.ui.util.sugerenciaInicioFinCancha
import com.example.happyj.ui.util.normalizarHoraApi
import com.example.happyj.ui.util.repartirMontoSoles
import com.example.happyj.ui.util.localDateDesdeCampoApi
import com.example.happyj.ui.util.primerSlotSqlDelGrupo
import com.example.happyj.ui.util.reservaQueSolapaSlot
import com.example.happyj.ui.util.slotsCanchaParaFecha
import com.example.happyj.viewmodel.CanchaViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

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

private fun etiquetaDeporte(api: String) = when (api) {
    "Futbol" -> "Fútbol"
    "Voley" -> "Vóley"
    else -> api
}

/** Hora de fin del bloque (1 h si empieza en :00, 30 min si empieza en :30). */
private fun horaFinCancha(horaSlot: String): String {
    val start = horaToMin(horaSlot)
    val end = start + duracionCanchaMinutos(horaSlot)
    val hh = end / 60
    val mm = end % 60
    return String.format("%02d:%02d", hh, mm)
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
    return horaToMin(horaSlotSql) < ahoraMin
}

private fun canchaFinalizada(fecha: LocalDate, reserva: ReservaCanchaDto): Boolean {
    val hoy = LocalDate.now()
    if (fecha.isBefore(hoy)) return true
    if (fecha.isAfter(hoy)) return false
    val ahoraMin = LocalTime.now().hour * 60 + LocalTime.now().minute
    val finMin = horaToMin(reserva.hora) + duracionReservaCanchaMinutos(reserva)
    return finMin <= ahoraMin
}

private fun fechaCortaEs(isoFecha: String): String {
    return try {
        val d = localDateDesdeCampoApi(isoFecha) ?: return isoFecha
        DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("es-ES"))
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

private data class FilaGrillaCancha(
    val horaSlot: String,
    val libre: Boolean,
    val grupo: GrupoTurnoCancha? = null,
)

private fun construirFilasListaCancha(
    fecha: LocalDate,
    reservas: List<ReservaCanchaDto>,
): List<FilaGrillaCancha> {
    val slotsL = slotsCanchaParaFecha(fecha)
    val grupos = agruparReservasCanchaContiguas(reservas)
    val activas = reservas.filter { it.estado != "cancelado" }
    return slotsL.mapNotNull { horaSlot ->
        val r = reservaQueSolapaSlot(reservas, horaSlot)
        if (r == null) {
            FilaGrillaCancha(horaSlot, libre = true, grupo = null)
        } else {
            val g = grupos.find { gr -> gr.segmentos.any { it.id == r.id } }
                ?: GrupoTurnoCancha(listOf(r))
            val primero = primerSlotSqlDelGrupo(slotsL, activas, g) ?: horaSlot
            if (horaSlot != primero) return@mapNotNull null
            FilaGrillaCancha(primero, libre = false, grupo = g)
        }
    }
}

private fun construirCeldasCalendarioMes(mesVisible: YearMonth): List<LocalDate?> {
    val offsetLunes = (mesVisible.atDay(1).dayOfWeek.value + 6) % 7
    return buildList {
        repeat(offsetLunes) { add(null) }
        for (d in 1..mesVisible.lengthOfMonth()) add(mesVisible.atDay(d))
        while (size % 7 != 0) add(null)
    }
}

private fun coloresLeyendaCeldaCalendarioCancha(
    dia: LocalDate?,
    estadoPorDia: Map<LocalDate, EstadoDisponibilidadDiaCancha>,
    cargando: Boolean,
): Pair<Color, Color> {
    if (dia == null) return Color.Transparent to Color.Transparent
    val st = estadoPorDia[dia]
    if (cargando && st == null) return Color(0xFFEEEEEE) to Color(0xFF9E9E9E)
    if (st == null) return Color(0xFFEEEEEE) to Color(0xFF757575)
    return when (st) {
        EstadoDisponibilidadDiaCancha.Libre -> Color(0xFFC8E6C9) to Color(0xFF1B5E20)
        EstadoDisponibilidadDiaCancha.Parcial -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
        EstadoDisponibilidadDiaCancha.Lleno -> Color(0xFFFFCDD2) to Color(0xFFB71C1C)
        EstadoDisponibilidadDiaCancha.Pasado -> Color(0xFFECEFF1) to Color(0xFF78909C)
    }
}

private val nombresDiaSemanaCalendarioCancha =
    listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

@Composable
private fun CalendarioCanchaBarraMes(
    tituloMes: String,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onMesAnterior) {
            Icon(Icons.Outlined.ChevronLeft, contentDescription = "Mes anterior")
        }
        Text(
            tituloMes,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF1A1A2E),
        )
        IconButton(onClick = onMesSiguiente) {
            Icon(Icons.Outlined.ChevronRight, contentDescription = "Mes siguiente")
        }
    }
}

@Composable
private fun CalendarioCanchaLeyendaEstados() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LeyendaCalendarioCancha(Color(0xFFC8E6C9), "Libre")
        LeyendaCalendarioCancha(Color(0xFFFFF9C4), "Parcial")
        LeyendaCalendarioCancha(Color(0xFFFFCDD2), "Lleno")
        LeyendaCalendarioCancha(Color(0xFFECEFF1), "Pasado")
    }
}

@Composable
private fun CalendarioCanchaFilaDiasSemana() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        nombresDiaSemanaCalendarioCancha.forEach { n ->
            Text(
                n,
                fontSize = 11.sp,
                color = HappyTextSecondary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun RowScope.CeldaDiaCalendarioCancha(
    dia: LocalDate?,
    fechaSeleccionada: LocalDate,
    estadoPorDia: Map<LocalDate, EstadoDisponibilidadDiaCancha>,
    cargando: Boolean,
    onElegirFecha: (LocalDate) -> Unit,
) {
    val (bg, fg) = coloresLeyendaCeldaCalendarioCancha(dia, estadoPorDia, cargando)
    val sel = dia != null && dia == fechaSeleccionada
    Box(
        modifier = Modifier
            .weight(1f)
            .padding(2.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(
                width = if (sel) 2.dp else 0.dp,
                color = if (sel) HappyGreen else Color.Transparent,
                shape = RoundedCornerShape(10.dp),
            )
            .then(
                if (dia != null) Modifier.clickable { onElegirFecha(dia) }
                else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (dia != null) {
            Text(
                "${dia.dayOfMonth}",
                fontWeight = if (sel) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 15.sp,
                color = fg,
            )
        }
    }
}

@Composable
private fun CalendarioCanchaGrillaMes(
    celdas: List<LocalDate?>,
    fechaSeleccionada: LocalDate,
    estadoPorDia: Map<LocalDate, EstadoDisponibilidadDiaCancha>,
    cargando: Boolean,
    onElegirFecha: (LocalDate) -> Unit,
) {
    Column {
        celdas.chunked(7).forEach { semana ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                for (dia in semana) {
                    CeldaDiaCalendarioCancha(
                        dia,
                        fechaSeleccionada,
                        estadoPorDia,
                        cargando,
                        onElegirFecha,
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarioCanchaGrillaConCarga(
    celdas: List<LocalDate?>,
    fechaSeleccionada: LocalDate,
    estadoPorDia: Map<LocalDate, EstadoDisponibilidadDiaCancha>,
    cargando: Boolean,
    onElegirFecha: (LocalDate) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        CalendarioCanchaGrillaMes(
            celdas,
            fechaSeleccionada,
            estadoPorDia,
            cargando,
            onElegirFecha,
        )
        if (cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66FFFFFF)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = HappyGreen,
                    strokeWidth = 3.dp,
                )
            }
        }
    }
}

@Composable
private fun CalendarioCanchaDisponibilidadDialog(
    mesVisible: YearMonth,
    fechaSeleccionada: LocalDate,
    estadoPorDia: Map<LocalDate, EstadoDisponibilidadDiaCancha>,
    cargando: Boolean,
    errorCarga: String?,
    onMesChange: (YearMonth) -> Unit,
    onElegirFecha: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val tituloMes = mesVisible.format(mesAnioFmt).replaceFirstChar { it.uppercase() }
    val celdas = construirCeldasCalendarioMes(mesVisible)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 8.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                CalendarioCanchaBarraMes(
                    tituloMes,
                    onMesAnterior = { onMesChange(mesVisible.minusMonths(1)) },
                    onMesSiguiente = { onMesChange(mesVisible.plusMonths(1)) },
                )
                Text(
                    "Toca un día para verlo en la grilla de horarios.",
                    fontSize = 12.sp,
                    color = HappyTextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                CalendarioCanchaLeyendaEstados()
                errorCarga?.let { err ->
                    Text(
                        err,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                CalendarioCanchaFilaDiasSemana()
                Spacer(Modifier.height(6.dp))
                CalendarioCanchaGrillaConCarga(
                    celdas,
                    fechaSeleccionada,
                    estadoPorDia,
                    cargando,
                    onElegirFecha,
                )
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text("Cerrar", color = HappyGreen, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun LeyendaCalendarioCancha(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color),
        )
        Text(texto, fontSize = 10.sp, color = Color(0xFF616161))
    }
}

@Composable
private fun CanchaTituloPrincipal() {
    Text(
        text = "Cancha Principal",
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1A2E),
    )
}

@Composable
private fun CanchaBarraAyudaOtraHora(onOtraHora: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE8F5E9),
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = HappyGreen,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "Grilla :00/:30. «Otra hora»: cualquier minuto desde ahora (ej. 12:15–12:45).",
                modifier = Modifier.weight(1f),
                fontSize = 12.sp,
                lineHeight = 15.sp,
                color = Color(0xFF33691E),
                maxLines = 3,
            )
            Button(
                onClick = onOtraHora,
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HappyGreen),
            ) {
                Text("Otra hora", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun CanchaFilaMesYCalendario(
    fecha: LocalDate,
    onAbrirCalendario: () -> Unit,
) {
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
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF333333),
        )
        IconButton(onClick = onAbrirCalendario) {
            Icon(
                Icons.Outlined.CalendarMonth,
                contentDescription = "Abrir calendario para elegir fecha",
                tint = HappyGreen,
            )
        }
    }
}

@Composable
private fun CanchaSelectorChipsDiasSemana(
    diasSemana: List<LocalDate>,
    fechaSeleccionada: LocalDate,
    onElegirDia: (LocalDate) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        diasSemana.forEach { dia ->
            val sel = dia == fechaSeleccionada
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (sel) HappyGreen else Color(0xFFEEEEEE))
                    .clickable { onElegirDia(dia) }
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
}

@Composable
private fun CanchaFilaBotonIrHoy(onIrHoy: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onIrHoy) {
            Text("Ir a hoy", color = HappyGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ColumnScope.CanchaBloqueCargaErrores(
    loading: Boolean,
    mostrarForm: Boolean,
    error: String?,
    avisoHorario: String?,
) {
    if (loading && !mostrarForm) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            color = HappyGreen,
            strokeWidth = 2.dp,
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
        Text(
            it,
            color = Color(0xFF8D6E63),
            modifier = Modifier.padding(horizontal = 20.dp),
            fontSize = 13.sp,
        )
    }
}

private fun claveEstableFilaGrillaCancha(fila: FilaGrillaCancha): String =
    if (fila.libre) "L${fila.horaSlot}"
    else "G${fila.grupo!!.segmentos.first().id}"

@Composable
private fun CanchaSlotItemFila(
    f: FilaGrillaCancha,
    fecha: LocalDate,
    onCeldaLibreClick: (horaSlot: String, pasado: Boolean) -> Unit,
    onCeldaOcupadaClick: (GrupoTurnoCancha) -> Unit,
) {
    if (f.libre) {
        CanchaSlotItemFilaLibre(f, fecha, onCeldaLibreClick)
    } else {
        CanchaSlotItemFilaOcupada(f, fecha, onCeldaOcupadaClick)
    }
}

@Composable
private fun CanchaSlotItemFilaLibre(
    f: FilaGrillaCancha,
    fecha: LocalDate,
    onCeldaLibreClick: (horaSlot: String, pasado: Boolean) -> Unit,
) {
    val horaSlot = f.horaSlot
    val pasado = esSlotPasado(fecha, horaSlot)
    val etiquetaDuracion =
        if (duracionCanchaMinutos(horaSlot) == 30) "½ h" else "1 h"
    SlotCardCancha(
        SlotCardCanchaProps(
            horaInicio = horaSlot.take(5),
            horaFin = horaFinCancha(horaSlot),
            reserva = null,
            finalizada = false,
            etiquetaDuracion = etiquetaDuracion,
            slotPasado = pasado,
            unTurnoLargo = false,
            hayAdelantoEnGrupo = false,
            onClick = { onCeldaLibreClick(horaSlot, pasado) },
        ),
    )
}

@Composable
private fun CanchaSlotItemFilaOcupada(
    f: FilaGrillaCancha,
    fecha: LocalDate,
    onCeldaOcupadaClick: (GrupoTurnoCancha) -> Unit,
) {
    val g = f.grupo!!
    val rep = g.segmentos.first()
    val finalizada = g.segmentos.all { canchaFinalizada(fecha, it) }
    val etiquetaDuracion = etiquetaDuracionSlotOcupado(g, rep)
    val hayAdelanto = g.segmentos.any { it.estado == "con_adelanto" }
    SlotCardCancha(
        SlotCardCanchaProps(
            horaInicio = g.horaInicioTexto,
            horaFin = g.horaFinTexto,
            reserva = rep,
            finalizada = finalizada,
            etiquetaDuracion = etiquetaDuracion,
            slotPasado = false,
            unTurnoLargo = g.segmentos.size > 1,
            hayAdelantoEnGrupo = hayAdelanto,
            onClick = { onCeldaOcupadaClick(g) },
        ),
    )
}

private fun etiquetaDuracionSlotOcupado(g: GrupoTurnoCancha, rep: ReservaCanchaDto): String =
    if (g.segmentos.size > 1) {
        formatearDuracionTotalCancha(g.duracionTotalMinutos)
    } else {
        if (duracionCanchaMinutos(rep.hora) == 30) "½ h" else "1 h"
    }

private fun LazyListScope.CanchaItemsListaFilasHorarios(
    filasGrilla: List<FilaGrillaCancha>,
    fecha: LocalDate,
    onCeldaLibreClick: (horaSlot: String, pasado: Boolean) -> Unit,
    onCeldaOcupadaClick: (GrupoTurnoCancha) -> Unit,
) {
    items(
        filasGrilla,
        key = { claveEstableFilaGrillaCancha(it) },
    ) { fila ->
        CanchaSlotItemFila(fila, fecha, onCeldaLibreClick, onCeldaOcupadaClick)
    }
    item { Spacer(Modifier.height(24.dp)) }
}

@Composable
private fun ColumnScope.CanchaLazyListaFilasHorarios(
    filasGrilla: List<FilaGrillaCancha>,
    fecha: LocalDate,
    listState: LazyListState,
    onCeldaLibreClick: (horaSlot: String, pasado: Boolean) -> Unit,
    onCeldaOcupadaClick: (GrupoTurnoCancha) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        CanchaItemsListaFilasHorarios(
            filasGrilla,
            fecha,
            onCeldaLibreClick,
            onCeldaOcupadaClick,
        )
    }
}

private fun textoTituloDialogoDetalleGrupo(cancelada: Boolean, variasFranjas: Boolean): String =
    when {
        cancelada -> "Reserva cancelada"
        variasFranjas -> "Reserva (un solo turno)"
        else -> "Reserva"
    }

@Composable
private fun DialogoDetalleGrupoTitulo(cancelada: Boolean, variasFranjas: Boolean) {
    Text(textoTituloDialogoDetalleGrupo(cancelada, variasFranjas))
}

@Composable
private fun DialogoDetalleGrupoLineaFranjasMultiples(g: GrupoTurnoCancha, variasFranjas: Boolean) {
    if (!variasFranjas) return
    Text(
        "${g.segmentos.size} franjas contiguas · un solo turno de juego",
        fontSize = 12.sp,
        color = HappyTextSecondary,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun DialogoDetalleGrupoLineaMotivoCancelacion(cancelada: Boolean, rep: ReservaCanchaDto) {
    if (!cancelada) return
    Spacer(Modifier.height(8.dp))
    Text(
        "Motivo (cliente): ${rep.motivoCancelacion ?: "—"}",
        color = Color(0xFF795548),
        fontSize = 13.sp,
    )
}

@Composable
private fun DialogoDetalleGrupoLineaSaldoPendiente(saldoPend: Boolean, saldoTotal: Double) {
    if (!saldoPend) return
    Spacer(Modifier.height(8.dp))
    Text(
        "Saldo pendiente: S/ ${"%.2f".format(saldoTotal)}",
        color = Color(0xFF795548),
        fontWeight = FontWeight.SemiBold,
    )
}

private data class DialogoDetalleGrupoUiState(
    val g: GrupoTurnoCancha,
    val rep: ReservaCanchaDto,
    val cancelada: Boolean,
    val variasFranjas: Boolean,
    val saldoPend: Boolean,
    val saldoTotal: Double,
    val idsSaldo: List<Int>,
)

private fun segmentoCanchaConSaldoPendiente(rep: ReservaCanchaDto): Boolean =
    rep.adelanto < rep.montoTotal - 1e-6

private fun dialogoDetalleGrupoUiState(grupo: GrupoTurnoCancha): DialogoDetalleGrupoUiState {
    val rep = grupo.segmentos.first()
    val cancelada = grupo.segmentos.all { it.estado == "cancelado" }
    val saldoPend = !cancelada && grupo.segmentos.any(::segmentoCanchaConSaldoPendiente)
    val saldoTotal = grupo.montoTotalAcumulado - grupo.adelantoAcumulado
    val variasFranjas = grupo.segmentos.size > 1
    val idsSaldo = if (saldoPend) {
        grupo.segmentos.filter(::segmentoCanchaConSaldoPendiente).map { it.id }
    } else {
        emptyList()
    }
    return DialogoDetalleGrupoUiState(
        grupo,
        rep,
        cancelada,
        variasFranjas,
        saldoPend,
        saldoTotal,
        idsSaldo,
    )
}

@Composable
private fun DialogoDetalleGrupoTextoColumn(state: DialogoDetalleGrupoUiState) {
    val g = state.g
    val rep = state.rep
    Column {
        Text("Cliente: ${rep.nombreCliente}")
        Text("Deporte: ${etiquetaDeporte(rep.deporte)}")
        Text("Hora: ${g.horaInicioTexto} – ${g.horaFinTexto}")
        DialogoDetalleGrupoLineaFranjasMultiples(g, state.variasFranjas)
        Text("Total: S/ ${"%.2f".format(g.montoTotalAcumulado)}")
        Text("Adelanto: S/ ${"%.2f".format(g.adelantoAcumulado)}")
        Text("Estado: ${rep.estado}")
        DialogoDetalleGrupoLineaMotivoCancelacion(state.cancelada, rep)
        DialogoDetalleGrupoLineaSaldoPendiente(state.saldoPend, state.saldoTotal)
    }
}

@Composable
private fun DialogoDetalleGrupoBotonCobrar(
    saldoPend: Boolean,
    idsSaldo: List<Int>,
    viewModel: CanchaViewModel,
    onCerrar: () -> Unit,
) {
    if (!saldoPend) return
    TextButton(
        onClick = {
            viewModel.cobrarSaldoGrupoCancha(idsSaldo) { onCerrar() }
        },
    ) {
        Text("Marcar como pagado", color = HappyGreen, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DialogoDetalleGrupoBotonesConfirmar(
    cancelada: Boolean,
    onPedirCancelar: () -> Unit,
    onCerrar: () -> Unit,
) {
    Row {
        if (!cancelada) {
            TextButton(onClick = onPedirCancelar) {
                Text("Cancelar reserva", color = MaterialTheme.colorScheme.error)
            }
        }
        TextButton(onClick = onCerrar) { Text("Cerrar") }
    }
}

@Composable
private fun DialogoDetalleGrupoCancha(
    g: GrupoTurnoCancha,
    viewModel: CanchaViewModel,
    onCerrar: () -> Unit,
    onPedirCancelar: (GrupoTurnoCancha) -> Unit,
) {
    val s = dialogoDetalleGrupoUiState(g)
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { DialogoDetalleGrupoTitulo(s.cancelada, s.variasFranjas) },
        text = { DialogoDetalleGrupoTextoColumn(s) },
        dismissButton = {
            DialogoDetalleGrupoBotonCobrar(s.saldoPend, s.idsSaldo, viewModel, onCerrar)
        },
        confirmButton = {
            DialogoDetalleGrupoBotonesConfirmar(
                s.cancelada,
                onPedirCancelar = { onPedirCancelar(g) },
                onCerrar = onCerrar,
            )
        },
    )
}

@Composable
private fun DialogoCancelarReservaGrupo(
    gc: GrupoTurnoCancha,
    viewModel: CanchaViewModel,
    onCerrar: () -> Unit,
) {
    val claveDialogoCancelar = gc.segmentos.joinToString("-") { "${it.id}" }
    var motivo by remember(claveDialogoCancelar) { mutableStateOf("") }
    val motivoOk = motivo.trim().length >= 2
    val varias = gc.segmentos.size > 1
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (varias) "Cancelar turno completo" else "Cancelar reserva") },
        text = {
            Column {
                Text(
                    "Cliente: ${gc.segmentos.first().nombreCliente}",
                    fontSize = 14.sp,
                )
                if (varias) {
                    Text(
                        "Se cancelarán las ${gc.segmentos.size} franjas de ${gc.horaInicioTexto} a ${gc.horaFinTexto}.",
                        fontSize = 13.sp,
                        color = HappyTextSecondary,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Escribe el motivo que dio el cliente (obligatorio, mín. 2 caracteres).",
                    fontSize = 13.sp,
                    color = HappyTextSecondary,
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
            TextButton(onClick = onCerrar) { Text("Volver") }
        },
        confirmButton = {
            TextButton(
                enabled = motivoOk,
                onClick = {
                    viewModel.cancelarGrupoCancha(gc.ids, motivo.trim()) {
                        onCerrar()
                    }
                },
            ) {
                Text("Confirmar cancelación", color = MaterialTheme.colorScheme.error)
            }
        },
    )
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
    val estadoDiasMes by viewModel.estadoDiasMesCalendario.collectAsStateWithLifecycle()
    val loadingCalendarioMes by viewModel.loadingCalendarioMes.collectAsStateWithLifecycle()
    val errorCalendarioMes by viewModel.errorCalendarioMes.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.cargar() }

    var slotSel by remember { mutableStateOf<String?>(null) }
    var mostrarForm by remember { mutableStateOf(false) }
    var mostrarDetalleGrupo by remember { mutableStateOf<GrupoTurnoCancha?>(null) }
    var grupoParaCancelar by remember { mutableStateOf<GrupoTurnoCancha?>(null) }
    var avisoHorario by remember { mutableStateOf<String?>(null) }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mesCalendarioUi by remember { mutableStateOf(YearMonth.now()) }

    LaunchedEffect(mostrarDatePicker, mesCalendarioUi) {
        if (mostrarDatePicker) viewModel.cargarDisponibilidadMesCalendario(mesCalendarioUi)
    }

    val filasGrilla = remember(reservas, fecha) { construirFilasListaCancha(fecha, reservas) }
    val diasSemana = remember(fecha) { semanaDelMes(fecha) }
    val listState = rememberLazyListState()

    Column(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(HappyBgTop, HappyBgMiddle, HappyBgBottom))),
    ) {
        CanchaTituloPrincipal()
        CanchaBarraAyudaOtraHora(
            onOtraHora = {
                avisoHorario = null
                slotSel = null
                mostrarForm = true
            },
        )
        CanchaFilaMesYCalendario(
            fecha = fecha,
            onAbrirCalendario = {
                mesCalendarioUi = YearMonth.from(fecha)
                mostrarDatePicker = true
            },
        )
        NavegacionSemanaBar(
            onSemanaAnterior = { viewModel.irSemanaAnterior() },
            onSemanaSiguiente = { viewModel.irSemanaSiguiente() },
            modifier = Modifier.padding(bottom = 0.dp),
            textoAyuda = null,
        )
        CanchaSelectorChipsDiasSemana(
            diasSemana = diasSemana,
            fechaSeleccionada = fecha,
            onElegirDia = { viewModel.elegirFecha(it) },
        )
        CanchaFilaBotonIrHoy(onIrHoy = { viewModel.irHoy() })
        CanchaBloqueCargaErrores(
            loading = loading,
            mostrarForm = mostrarForm,
            error = error,
            avisoHorario = avisoHorario,
        )
        CanchaLazyListaFilasHorarios(
            filasGrilla = filasGrilla,
            fecha = fecha,
            listState = listState,
            onCeldaLibreClick = { horaSlot, pasado ->
                if (pasado) {
                    avisoHorario =
                        "Este horario ya pasó. Si el cliente entra en unos minutos, pulsa «Otra hora» y escribe la hora exacta."
                } else {
                    avisoHorario = null
                    slotSel = horaSlot
                    mostrarForm = true
                }
            },
            onCeldaOcupadaClick = { mostrarDetalleGrupo = it },
        )
        if (mostrarDatePicker) {
            CalendarioCanchaDisponibilidadDialog(
                mesVisible = mesCalendarioUi,
                fechaSeleccionada = fecha,
                estadoPorDia = estadoDiasMes,
                cargando = loadingCalendarioMes,
                errorCarga = errorCalendarioMes,
                onMesChange = { mesCalendarioUi = it },
                onElegirFecha = { d ->
                    viewModel.elegirFecha(d)
                    mostrarDatePicker = false
                },
                onDismiss = { mostrarDatePicker = false },
            )
        }
    }

    if (mostrarForm) {
        FormReservaCanchaDialog(
            fecha = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE),
            horaSlotPrellenado = slotSel,
            guardando = loading,
            onDismiss = {
                mostrarForm = false
                slotSel = null
            },
            onConfirm = { lista ->
                viewModel.crearReservasCancha(lista) {
                    mostrarForm = false
                    slotSel = null
                }
            },
        )
    }

    mostrarDetalleGrupo?.let { g ->
        DialogoDetalleGrupoCancha(
            g = g,
            viewModel = viewModel,
            onCerrar = { mostrarDetalleGrupo = null },
            onPedirCancelar = { gr ->
                grupoParaCancelar = gr
                mostrarDetalleGrupo = null
            },
        )
    }

    grupoParaCancelar?.let { gc ->
        DialogoCancelarReservaGrupo(
            gc = gc,
            viewModel = viewModel,
            onCerrar = { grupoParaCancelar = null },
        )
    }
}

private data class SlotCardPaleta(
    val accent: Color,
    val badgeBg: Color,
    val badgeTexto: String,
)

private fun slotCardPaleta(
    disponible: Boolean,
    finalizada: Boolean,
    conAdelanto: Boolean,
    saldoPendiente: Boolean,
): SlotCardPaleta {
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
    return SlotCardPaleta(accent, badgeBg, badgeTexto)
}

@Composable
private fun SlotCardFranjaLateral(accent: Color) {
    Box(
        modifier = Modifier
            .width(4.dp)
            .fillMaxHeight()
            .background(accent),
    )
}

@Composable
private fun SlotCardChipDuracionTramo(etiquetaDuracion: String) {
    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE3F2FD)) {
        Text(
            text = etiquetaDuracion,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1565C0),
        )
    }
}

@Composable
private fun SlotCardBadgeLineaEstado(paleta: SlotCardPaleta) {
    Surface(shape = RoundedCornerShape(50), color = paleta.badgeBg) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(paleta.accent, CircleShape),
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = paleta.badgeTexto,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = paleta.accent,
            )
        }
    }
}

@Composable
private fun SlotCardInteriorDisponible(
    horaInicio: String,
    horaFin: String,
    etiquetaDuracion: String,
    paleta: SlotCardPaleta,
    slotPasado: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "$horaInicio – $horaFin",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color(0xFF1A1A2E),
        )
        SlotCardChipDuracionTramo(etiquetaDuracion)
    }
    Spacer(Modifier.height(6.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        SlotCardBadgeLineaEstado(paleta)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (slotPasado) "Pasado" else "Toca",
                fontSize = 11.sp,
                color = Color(0xFF9E9E9E),
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Outlined.AddCircleOutline,
                contentDescription = null,
                tint = if (slotPasado) Color(0xFFBDBDBD) else HappyGreen,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

private data class SlotCardInteriorOcupadoProps(
    val horaInicio: String,
    val horaFin: String,
    val etiquetaDuracion: String,
    val reserva: ReservaCanchaDto,
    val paleta: SlotCardPaleta,
    val finalizada: Boolean,
    val saldoPendiente: Boolean,
    val unTurnoLargo: Boolean,
)

@Composable
private fun SlotCardInteriorOcupado(props: SlotCardInteriorOcupadoProps) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${props.horaInicio} – ${props.horaFin}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A2E),
                )
                Spacer(Modifier.width(8.dp))
                SlotCardChipDuracionTramo(props.etiquetaDuracion)
            }
            if (props.unTurnoLargo) {
                Text(
                    "Un solo turno (varias franjas)",
                    fontSize = 11.sp,
                    color = Color(0xFF5D4037),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                SlotCardBadgeLineaEstado(props.paleta)
            }
        }
    }
    Spacer(Modifier.height(6.dp))
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
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Column {
                Text(
                    text = props.reserva.nombreCliente,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF1A1A2E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = etiquetaDeporte(props.reserva.deporte),
                    fontSize = 12.sp,
                    color = Color(0xFF757575),
                )
            }
        }
        Icon(
            Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(20.dp),
        )
    }
    if (props.finalizada) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (props.saldoPendiente) "Finalizado · falta cobrar saldo" else "Finalizado",
            fontSize = 11.sp,
            color = Color(0xFF607D8B),
        )
    }
}

private data class SlotCardCanchaProps(
    val horaInicio: String,
    val horaFin: String,
    val reserva: ReservaCanchaDto?,
    val finalizada: Boolean,
    val etiquetaDuracion: String,
    val slotPasado: Boolean,
    val unTurnoLargo: Boolean,
    val hayAdelantoEnGrupo: Boolean,
    val onClick: () -> Unit,
)

@Composable
private fun SlotCardCancha(props: SlotCardCanchaProps) {
    val reserva = props.reserva
    val disponible = reserva == null
    val conAdelanto = props.hayAdelantoEnGrupo || reserva?.estado == "con_adelanto"
    val saldoPendiente = reserva != null && reserva.adelanto < reserva.montoTotal
    val paleta = slotCardPaleta(disponible, props.finalizada, conAdelanto, saldoPendiente)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (props.slotPasado) Modifier.alpha(0.52f) else Modifier)
            .clickable(onClick = props.onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            SlotCardFranjaLateral(paleta.accent)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                if (disponible) {
                    SlotCardInteriorDisponible(
                        props.horaInicio,
                        props.horaFin,
                        props.etiquetaDuracion,
                        paleta,
                        props.slotPasado,
                    )
                } else {
                    SlotCardInteriorOcupado(
                        SlotCardInteriorOcupadoProps(
                            horaInicio = props.horaInicio,
                            horaFin = props.horaFin,
                            etiquetaDuracion = props.etiquetaDuracion,
                            reserva = reserva!!,
                            paleta = paleta,
                            finalizada = props.finalizada,
                            saldoPendiente = saldoPendiente,
                            unTurnoLargo = props.unTurnoLargo,
                        ),
                    )
                }
            }
        }
    }
}

private fun parseSolesCanchaForm(texto: String): Double =
    texto.replace(",", ".").toDoubleOrNull() ?: 0.0

private data class MontosReservaFormularioUi(
    val total: Double,
    val adelantoEfectivo: Double,
    val saldoPendiente: Double,
)

private fun montosReservaFormularioUi(
    modoPago: ModoPagoCancha,
    monto: String,
    adelantoIngresado: String,
): MontosReservaFormularioUi {
    val totalNum = parseSolesCanchaForm(monto)
    val adelNum = when (modoPago) {
        ModoPagoCancha.Cancelado -> totalNum
        ModoPagoCancha.Adelanto -> parseSolesCanchaForm(adelantoIngresado)
    }
    val saldoPendiente = (totalNum - adelNum).coerceAtLeast(0.0)
    return MontosReservaFormularioUi(totalNum, adelNum, saldoPendiente)
}

private sealed class FormReservaCanchaGuardarResult {
    data class ErrorHorario(val mensaje: String) : FormReservaCanchaGuardarResult()
    data class Exito(val lista: List<ReservaCanchaCreate>) : FormReservaCanchaGuardarResult()
}

private fun adelantoEnviarValidadoForm(
    modoPago: ModoPagoCancha,
    montoTotal: Double,
    adelantoIngresado: String,
): Double? =
    when (modoPago) {
        ModoPagoCancha.Cancelado -> montoTotal
        ModoPagoCancha.Adelanto -> {
            val a = parseSolesCanchaForm(adelantoIngresado)
            if (a <= 0 || a > montoTotal) null else a
        }
    }

private fun intentarConstruirReservasCanchaForm(
    guardando: Boolean,
    nombre: String,
    monto: String,
    modoPago: ModoPagoCancha,
    adelantoIngresado: String,
    fechaParsed: LocalDate?,
    fechaIso: String,
    deporte: String,
    horaIniText: String,
    horaFinText: String,
): FormReservaCanchaGuardarResult? {
    if (guardando) return null
    val mt = parseSolesCanchaForm(monto)
    if (nombre.isBlank() || mt <= 0) return null
    val adEnviarTotal = adelantoEnviarValidadoForm(modoPago, mt, adelantoIngresado) ?: return null
    val fechaSel = fechaParsed ?: return null
    mensajeRangoCanchaInvalido(horaIniText, horaFinText, fechaSel)?.let {
        return FormReservaCanchaGuardarResult.ErrorHorario(it)
    }
    val franjas = franjasCanchaEntreDetalle(horaIniText, horaFinText, fechaSel)
    if (franjas.isEmpty()) {
        return FormReservaCanchaGuardarResult.ErrorHorario("Revisa las horas: no se pudo armar el turno.")
    }
    if (inicioTurnoCanchaEsPasado(fechaSel, horaIniText)) {
        return FormReservaCanchaGuardarResult.ErrorHorario(
            "La hora de inicio ya pasó. Pon una hora desde este minuto en adelante.",
        )
    }
    val n = franjas.size
    val montos = repartirMontoSoles(mt, n)
    val adelantos = repartirMontoSoles(adEnviarTotal, n)
    val lista = franjas.mapIndexed { i, f ->
        ReservaCanchaCreate(
            nombreCliente = nombre.trim(),
            deporte = deporte,
            fecha = fechaIso,
            hora = f.horaInicioSql,
            montoTotal = montos[i],
            adelanto = adelantos[i],
            duracionMinutos = f.duracionMinutos,
        )
    }
    return FormReservaCanchaGuardarResult.Exito(lista)
}

@Composable
private fun FormReservaCanchaSeccionFecha(fecha: String) {
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
}

@Composable
private fun FormReservaCanchaSeccionHorario(
    horaIniText: String,
    horaFinText: String,
    errorHorario: String?,
    onHoraIniChange: (String) -> Unit,
    onHoraFinChange: (String) -> Unit,
) {
    Text(
        "Horario de juego",
        fontSize = 13.sp,
        color = Color(0xFF757575),
        fontWeight = FontWeight.Medium,
    )
    Text(
        "Formato 24 h (1:30 pm = 13:30). Ej. 11:00 a 13:30: dos horas y media; el monto se reparte en cada tramo.",
        fontSize = 12.sp,
        color = Color(0xFF9E9E9E),
        lineHeight = 16.sp,
    )
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedTextField(
            value = horaIniText,
            onValueChange = onHoraIniChange,
            modifier = Modifier.weight(1f),
            label = { Text("Inicio (HH:mm)") },
            singleLine = true,
            placeholder = { Text("09:00") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HappyGreen),
        )
        OutlinedTextField(
            value = horaFinText,
            onValueChange = onHoraFinChange,
            modifier = Modifier.weight(1f),
            label = { Text("Fin (HH:mm)") },
            singleLine = true,
            placeholder = { Text("12:00") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HappyGreen),
        )
    }
    errorHorario?.let { err ->
        Spacer(Modifier.height(8.dp))
        Text(
            text = err,
            color = MaterialTheme.colorScheme.error,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
    Spacer(Modifier.height(20.dp))
}

@Composable
private fun FormReservaCanchaSeccionNombre(
    nombre: String,
    onNombreChange: (String) -> Unit,
) {
    Text("Nombre del Cliente", fontSize = 13.sp, color = Color(0xFF757575), fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = nombre,
        onValueChange = onNombreChange,
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
}

@Composable
private fun FormReservaCanchaSeccionDeporte(
    deporte: String,
    onElegirFutbol: () -> Unit,
    onElegirVoley: () -> Unit,
) {
    Text("Deporte", fontSize = 13.sp, color = Color(0xFF757575), fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HappyToggleOption(
            text = "Fútbol",
            selected = deporte == "Futbol",
            modifier = Modifier.weight(1f),
            onClick = onElegirFutbol,
        )
        HappyToggleOption(
            text = "Vóley",
            selected = deporte == "Voley",
            modifier = Modifier.weight(1f),
            onClick = onElegirVoley,
        )
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun FormReservaCanchaSeccionMonto(
    monto: String,
    onMontoChange: (String) -> Unit,
) {
    Text("Monto total (S/)", fontSize = 13.sp, color = Color(0xFF757575), fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = monto,
        onValueChange = onMontoChange,
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
}

@Composable
private fun FormReservaCanchaSeccionFormaPago(
    modoPago: ModoPagoCancha,
    adelantoIngresado: String,
    montosUi: MontosReservaFormularioUi,
    onModoCancelado: () -> Unit,
    onModoAdelanto: () -> Unit,
    onAdelantoChange: (String) -> Unit,
) {
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
            onClick = onModoCancelado,
        )
        ModoPagoOpcion(
            texto = "Adelanto",
            subtitulo = "Parcial",
            seleccionado = modoPago == ModoPagoCancha.Adelanto,
            modifier = Modifier.weight(1f),
            onClick = onModoAdelanto,
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
            onValueChange = onAdelantoChange,
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
    if (modoPago == ModoPagoCancha.Adelanto && montosUi.total > 0 && montosUi.saldoPendiente > 0) {
        Spacer(Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE8F5E9),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Saldo pendiente a pagar en local: S/ ${"%.2f".format(montosUi.saldoPendiente)}",
                modifier = Modifier.padding(14.dp),
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
        }
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun FormReservaCanchaBotonGuardar(
    guardando: Boolean,
    onGuardarClick: () -> Unit,
) {
    Button(
        onClick = onGuardarClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(52.dp),
        enabled = !guardando,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = HappyGreen),
    ) {
        Text("Guardar Reserva", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.width(8.dp))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
    }
}

@Composable
private fun FormReservaCanchaDialog(
    fecha: String,
    horaSlotPrellenado: String?,
    guardando: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (List<ReservaCanchaCreate>) -> Unit,
) {
    val fechaParsed = remember(fecha) { runCatching { LocalDate.parse(fecha) }.getOrNull() }
    val sugerencia = remember(fecha) {
        fechaParsed?.let { sugerenciaInicioFinCancha(it) } ?: ("09:00" to "10:00")
    }
    var nombre by remember { mutableStateOf("") }
    var deporte by remember { mutableStateOf("Futbol") }
    var monto by remember { mutableStateOf("30") }
    var modoPago by remember { mutableStateOf(ModoPagoCancha.Cancelado) }
    var adelantoIngresado by remember { mutableStateOf("") }
    var horaIniText by remember(horaSlotPrellenado, fecha) {
        mutableStateOf(
            if (horaSlotPrellenado != null) horaSlotPrellenado.take(5) else sugerencia.first,
        )
    }
    var horaFinText by remember(horaSlotPrellenado, fecha) {
        mutableStateOf(
            if (horaSlotPrellenado != null) horaFinCancha(horaSlotPrellenado).take(5) else sugerencia.second,
        )
    }
    var errorHorario by remember { mutableStateOf<String?>(null) }
    val scroll = rememberScrollState()
    val montosUi = montosReservaFormularioUi(modoPago, monto, adelantoIngresado)

    FullscreenFormDialogScaffold(
        guardando = guardando,
        onDismissRequest = onDismiss,
        title = "Registrar Reserva",
        scrollState = scroll,
        content = {
            FormReservaCanchaSeccionFecha(fecha)
            FormReservaCanchaSeccionHorario(
                horaIniText = horaIniText,
                horaFinText = horaFinText,
                errorHorario = errorHorario,
                onHoraIniChange = { v ->
                    errorHorario = null
                    horaIniText = v.filter { c -> c.isDigit() || c == ':' }.take(5)
                },
                onHoraFinChange = { v ->
                    errorHorario = null
                    horaFinText = v.filter { c -> c.isDigit() || c == ':' }.take(5)
                },
            )
            FormReservaCanchaSeccionNombre(nombre) { nombre = it }
            FormReservaCanchaSeccionDeporte(
                deporte = deporte,
                onElegirFutbol = { deporte = "Futbol" },
                onElegirVoley = { deporte = "Voley" },
            )
            FormReservaCanchaSeccionMonto(monto) { monto = it.filter { c -> c.isDigit() || c == '.' || c == ',' } }
            FormReservaCanchaSeccionFormaPago(
                modoPago = modoPago,
                adelantoIngresado = adelantoIngresado,
                montosUi = montosUi,
                onModoCancelado = { modoPago = ModoPagoCancha.Cancelado },
                onModoAdelanto = { modoPago = ModoPagoCancha.Adelanto },
                onAdelantoChange = { adelantoIngresado = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
            )
        },
        bottomBar = {
            FormReservaCanchaBotonGuardar(guardando = guardando) {
                when (
                    val r = intentarConstruirReservasCanchaForm(
                        guardando = guardando,
                        nombre = nombre,
                        monto = monto,
                        modoPago = modoPago,
                        adelantoIngresado = adelantoIngresado,
                        fechaParsed = fechaParsed,
                        fechaIso = fecha,
                        deporte = deporte,
                        horaIniText = horaIniText,
                        horaFinText = horaFinText,
                    )
                ) {
                    is FormReservaCanchaGuardarResult.ErrorHorario -> errorHorario = r.mensaje
                    is FormReservaCanchaGuardarResult.Exito -> onConfirm(r.lista)
                    null -> Unit
                }
            }
        },
    )
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
