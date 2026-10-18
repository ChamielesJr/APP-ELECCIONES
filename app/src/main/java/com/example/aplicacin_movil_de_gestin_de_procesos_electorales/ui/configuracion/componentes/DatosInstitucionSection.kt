package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion.ConfiguracionState

@Composable
fun DatosInstitucionSection(
    nombreInstitucion: String,
    onNombreInstitucionChange: (String) -> Unit,
    mensajeInstitucional: String,
    onMensajeChange: (String) -> Unit,
    isErrorNombre: Boolean = false,
    isErrorMensaje: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Título de la sección
        Text(
            text = "Datos de la institución",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Campo 1: Nombre de la institución *
        OutlinedTextField(
            value = nombreInstitucion,
            onValueChange = onNombreInstitucionChange,
            label = { Text("Nombre de la institución *") },
            isError = isErrorNombre,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo 2: Mensaje institucional * (Obligatorio)
        OutlinedTextField(
            value = mensajeInstitucional,
            onValueChange = onMensajeChange,
            label = { Text("Mensaje institucional *") },
            isError = isErrorMensaje,
            minLines = 3,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Este mensaje se mostrará en la pantalla pública de votación.",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${mensajeInstitucional.length}/${ConfiguracionState.MAX_MENSAJE}",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.End
            )
        }
    }
}