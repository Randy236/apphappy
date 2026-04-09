package com.example.happyj.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import com.example.happyj.viewmodel.AdminViewModel
import com.example.happyj.viewmodel.AuthViewModel
import com.example.happyj.viewmodel.CanchaViewModel
import com.example.happyj.viewmodel.PerfilViewModel
import com.example.happyj.viewmodel.SalonesViewModel
import com.example.happyj.ui.theme.HappyGreen

private val HeaderFondo = Color(0xFFF2F9F5)
private val HeaderTextoSec = Color(0xFF5C6B66)

@Composable
private fun BienvenidaHeader(nombreUsuario: String) {
    val nombre = nombreUsuario.trim().ifBlank { "Usuario" }
    Surface(
        color = HeaderFondo,
        tonalElevation = 0.dp,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hola, $nombre 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E),
                    lineHeight = 28.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Bienvenido a Happy Jump",
                    fontSize = 14.sp,
                    color = HeaderTextoSec,
                )
            }
            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = HappyGreen.copy(alpha = 0.14f),
            ) {
                Box(Modifier.size(46.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = HappyGreen,
                        modifier = Modifier.size(26.dp),
                    )
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
        topBar = { BienvenidaHeader(session.nombre) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    icon = { Icon(Icons.Filled.SportsBasketball, contentDescription = null) },
                    label = { Text("Cancha") },
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    icon = { Icon(Icons.Outlined.Celebration, contentDescription = null) },
                    label = { Text("Salones") },
                )
                if (isAdmin) {
                    NavigationBarItem(
                        selected = tab == adminTabIndex,
                        onClick = { tab = adminTabIndex },
                        icon = { Icon(Icons.Outlined.BarChart, contentDescription = null) },
                        label = { Text("Reportes") },
                    )
                }
                NavigationBarItem(
                    selected = tab == perfilTabIndex,
                    onClick = { tab = perfilTabIndex },
                    icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    label = { Text("Perfil") },
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
