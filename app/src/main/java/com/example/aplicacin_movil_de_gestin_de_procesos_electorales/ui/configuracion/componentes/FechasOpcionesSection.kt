package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion.componentes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.Locale

@Composable
fun FechasOpcionesSection(
    fechaInicio: String,
    onFechaInicioChange: (String) -> Unit,
    horaInicio: String,
    onHoraInicioChange: (String) -> Unit,
    fechaFin: String,
    onFechaFinChange: (String) -> Unit,
    horaFin: String,
    onHoraFinChange: (String) -> Unit,
    habilitarEleccion: Boolean,
    onHabilitarEleccionChange: (Boolean) -> Unit,
    publicarResultados: Boolean,
    onPublicarResultadosChange: (Boolean) -> Unit,
    onGuardarClick: () -> Unit,
    onCancelarClick: () -> Unit,
    isErrorFechas: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    fun mostrarDatePicker(onFechaSeleccionada: (String) -> Unit) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val fechaFormatted = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)
                onFechaSeleccionada(fechaFormatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun mostrarTimePicker(onHoraSeleccionada: (String) -> Unit) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val horaFormatted = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                onHoraSeleccionada(horaFormatted)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // FECHA Y HORARIO
        Text(
            text = "Fecha y horario *",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Fecha y Hora de Inicio
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de inicio *") },

                    leadingIcon = {
                        IconButton(onClick = { mostrarDatePicker(onFechaInicioChange) }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha inicio")
                        }
                    },
                    isError = isErrorFechas && fechaInicio.isBlank(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { mostrarDatePicker(onFechaInicioChange) }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora de inicio *") },
                    isError = isErrorFechas && horaInicio.isBlank(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { mostrarTimePicker(onHoraInicioChange) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Fecha y Hora de Finalización
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = fechaFin,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de fin *") },
                    // ICONO POSICIONADO A LA IZQUIERDA
                    leadingIcon = {
                        IconButton(onClick = { mostrarDatePicker(onFechaFinChange) }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha fin")
                        }
                    },
                    isError = isErrorFechas && fechaFin.isBlank(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { mostrarDatePicker(onFechaFinChange) }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = horaFin,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora de fin *") },
                    isError = isErrorFechas && horaFin.isBlank(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { mostrarTimePicker(onHoraFinChange) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // OPCIONES DE LA ELECCIÓN
        Text(
            text = "Opciones de la elección",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = habilitarEleccion,
                onCheckedChange = onHabilitarEleccionChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF1976D2),
                    uncheckedColor = Color.Gray,
                    checkmarkColor = Color.White
                )
            )
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = "Habilitar elección",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "La votación solo estará activa dentro de las fechas y horas configuradas.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = publicarResultados,
                onCheckedChange = onPublicarResultadosChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF1976D2),
                    uncheckedColor = Color.Gray,
                    checkmarkColor = Color.White
                )

            )
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = "Publicar resultados",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Permitir mostrar los resultados en la sección pública cuando el modo esté disponible.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // -BOTONES GUARDAR Y CANCELAR
        Button(
            onClick = onGuardarClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White
            )
        ) {
            Text(text = "Guardar configuración", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onCancelarClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Cancelar", fontSize = 14.sp)
        }
    }
}