package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.candidatos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.io.File

data class CandidatoFormUiState(
    val nombres: String = "",
    val apellidos: String = "",
    val cargo: String = "",
    val foto: String = "",
    val listaId: Int? = null,
    val menuAbierto: Boolean = false,
    val menuCargoAbierto: Boolean = false,
    val error: String? = null
)

@Composable
fun CandidatoFormScreen(
    listas: List<ListaElectoralEntity>,
    candidatoExistente: CandidatoEntity?,
    state: CandidatoFormUiState,
    onNombresChange: (String) -> Unit,
    onApellidosChange: (String) -> Unit,
    onCargoChange: (String) -> Unit,
    onFotoChange: (String) -> Unit,
    onAbrirMenuCargo: () -> Unit,
    onCerrarMenuCargo: () -> Unit,
    onSeleccionarFoto: () -> Unit,
    onAbrirMenu: () -> Unit,
    onCerrarMenu: () -> Unit,
    onSeleccionarLista: (Int) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    val listaSeleccionada = listas.firstOrNull {
        it.id == state.listaId
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (candidatoExistente == null) {
                "Registrar candidato"
            } else {
                "Editar candidato"
            }
        )
        OutlinedTextField(
            value = state.nombres,
            onValueChange = onNombresChange,
            label = { Text("Nombres") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.apellidos,
            onValueChange = onApellidosChange,
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Text("Cargo del candidato")
        DropdownMenu(
            expanded = state.menuCargoAbierto,
            onDismissRequest = onCerrarMenuCargo
        ) {
            listOf("Presidente", "Vicepresidente").forEach { cargo ->
                DropdownMenuItem(
                    text = { Text(cargo) },
                    onClick = {
                        onCargoChange(cargo)
                        onCerrarMenuCargo()
                    }
                )
            }
        }
        OutlinedButton(
            onClick = onAbrirMenuCargo,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (state.cargo.isBlank()) {
                    "Seleccionar cargo"
                } else {
                    state.cargo
                }
            )
        }
        Text("Fotografía del candidato")
        if (state.foto.isNotBlank()) {
            AsyncImage(
                model = File(state.foto),
                contentDescription = "Fotografía del candidato",
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
        OutlinedButton(
            onClick = onSeleccionarFoto,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (state.foto.isNotBlank()) {
                    "Cambiar fotografía"
                } else {
                    "Seleccionar fotografía"
                }
            )
        }
        Text(
            text = if (state.foto.isNotBlank()) {
                "Fotografía seleccionada"
            } else {
                "No has seleccionado una fotografía."
            }
        )
        Text("Lista electoral")
        OutlinedButton(
            onClick = onAbrirMenu,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                listaSeleccionada?.let {
                    "${it.numero} - ${it.nombre}"
                } ?: "Seleccionar lista electoral"
            )
        }
        DropdownMenu(
            expanded = state.menuAbierto,
            onDismissRequest = onCerrarMenu
        ) {
            listas.forEach { lista ->
                DropdownMenuItem(
                    text = {
                        Text("${lista.numero} - ${lista.nombre}")
                    },
                    onClick = {
                        onSeleccionarLista(lista.id)
                    }
                )
            }
        }
        if (listas.isEmpty()) {
            Text("Primero debes registrar una lista electoral activa.")
        }
        state.error?.let { mensaje ->
            Text(mensaje)
        }
        Button(
            onClick = onGuardar,
            modifier = Modifier.fillMaxWidth(),
            enabled = listas.isNotEmpty()
        ) {
            Text("Guardar candidato")
        }
        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}
