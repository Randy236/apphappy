package com.example.happyj.ui.login

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happyj.R
import com.example.happyj.ui.theme.HappyBgBottom
import com.example.happyj.ui.theme.HappyBgMiddle
import com.example.happyj.ui.theme.HappyBgTop
import com.example.happyj.ui.theme.HappyGreen
import com.example.happyj.ui.components.BannerMensajeImportante
import com.example.happyj.ui.theme.HappyTextSecondary
import com.example.happyj.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

private enum class PasoLogin { Nombre, Pin }

@Composable
fun LoginScreen(viewModel: AuthViewModel) {
    var nombre by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var paso by remember { mutableStateOf(PasoLogin.Nombre) }
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    val pinFocus = remember { FocusRequester() }

    LaunchedEffect(loginError) {
        if (loginError != null) {
            pin = ""
        }
    }

    LaunchedEffect(paso) {
        if (paso == PasoLogin.Pin) {
            delay(120)
            pinFocus.requestFocus()
        }
    }

    val fieldShape = RoundedCornerShape(16.dp)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = HappyGreen,
        focusedLabelColor = HappyGreen,
        cursorColor = HappyGreen,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(HappyBgTop, HappyBgMiddle, HappyBgBottom)))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        LoginMarcaYCabecera()
        Spacer(modifier = Modifier.height(22.dp))
        LoginFilaProgresoPasos(paso)
        LoginTextoAyudaPaso(paso)
        Spacer(modifier = Modifier.height(20.dp))

        Crossfade(targetState = paso, label = "loginPaso") { p ->
            when (p) {
                PasoLogin.Nombre -> LoginPasoNombre(
                    nombre = nombre,
                    fieldShape = fieldShape,
                    fieldColors = fieldColors,
                    onNombreChange = {
                        nombre = it
                        viewModel.clearLoginError()
                    },
                    onContinuar = {
                        if (nombre.isNotBlank()) {
                            pin = ""
                            viewModel.clearLoginError()
                            paso = PasoLogin.Pin
                        }
                    },
                )
                PasoLogin.Pin -> LoginPasoPin(
                    nombre = nombre,
                    pin = pin,
                    fieldShape = fieldShape,
                    fieldColors = fieldColors,
                    pinFocus = pinFocus,
                    onPinChange = { v ->
                        viewModel.clearLoginError()
                        pin = v.filter { it.isDigit() }.take(8)
                    },
                    onVolverNombre = {
                        paso = PasoLogin.Nombre
                        viewModel.clearLoginError()
                    },
                    onEntrar = {
                        viewModel.clearLoginError()
                        viewModel.login(nombre, pin)
                    },
                )
            }
        }

        if (loginError != null) {
            BannerMensajeImportante(
                titulo = "No se pudo entrar",
                mensaje = loginError!!,
                modifier = Modifier
                    .padding(top = 14.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun LoginMarcaYCabecera() {
    Image(
        painter = painterResource(R.drawable.logo_happy_jump),
        contentDescription = "Happy Jump",
        modifier = Modifier
            .size(112.dp)
            .clip(RoundedCornerShape(20.dp)),
        contentScale = ContentScale.Crop,
    )
    Spacer(modifier = Modifier.height(14.dp))
    Text(
        text = "Happy Jump",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Text(
        text = "Inicia sesión con tu usuario",
        fontSize = 15.sp,
        color = HappyTextSecondary,
        modifier = Modifier.padding(top = 4.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun LoginFilaProgresoPasos(paso: PasoLogin) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PasoIndicador(activo = paso == PasoLogin.Nombre, texto = "1")
        Spacer(Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { if (paso == PasoLogin.Pin) 1f else 0.35f },
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = HappyGreen,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        )
        Spacer(Modifier.width(8.dp))
        PasoIndicador(activo = paso == PasoLogin.Pin, texto = "2")
    }
}

@Composable
private fun LoginTextoAyudaPaso(paso: PasoLogin) {
    Text(
        text = if (paso == PasoLogin.Nombre) "Tu nombre" else "Tu PIN",
        fontSize = 12.sp,
        color = HappyTextSecondary,
        modifier = Modifier.padding(top = 6.dp),
    )
}

@Composable
private fun LoginPasoNombre(
    nombre: String,
    fieldShape: Shape,
    fieldColors: TextFieldColors,
    onNombreChange: (String) -> Unit,
    onContinuar: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = fieldShape,
            label = { Text("Nombre de usuario") },
            placeholder = { Text("Ej. Rosisela o Admin") },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = null,
                    tint = HappyGreen,
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
            ),
            colors = fieldColors,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Usa el teclado normal del teléfono. En el siguiente paso solo se pedirán números para el PIN.",
            fontSize = 12.sp,
            color = HappyTextSecondary,
            lineHeight = 16.sp,
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onContinuar,
            enabled = nombre.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = HappyGreen,
                disabledContainerColor = HappyGreen.copy(alpha = 0.35f),
            ),
        ) {
            Text("Continuar", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        }
    }
}

@Composable
private fun LoginPasoPin(
    nombre: String,
    pin: String,
    fieldShape: Shape,
    fieldColors: TextFieldColors,
    pinFocus: FocusRequester,
    onPinChange: (String) -> Unit,
    onVolverNombre: () -> Unit,
    onEntrar: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onVolverNombre) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF1A1A2E),
                )
            }
            Text(
                "Confirma tu PIN",
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = Color(0xFF1A1A2E),
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(8.dp))
        LoginTarjetaResumenUsuario(nombre, onVolverNombre)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = pin,
            onValueChange = onPinChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(pinFocus),
            singleLine = true,
            shape = fieldShape,
            label = { Text("PIN (solo números)") },
            placeholder = { Text("Mínimo 4 dígitos") },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = HappyGreen,
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            colors = fieldColors,
        )
        Text(
            "Aquí aparece el teclado numérico del celular. El PIN no se muestra en pantalla.",
            fontSize = 12.sp,
            color = HappyTextSecondary,
            modifier = Modifier.padding(top = 8.dp),
            lineHeight = 16.sp,
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onEntrar,
            enabled = pin.length >= 4,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = HappyGreen,
                disabledContainerColor = HappyGreen.copy(alpha = 0.35f),
            ),
        ) {
            Text("Entrar", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        }
    }
}

@Composable
private fun LoginTarjetaResumenUsuario(nombre: String, onCambiarUsuario: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.92f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Usuario",
                    fontSize = 11.sp,
                    color = HappyTextSecondary,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    nombre.trim().ifBlank { "—" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A2E),
                )
            }
            TextButton(onClick = onCambiarUsuario) {
                Text("Cambiar", color = HappyGreen, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun PasoIndicador(activo: Boolean, texto: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (activo) HappyGreen else MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (activo) Color.White else HappyTextSecondary,
        )
    }
}
