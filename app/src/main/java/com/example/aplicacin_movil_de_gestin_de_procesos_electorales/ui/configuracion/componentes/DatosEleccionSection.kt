package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion.componentes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun DatosEleccionSection(
    nombreEleccion: String,
    onNombreEleccionChange: (String) -> Unit,
    escudoUri: Uri? = null,
    onEscudoSeleccionado: (Uri?) -> Unit = {},
    isErrorNombreEleccion: Boolean = false,
    isErrorEscudo: Boolean = false,
    modifier: Modifier = Modifier
) {

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onEscudoSeleccionado(uri)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // DATOS DE LA ELECCIÓN
        Text(
            text = "Datos de la elección",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = nombreEleccion,
            onValueChange = onNombreEleccionChange,
            label = { Text("Nombre de la elección *") },
            isError = isErrorNombreEleccion,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ESCUDO DE LA INSTITUCIÓN
        Text(
            text = "Escudo de la institución *",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedCard(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.Transparent
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = if (isErrorEscudo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (escudoUri != null) {

                        Image(
                            painter = rememberAsyncImagePainter(escudoUri),
                            contentDescription = "Escudo de la Institución",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {

                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Sin imagen",
                            tint = if (isErrorEscudo) MaterialTheme.colorScheme.error else Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Seleccionar archivo", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Formatos permitidos: JPG, PNG. Tamaño máximo: 2 MB.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}