package com.example.happyj.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.happyj.ui.theme.AdelantoAmarillo
import com.example.happyj.ui.theme.AdelantoAmarilloBg
import com.example.happyj.ui.theme.HappyTextPrimary

/**
 * Aviso visible y legible (icono + fondo suave), pensado para personas
 * que no están acostumbradas a leer mensajes técnicos en rojo plano.
 */
@Composable
fun BannerMensajeImportante(
    mensaje: String,
    modifier: Modifier = Modifier,
    /** Si no es null, aparece en negrita encima del detalle. */
    titulo: String? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = AdelantoAmarilloBg,
        shadowElevation = 0.dp,
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = AdelantoAmarillo,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                if (!titulo.isNullOrBlank()) {
                    Text(
                        titulo,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = HappyTextPrimary,
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF78350F),
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                )
            }
        }
    }
}
