package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.instituciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity

@Composable
fun InstitucionFormDialog(
    institucionEditando: InstitucionEntity?,
    onDismiss: () -> Unit,
    onGuardar: (
        nombre: String,
        codigo: String,
        direccion: String,
        telefono: String,
        correo: String,
        distrito: String,
        estado: Boolean
    ) -> Unit
) {
    var nombre by remember { mutableStateOf(institucionEditando?.nombre ?: "") }
    var codigo by remember { mutableStateOf(institucionEditando?.codigoInstitucional ?: "") }
    var direccion by remember { mutableStateOf(institucionEditando?.direccion ?: "") }
    var telefono by remember { mutableStateOf(institucionEditando?.telefono ?: "") }
    var correo by remember { mutableStateOf(institucionEditando?.correo ?: "") }
    var distrito by remember { mutableStateOf(institucionEditando?.distrito ?: "13D02") }
    var estado by remember { mutableStateOf(institucionEditando?.estado ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (institucionEditando == null) "Agregar Institución" else "Editar Institución",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF101B55)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la Institución *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código Institucional (AMIE) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono de contacto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = distrito,
                    onValueChange = { distrito = it },
                    label = { Text("Distrito Educativo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Institución Activa", fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = estado,
                        onCheckedChange = { estado = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(nombre, codigo, direccion, telefono, correo, distrito, estado)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2468F2))
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF667DAA))
            }
        },
        containerColor = Color.White
    )
}
