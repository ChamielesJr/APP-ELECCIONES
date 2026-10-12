package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.listas

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import java.io.File
import java.io.FileOutputStream

private val AzulPrimario = Color(0xFF2468F2)
private val AzulMarino = Color(0xFF101B55)
private val FondoClaro = Color(0xFFF5F8FE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaElectoralFormScreen(
    listaExistente: ListaElectoralEntity? = null,
    presidenteExistente: String = "",
    vicepresidenteExistente: String = "",
    onGuardarConCandidatos: (ListaElectoralEntity, String, String) -> Unit = { _, _, _ -> },
    onGuardar: (ListaElectoralEntity) -> Unit = {},
    onCancelar: () -> Unit
) {
    val context = LocalContext.current

    var nombre by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.nombre ?: "")
    }
    var numero by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.numero?.toString() ?: "")
    }
    var descripcion by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.descripcion ?: "")
    }
    var presidente by rememberSaveable(listaExistente?.id) {
        mutableStateOf(presidenteExistente)
    }
    var vicepresidente by rememberSaveable(listaExistente?.id) {
        mutableStateOf(vicepresidenteExistente)
    }
    var color by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.color ?: "#2468F2")
    }
    var logo by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.logo ?: "")
    }
    var activa by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.estado ?: true)
    }
    var esVotoNulo by rememberSaveable(listaExistente?.id) {
        mutableStateOf(
            listaExistente?.nombre?.trim()?.equals("Voto Nulo", ignoreCase = true) == true
        )
    }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val pathLocal = guardarImagenEnAlmacenamientoLocal(context, it)
            if (pathLocal != null) {
                logo = pathLocal
                error = null
            } else {
                error = "Error al guardar la imagen del logo."
            }
        }
    }

    Scaffold(
        containerColor = FondoClaro,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (listaExistente == null) "Registrar lista electoral" else "Editar lista electoral",
                        fontWeight = FontWeight.Bold,
                        color = AzulMarino
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Datos de la lista",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulMarino
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            if (it.trim().equals("Voto Nulo", ignoreCase = true)) {
                                esVotoNulo = true
                            }
                            error = null
                        },
                        label = { Text("Nombre de la lista *") },
                        placeholder = { Text("Ej. Lista 1 - Juventud Activa") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = numero,
                        onValueChange = {
                            numero = it.filter { caracter -> caracter.isDigit() }
                            error = null
                        },
                        label = { Text(if (esVotoNulo) "Número de la lista" else "Número de la lista *") },
                        placeholder = { Text("Ej. 1") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción o lema") },
                        placeholder = { Text("Ej. El futuro de nuestra institución lo construimos juntos.") },
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!esVotoNulo) {
                        OutlinedTextField(
                            value = presidente,
                            onValueChange = {
                                presidente = it
                                error = null
                            },
                            label = { Text("Presidente *") },
                            placeholder = { Text("Ej. Lian Vicente Ávila Zambrano") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = vicepresidente,
                            onValueChange = {
                                vicepresidente = it
                                error = null
                            },
                            label = { Text("Vicepresidente *") },
                            placeholder = { Text("Ej. Pinargote Navarrete Scarleth Jamieth") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Sección del Logo (OBLIGATORIO para listas ordinarias)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (esVotoNulo) "Logo de la lista (Opcional para Voto Nulo)" else "Logo de la lista *",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulMarino
                        )
                    }

                    if (logo.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5FD)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = logo,
                                    contentDescription = "Logo de la lista",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Cambiar logo")
                                }

                                OutlinedButton(
                                    onClick = {
                                        logo = ""
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Quitar logo",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Quitar")
                                }
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddAPhoto,
                                contentDescription = null,
                                tint = AzulPrimario
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Seleccionar imagen del logo *",
                                color = AzulPrimario,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Sección de Color
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Color de la lista *",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulMarino
                    )

                    val coloresDisponibles = listOf(
                        "#2468F2",
                        "#C0C0C0",
                        "#FF0000",
                        "#95151F",
                        "#065F46",
                        "#9333EA",
                        "#F97316",
                        "#EAB308",
                        "#06B6D4",
                        "#252525"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        coloresDisponibles.take(5).forEach { codigo ->
                            SelectorColorCircle(
                                codigo = codigo,
                                seleccionado = color.equals(codigo, ignoreCase = true),
                                onClick = {
                                    color = codigo
                                    error = null
                                }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        coloresDisponibles.drop(5).forEach { codigo ->
                            SelectorColorCircle(
                                codigo = codigo,
                                seleccionado = color.equals(codigo, ignoreCase = true),
                                onClick = {
                                    color = codigo
                                    error = null
                                }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = color,
                        onValueChange = {
                            color = it
                            error = null
                        },
                        label = { Text("Código hexadecimal del color") },
                        placeholder = { Text("#2468F2") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Estado de la lista
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lista activa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulMarino
                        )
                        Text(
                            text = if (activa) "Disponible para votación" else "Desactivada temporalmente",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = activa,
                        onCheckedChange = { activa = it }
                    )
                }
            }

            if (error != null) {
                Text(
                    text = error.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    val nombreLimpio = nombre.trim()
                    val numeroLista = numero.toIntOrNull()

                    when {
                        nombreLimpio.isBlank() -> {
                            error = "Escribe el nombre de la lista."
                        }
                        !esVotoNulo && (numeroLista == null || numeroLista <= 0) -> {
                            error = "Introduce un número válido mayor a cero."
                        }
                        !esVotoNulo && presidente.isBlank() -> {
                            error = "Escribe el nombre del presidente."
                        }
                        !esVotoNulo && vicepresidente.isBlank() -> {
                            error = "Escribe el nombre del vicepresidente."
                        }
                        !esVotoNulo && presidente.trim().equals(vicepresidente.trim(), ignoreCase = true) -> {
                            error = "El presidente y el vicepresidente no pueden ser la misma persona."
                        }
                        color.isBlank() -> {
                            error = "Selecciona un color para la lista."
                        }
                        !esVotoNulo && logo.isBlank() -> {
                            error = "Debe seleccionar un logo para la lista electoral."
                        }
                        else -> {
                            val entidad = ListaElectoralEntity(
                                id = listaExistente?.id ?: 0,
                                nombre = nombreLimpio,
                                numero = numeroLista ?: 0,
                                descripcion = descripcion.trim().ifBlank { null },
                                color = color.trim(),
                                logo = logo.trim(),
                                estado = activa
                            )
                            onGuardarConCandidatos(entidad, presidente.trim(), vicepresidente.trim())
                            onGuardar(entidad)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    if (listaExistente == null) "Guardar lista" else "Guardar cambios",
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onCancelar,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Cancelar", color = AzulMarino, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SelectorColorCircle(
    codigo: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val colorCompose = remember(codigo) {
        try {
            Color(android.graphics.Color.parseColor(codigo))
        } catch (_: Exception) {
            Color.Gray
        }
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(colorCompose)
            .then(
                if (seleccionado) {
                    Modifier.border(width = 3.dp, color = AzulMarino, shape = CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable { onClick() }
    )
}

private fun guardarImagenEnAlmacenamientoLocal(context: Context, uri: Uri): String? {
    return try {
        val directory = File(context.filesDir, "logos")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val destinationFile = File(directory, "logo_${System.currentTimeMillis()}.png")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        destinationFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
