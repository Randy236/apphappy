package com.example.happyj.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happyj.R
import com.example.happyj.ui.theme.HappyGreen
import com.example.happyj.ui.theme.HappyKeypadBg
import com.example.happyj.ui.theme.HappyKeypadText
import com.example.happyj.ui.theme.HappyTextSecondary
import com.example.happyj.viewmodel.AuthViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel) {
    var nombre by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()

    LaunchedEffect(loginError) {
        if (loginError != null) {
            pin = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Image(
            painter = painterResource(R.drawable.logo_happy_jump),
            contentDescription = "Happy Jump",
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(18.dp)),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Happy Jump",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Acceso de Trabajador",
            fontSize = 15.sp,
            color = HappyTextSecondary,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Nombre",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.SemiBold,
            color = HappyTextSecondary,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HappyGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PIN numérico",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.SemiBold,
            color = HappyTextSecondary,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = pin,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HappyGreen,
                unfocusedBorderColor = HappyGreen,
            ),
        )

        if (loginError != null) {
            Text(
                text = loginError!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.clearLoginError()
                viewModel.login(nombre, pin)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HappyGreen),
        ) {
            Text("Entrar", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
        NumericKeypad(
            onDigit = { d ->
                viewModel.clearLoginError()
                if (pin.length < 8) pin += d
            },
            onBackspace = {
                if (pin.isNotEmpty()) pin = pin.dropLast(1)
            },
        )
    }
}

@Composable
private fun NumericKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(null, "0", "bs"),
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.4f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HappyKeypadBg)
                            .clickable(enabled = cell != null) {
                                when (cell) {
                                    "bs" -> onBackspace()
                                    null -> {}
                                    else -> onDigit(cell)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        when (cell) {
                            null -> {}
                            "bs" -> androidx.compose.material3.Icon(
                                Icons.AutoMirrored.Outlined.Backspace,
                                contentDescription = "Borrar",
                                tint = HappyKeypadText,
                                modifier = Modifier.size(26.dp),
                            )
                            else -> Text(
                                text = cell,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = HappyKeypadText,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}
