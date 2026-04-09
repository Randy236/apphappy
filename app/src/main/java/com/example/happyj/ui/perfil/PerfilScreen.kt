package com.example.happyj.ui.perfil

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happyj.data.NetworkModule
import com.example.happyj.data.PinUpdateBody
import com.example.happyj.data.Session
import com.example.happyj.ui.theme.HappyGreen
import com.example.happyj.viewmodel.AuthViewModel
import com.example.happyj.viewmodel.PerfilViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

private val fmtFechaLarga = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    session: Session,
    authViewModel: AuthViewModel,
    perfilViewModel: PerfilViewModel,
) {
    var expandPin by remember { mutableStateOf(false) }
    var expandHistorial by remember { mutableStateOf(false) }
    var pinActual by remember { mutableStateOf("") }
    var pinNuevo by remember { mutableStateOf("") }
    var mensajePin by remember { mutableStateOf<String?>(null) }
    var errorPin by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val lineas by perfilViewModel.lineasHistorial.collectAsStateWithLifecycle()
    val cargandoHist by perfilViewModel.cargandoHistorial.collectAsStateWithLifecycle()
    val errorHist by perfilViewModel.errorHistorial.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        perfilViewModel.cargarHistorial()
    }

    val inicial = session.nombre.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Column(
        modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            "Perfil",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E),
        )
        Spacer(Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(HappyGreen.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(inicial, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = HappyGreen)
                }
                Spacer(Modifier.size(16.dp))
                Column {
                    Text(session.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A2E))
                    Text(
                        if (session.rol == "administrador") "Administrador" else "Trabajador",
                        fontSize = 14.sp,
                        color = Color(0xFF757575),
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(HappyGreen.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Person, null, tint = HappyGreen)
                }
                Spacer(Modifier.size(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Nombre", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A2E))
                    Text(session.nombre, fontSize = 15.sp, color = Color(0xFF424242))
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        TarjetaMenu(
            titulo = "Cambiar PIN",
            subtitulo = if (expandPin) "Ocultar" else "Actualizar tu PIN de acceso",
            icono = { Icon(Icons.Outlined.Lock, null, tint = HappyGreen) },
            expandido = expandPin,
            onClick = { expandPin = !expandPin },
        )
        AnimatedVisibility(expandPin) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = pinActual,
                            onValueChange = { pinActual = it.filter { c -> c.isDigit() } },
                            label = { Text("PIN actual") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pinNuevo,
                            onValueChange = { pinNuevo = it.filter { c -> c.isDigit() } },
                            label = { Text("PIN nuevo") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        mensajePin?.let {
                            Text(it, color = HappyGreen, modifier = Modifier.padding(top = 8.dp), fontSize = 13.sp)
                        }
                        errorPin?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp), fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                mensajePin = null
                                errorPin = null
                                scope.launch {
                                    try {
                                        val res = NetworkModule.api.cambiarPin(
                                            session.userId,
                                            PinUpdateBody(
                                                pinActual = if (session.rol == "administrador") null else pinActual,
                                                pinNuevo = pinNuevo,
                                            ),
                                        )
                                        if (res.isSuccessful) {
                                            mensajePin = "PIN actualizado correctamente"
                                            pinActual = ""
                                            pinNuevo = ""
                                        } else {
                                            errorPin = "No se pudo cambiar el PIN"
                                        }
                                    } catch (e: Exception) {
                                        errorPin = e.message ?: "Error"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = HappyGreen),
                        ) {
                            Text("Guardar nuevo PIN")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        TarjetaMenu(
            titulo = "Historial de reservas",
            subtitulo = if (expandHistorial) "Ocultar listado" else "Cancha y salones recientes",
            icono = { Icon(Icons.Outlined.History, null, tint = HappyGreen) },
            expandido = expandHistorial,
            onClick = {
                expandHistorial = !expandHistorial
                if (expandHistorial) perfilViewModel.cargarHistorial()
            },
        )
        AnimatedVisibility(expandHistorial) {
            val scrollHist = rememberScrollState()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                when {
                    cargandoHist -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = HappyGreen)
                        }
                    }
                    errorHist != null -> {
                        Text(
                            errorHist!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                    lineas.isEmpty() -> {
                        Text(
                            "No hay reservas en el rango consultado.",
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp,
                        )
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp)
                                .verticalScroll(scrollHist)
                                .padding(PaddingValues(8.dp)),
                        ) {
                            lineas.forEach { linea ->
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (linea.etiquetaTipo == "Cancha") Color(0xFFE8F5E9) else Color(0xFFE3F2FD),
                                        ) {
                                            Text(
                                                linea.etiquetaTipo,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF424242),
                                            )
                                        }
                                        Text(
                                            linea.fecha.format(fmtFechaLarga),
                                            fontSize = 12.sp,
                                            color = Color(0xFF9E9E9E),
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(linea.titulo, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text(linea.detalle, fontSize = 13.sp, color = Color(0xFF757575))
                                    HorizontalDivider(Modifier.padding(top = 8.dp), color = Color(0xFFEEEEEE))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = { authViewModel.logout() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
        ) {
            Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(8.dp))
            Text("Cerrar sesión", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TarjetaMenu(
    titulo: String,
    subtitulo: String,
    icono: @Composable () -> Unit,
    expandido: Boolean = false,
    mostrarChevron: Boolean = true,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(HappyGreen.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                icono()
            }
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A2E))
                Text(subtitulo, fontSize = 13.sp, color = Color(0xFF9E9E9E))
            }
            if (mostrarChevron) {
                Icon(
                    if (expandido) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFFBDBDBD),
                )
            }
        }
    }
}
