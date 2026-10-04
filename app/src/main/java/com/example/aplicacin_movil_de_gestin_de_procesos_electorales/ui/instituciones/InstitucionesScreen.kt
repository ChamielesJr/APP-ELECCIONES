package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.instituciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InstitucionesScreen(
    onBack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Módulo de Gestión de Instituciones Educativas",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text(text = "Volver al Dashboard")
        }
    }
}
