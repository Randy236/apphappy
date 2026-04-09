package com.example.happyj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.happyj.ui.login.LoginScreen
import com.example.happyj.ui.main.MainShell
import com.example.happyj.ui.theme.HappyjTheme
import com.example.happyj.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyjTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val authVm: AuthViewModel = viewModel()
                    val session by authVm.session.collectAsStateWithLifecycle()
                    val sess = session
                    if (sess == null) {
                        LoginScreen(authVm)
                    } else {
                        MainShell(sess, authVm)
                    }
                }
            }
        }
    }
}
