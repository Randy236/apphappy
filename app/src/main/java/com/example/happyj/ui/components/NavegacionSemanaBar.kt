package com.example.happyj.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.happyj.ui.theme.HappyGreen

/**
 * Flechas grandes + texto claro para cambiar de semana (cancha, salones, etc.).
 * [textoAyuda] opcional: una línea breve bajo la barra (usuarios poco familiarizados con apps).
 */
@Composable
fun NavegacionSemanaBar(
    onSemanaAnterior: () -> Unit,
    onSemanaSiguiente: () -> Unit,
    modifier: Modifier = Modifier,
    textoAyuda: String? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFE8F5E9),
            shadowElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledTonalButton(
                    onClick = onSemanaAnterior,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1A1A2E),
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    elevation = ButtonDefaults.filledTonalButtonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp,
                    ),
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = "Ir a la semana anterior",
                        modifier = Modifier.size(26.dp),
                        tint = HappyGreen,
                    )
                    Spacer(Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Semana",
                            fontSize = 12.sp,
                            color = Color(0xFF607D8B),
                            lineHeight = 14.sp,
                        )
                        Text(
                            "anterior",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E),
                            lineHeight = 18.sp,
                        )
                    }
                }
                FilledTonalButton(
                    onClick = onSemanaSiguiente,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1A1A2E),
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    elevation = ButtonDefaults.filledTonalButtonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp,
                    ),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Text(
                            "Semana",
                            fontSize = 12.sp,
                            color = Color(0xFF607D8B),
                            lineHeight = 14.sp,
                        )
                        Text(
                            "siguiente",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E),
                            lineHeight = 18.sp,
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Ir a la semana siguiente",
                        modifier = Modifier.size(26.dp),
                        tint = HappyGreen,
                    )
                }
            }
        }
        textoAyuda?.let { ayuda ->
            Text(
                text = ayuda,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Color(0xFF607D8B),
                textAlign = TextAlign.Center,
            )
        }
    }
}
