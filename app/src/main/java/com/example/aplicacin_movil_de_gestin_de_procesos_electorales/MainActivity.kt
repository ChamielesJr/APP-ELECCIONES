package com.example.aplicacin_movil_de_gestin_de_procesos_electorales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.AppNavigation
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.theme.Aplicación_Movil_De_Gestión_De_Procesos_electoralesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Aplicación_Movil_De_Gestión_De_Procesos_electoralesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
