package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.usuarios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioFormDialog(
    usuarioEditando: UsuarioEntity?,
    instituciones: List<InstitucionEntity>,
    onDismiss: () -> Unit,
    onGuardar: (
        nombre: String,
        apellido: String,
        correo: String,
        contrasena: String,
        confirmacion: String,
        rol: String,
        institucionId: Int?,
        estado: Boolean
    ) -> Unit
) {
    var nombre by remember { mutableStateOf(usuarioEditando?.nombre ?: "") }
    var apellido by remember { mutableStateOf(usuarioEditando?.apellido ?: "") }
    var correo by remember { mutableStateOf(usuarioEditando?.correo ?: "") }
    var contrasena by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(usuarioEditando?.rol ?: "Docente") }
    var institucionId by remember { mutableStateOf(usuarioEditando?.institucionId) }
    var estado by remember { mutableStateOf(usuarioEditando?.estado ?: true) }

    val rolesDisponibles = listOf("Administrador", "Rector", "Docente")
    var expRol by remember { mutableStateOf(false) }
    var expInst by remember { mutableStateOf(false) }

    val institucionSeleccionadaNombre = instituciones.find { it.id == institucionId }?.nombre ?: "Ninguna"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (usuarioEditando == null) "Agregar Usuario" else "Editar Usuario",
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
                    label = { Text("Nombres *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellidos *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text(if (usuarioEditando == null) "Contraseña *" else "Nueva Contraseña (opcional)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = confirmacion,
                    onValueChange = { confirmacion = it },
                    label = { Text("Confirmar Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Dropdown de Rol
                ExposedDropdownMenuBox(
                    expanded = expRol,
                    onExpandedChange = { expRol = !expRol },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = rol,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol de usuario *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expRol) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expRol,
                        onDismissRequest = { expRol = false }
                    ) {
                        rolesDisponibles.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r) },
                                onClick = {
                                    rol = r
                                    expRol = false
                                }
                            )
                        }
                    }
                }

                // Dropdown de Institución
                ExposedDropdownMenuBox(
                    expanded = expInst,
                    onExpandedChange = { expInst = !expInst },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = institucionSeleccionadaNombre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Institución Asignada") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expInst) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expInst,
                        onDismissRequest = { expInst = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ninguna") },
                            onClick = {
                                institucionId = null
                                expInst = false
                            }
                        )
                        instituciones.forEach { inst ->
                            DropdownMenuItem(
                                text = { Text("${inst.nombre} (${inst.codigoInstitucional})") },
                                onClick = {
                                    institucionId = inst.id
                                    expInst = false
                                }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cuenta Activa", fontWeight = FontWeight.SemiBold)
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
                    onGuardar(nombre, apellido, correo, contrasena, confirmacion, rol, institucionId, estado)
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
