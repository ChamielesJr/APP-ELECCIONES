package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.listas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaElectoralFormScreen(
    listaExistente: ListaElectoralEntity? = null,
    onGuardar: (ListaElectoralEntity) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.nombre ?: "")
    }
    var numero by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.numero?.toString() ?: "")
    }
    var descripcion by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.descripcion ?: "")
    }
    var color by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.color ?: "")
    }
    var logo by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.logo ?: "")
    }
    var activa by rememberSaveable(listaExistente?.id) {
        mutableStateOf(listaExistente?.estado ?: true)
    }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (listaExistente == null) {
                            "Registrar lista electoral"
                        } else {
                            "Editar lista electoral"
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Completa los datos de la lista.",
                style = MaterialTheme.typography.bodyLarge
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    error = null
                },
                label = { Text("Nombre de la lista *") },
                placeholder = { Text("Ej. Lista Esperanza") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = numero,
                onValueChange = {
                    numero = it.filter { caracter -> caracter.isDigit() }
                    error = null
                },
                label = { Text("Número de la lista *") },
                placeholder = { Text("Ej. 1") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                placeholder = { Text("Describe brevemente la lista") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color de la lista *") },
                placeholder = { Text("Ej. Azul o #1565C0") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            val coloresDisponibles = listOf(
                "#2463EB",
                "#065F46",
                "#DC2626",
                "#9333EA",
                "#F97316",
                "#EAB308",
                "#06B6D4",
                "#EC4899",
                "#252525",
                "#0D9488"
            )
            Text(
                text = "Color de la lista",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                coloresDisponibles.forEach { codigo ->
                    val colorCompose = Color(
                        android.graphics.Color.parseColor(codigo)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(
                            modifier = Modifier
                                .size(30.dp)
                                .background(colorCompose, CircleShape)
                                .then(
                                    if (color == codigo) {
                                        Modifier.border(
                                            width = 3.dp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            shape = CircleShape
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable {
                                    color = codigo
                                    error = null
                                }
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = run {
                                try {
                                    Color(android.graphics.Color.parseColor(color))
                                } catch (_: Exception) {
                                    Color.Gray
                                }
                            },
                            shape = CircleShape
                        )
                )
                Text(
                    text = if (color.isBlank()) {
                        "Selecciona un color"
                    } else {
                        color
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            OutlinedTextField(
                value = logo,
                onValueChange = { logo = it },
                label = { Text("Logo (opcional)") },
                placeholder = { Text("Nombre o referencia del logo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lista activa",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (activa) {
                            "Disponible"
                        } else {
                            "Inactiva"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Switch(
                    checked = activa,
                    onCheckedChange = { activa = it }
                )
            }
            if (error != null) {
                Text(
                    text = error.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    val nombreLimpio = nombre.trim()
                    val numeroLista = numero.toIntOrNull()

                    when {
                        nombreLimpio.isBlank() -> {
                            error = "Escribe el nombre de la lista."
                        }
                        numeroLista == null || numeroLista <= 0 -> {
                            error = "Introduce un número mayor que cero."
                        }
                        color.isBlank() -> {
                            error = "Escribe el color de la lista."
                        }
                        else -> {
                            onGuardar(
                                ListaElectoralEntity(
                                    id = listaExistente?.id ?: 0,
                                    nombre = nombreLimpio,
                                    numero = numeroLista,
                                    descripcion = descripcion.trim()
                                        .ifBlank { null },
                                    color = color.trim(),
                                    logo = logo.trim(),
                                    estado = activa
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar lista")
            }
            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}
