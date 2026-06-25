package com.example.happyj.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.happyj.data.Session
import com.example.happyj.ui.admin.AdminScreen
import com.example.happyj.ui.cancha.CanchaScreen
import com.example.happyj.ui.perfil.PerfilScreen
import com.example.happyj.ui.salones.SalonesScreen
import com.example.happyj.ui.theme.HappyGreen
import com.example.happyj.ui.theme.HappyGreenDark
import com.example.happyj.ui.theme.HappyGreenSoft
import com.example.happyj.ui.theme.HappyTextPrimary
import com.example.happyj.ui.theme.HappyTextSecondary
import com.example.happyj.viewmodel.AdminViewModel
import com.example.happyj.viewmodel.AuthViewModel
import com.example.happyj.viewmodel.CanchaViewModel
import com.example.happyj.viewmodel.PerfilViewModel
import com.example.happyj.viewmodel.SalonesViewModel

private fun etiquetaRolUsuario(rol: String): String = when (rol) {
    "administrador" -> "Administrador"
    "trabajador" -> "Trabajador"
    else -> rol.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

@Composable
private fun BienvenidaHeader(nombreUsuario: String, rol: String) {
    val nombre = nombreUsuario.trim().ifBlank { "Usuario" }
    val rolTxt = etiquetaRolUsuario(rol)
    Surface(
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(HappyGreenSoft, Color.White, HappyGreenSoft.copy(alpha = 0.5f)),
                    ),
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hola, $nombre 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = HappyTextPrimary,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Bienvenido a Happy Jump",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HappyTextSecondary,
                    )
                    Spacer(Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = HappyGreen.copy(alpha = 0.14f),
                    ) {
                        Text(
                            text = rolTxt,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = HappyGreenDark,
                        )
                    }
                }
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    color = HappyGreen,
                    shadowElevation = 4.dp,
                ) {
                    Box(Modifier.size(50.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainShell(
    session: Session,
    authViewModel: AuthViewModel,
) {
    val isAdmin = session.rol == "administrador"
    var tab by remember { mutableIntStateOf(0) }

    val canchaVm: CanchaViewModel = viewModel()
    val salonesVm: SalonesViewModel = viewModel()
    val adminVm: AdminViewModel = viewModel()
    val perfilVm: PerfilViewModel = viewModel()

    val adminTabIndex = if (isAdmin) 2 else -1
    val perfilTabIndex = if (isAdmin) 3 else 2

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { BienvenidaHeader(session.nombre, session.rol) },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
            ) {
                val navColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = HappyGreen,
                    selectedTextColor = HappyGreenDark,
                    indicatorColor = HappyGreen.copy(alpha = 0.12f),
                    unselectedIconColor = HappyTextSecondary,
                    unselectedTextColor = HappyTextSecondary,
                )
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    icon = { Icon(Icons.Filled.SportsBasketball, contentDescription = null) },
                    label = { Text("Cancha", fontWeight = if (tab == 0) FontWeight.SemiBold else FontWeight.Normal) },
                    colors = navColors,
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    icon = { Icon(Icons.Outlined.Celebration, contentDescription = null) },
                    label = { Text("Salones", fontWeight = if (tab == 1) FontWeight.SemiBold else FontWeight.Normal) },
                    colors = navColors,
                )
                if (isAdmin) {
                    NavigationBarItem(
                        selected = tab == adminTabIndex,
                        onClick = { tab = adminTabIndex },
                        icon = { Icon(Icons.Outlined.BarChart, contentDescription = null) },
                        label = {
                            Text(
                                "Reportes",
                                fontWeight = if (tab == adminTabIndex) FontWeight.SemiBold else FontWeight.Normal,
                            )
                        },
                        colors = navColors,
                    )
                }
                NavigationBarItem(
                    selected = tab == perfilTabIndex,
                    onClick = { tab = perfilTabIndex },
                    icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    label = {
                        Text(
                            "Perfil",
                            fontWeight = if (tab == perfilTabIndex) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                    colors = navColors,
                )
            }
        },
    ) { padding ->
        val mod = Modifier.padding(padding)
        when (tab) {
            0 -> CanchaScreen(mod, canchaVm)
            1 -> SalonesScreen(mod, salonesVm, soloConsulta = isAdmin)
            2 -> if (isAdmin) {
                AdminScreen(mod, adminVm)
            } else {
                PerfilScreen(mod, session, authViewModel, perfilVm)
            }
            3 -> PerfilScreen(mod, session, authViewModel, perfilVm)
            else -> {}
        }
    }
}
