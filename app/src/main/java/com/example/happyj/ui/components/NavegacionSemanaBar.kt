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
import androidx.compose.material3.MaterialTheme
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
import com.example.happyj.ui.theme.HappyGreenLight
import com.example.happyj.ui.theme.HappyTextPrimary
import com.example.happyj.ui.theme.HappyTextSecondary

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
            shape = RoundedCornerShape(20.dp),
            color = HappyGreenLight.copy(alpha = 0.55f),
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
                        contentColor = HappyTextPrimary,
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
                            style = MaterialTheme.typography.labelMedium,
                            color = HappyTextSecondary,
                        )
                        Text(
                            "anterior",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = HappyTextPrimary,
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
                        contentColor = HappyTextPrimary,
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
                            style = MaterialTheme.typography.labelMedium,
                            color = HappyTextSecondary,
                        )
                        Text(
                            "siguiente",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = HappyTextPrimary,
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
                style = MaterialTheme.typography.bodySmall,
                color = HappyTextSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}
