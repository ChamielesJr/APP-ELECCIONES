package com.example.aplicacin_movil_de_gestin_de_procesos_electorales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF7FAFF)
                ) {
                    LoginScreen()
                }
            }
        }
    }
}

@Composable
fun LoginScreen() {

    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var recordar by remember { mutableStateOf(false) }
    var mostrarPassword by remember { mutableStateOf(false) }
    var mostrarIdiomas by remember { mutableStateOf(false) }
    var idioma by remember { mutableStateOf("ES") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Fondo decorativo
        FondoDecorativo()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(35.dp))

            // Selector de idioma
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box {

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE8EEF8),
                                shape = RoundedCornerShape(30.dp)
                            ),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 10.dp)
                                .clickable {
                                    mostrarIdiomas = true
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = if (idioma == "ES") "🇪🇨" else "🇺🇸",
                                fontSize = 22.sp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = idioma,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF172B4D),
                                fontSize = 16.sp
                            )

                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Cambiar idioma",
                                tint = Color(0xFF172B4D)
                            )
                        }
                    }

                    if (mostrarIdiomas) {

                        Surface(
                            modifier = Modifier
                                .padding(top = 55.dp)
                                .width(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            shadowElevation = 8.dp
                        ) {
                            Column {

                                TextButton(
                                    onClick = {
                                        idioma = "ES"
                                        mostrarIdiomas = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("🇪🇨  Español")
                                }

                                TextButton(
                                    onClick = {
                                        idioma = "EN"
                                        mostrarIdiomas = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("🇺🇸  English")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ícono principal
            IconoElecciones()

            Spacer(modifier = Modifier.height(8.dp))

            // Título principal
            Text(
                text = "Elecciones",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14345C),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Estudiantiles",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1677E8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Distrito 13D02",
                fontSize = 28.sp,
                color = Color(0xFF596E8B),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Organización, transparencia\n" +
                        "y participación para un mejor futuro",
                fontSize = 18.sp,
                color = Color(0xFF607593),
                textAlign = TextAlign.Center,
                lineHeight = 27.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Tarjeta de login
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 22.dp,
                        vertical = 25.dp
                    )
                ) {

                    // Encabezado de tarjeta
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(58.dp),
                            tint = Color(0xFF1976D2)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {

                            Text(
                                text = "Bienvenido",
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16345D)
                            )

                            Text(
                                text = "Inicia sesión con tu cuenta institucional",
                                fontSize = 15.sp,
                                color = Color(0xFF71829A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // Correo
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Correo electrónico",
                                color = Color(0xFF93A2B7)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF687D97)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Contraseña",
                                color = Color(0xFF93A2B7)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF687D97)
                            )
                        },
                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    mostrarPassword = !mostrarPassword
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (mostrarPassword)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                    contentDescription = "Mostrar contraseña",
                                    tint = Color(0xFF71829A)
                                )
                            }
                        },
                        visualTransformation =
                            if (mostrarPassword)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Recordarme + contraseña olvidada
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = recordar,
                                onCheckedChange = {
                                    recordar = it
                                }
                            )

                            Text(
                                text = "Recordarme",
                                fontSize = 15.sp,
                                color = Color(0xFF586D87)
                            )
                        }

                        TextButton(
                            onClick = {
                                // Aquí después agregaremos recuperación de contraseña
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {

                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                color = Color(0xFF086BD8),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón iniciar sesión
                    Button(
                        onClick = {
                            // Aquí después conectaremos el login
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF096ACB)
                        )
                    ) {

                        Text(
                            text = "Iniciar sesión",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(15.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // Separador
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFD8DFE8)
                        )

                        Text(
                            text = "  Acceso para personal autorizado  ",
                            fontSize = 13.sp,
                            color = Color(0xFF697B94)
                        )

                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFD8DFE8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(45.dp))

            Text(
                text = "“Líderes hoy, mejores ciudadanos mañana”",
                fontSize = 15.sp,
                color = Color(0xFF526A89),
                fontWeight = FontWeight.Medium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Distrito 13D02",
                fontSize = 15.sp,
                color = Color(0xFF59718F)
            )

            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@Composable
fun IconoElecciones() {

    Box(
        modifier = Modifier.size(145.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.size(145.dp)
        ) {

            // Urna
            drawRoundRect(
                color = Color(0xFF1876D2),
                topLeft = Offset(25f, 60f),
                size = Size(95f, 65f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )

            // Parte superior
            drawRoundRect(
                color = Color(0xFF3B91EE),
                topLeft = Offset(18f, 50f),
                size = Size(110f, 25f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f)
            )

            // Hoja
            val hoja = Path().apply {
                moveTo(55f, 5f)
                lineTo(100f, 15f)
                lineTo(90f, 65f)
                lineTo(45f, 55f)
                close()
            }

            drawPath(
                path = hoja,
                color = Color(0xFFF8FBFF)
            )

            drawPath(
                path = hoja,
                color = Color(0xFFCADCF5),
                style = Stroke(width = 4f)
            )

            // Check
            val check = Path().apply {
                moveTo(60f, 35f)
                lineTo(72f, 47f)
                lineTo(94f, 27f)
            }

            drawPath(
                path = check,
                color = Color(0xFF2C83E8),
                style = Stroke(
                    width = 7f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )

            // Líneas decorativas
            drawLine(
                color = Color(0xFF4394ED),
                start = Offset(27f, 24f),
                end = Offset(17f, 19f),
                strokeWidth = 6f
            )

            drawLine(
                color = Color(0xFF4394ED),
                start = Offset(24f, 38f),
                end = Offset(12f, 38f),
                strokeWidth = 6f
            )

            drawLine(
                color = Color(0xFF4394ED),
                start = Offset(111f, 25f),
                end = Offset(120f, 17f),
                strokeWidth = 6f
            )

            drawLine(
                color = Color(0xFF4394ED),
                start = Offset(116f, 39f),
                end = Offset(129f, 37f),
                strokeWidth = 6f
            )
        }
    }
}

@Composable
fun FondoDecorativo() {

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {

        // Parte superior
        val pathSuperior = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, 90f)

            cubicTo(
                size.width * 0.75f,
                100f,
                size.width * 0.55f,
                135f,
                size.width * 0.35f,
                120f
            )

            cubicTo(
                size.width * 0.15f,
                105f,
                0f,
                95f,
                0f,
                70f
            )

            close()
        }

        drawPath(
            path = pathSuperior,
            color = Color(0xFFE8F2FF)
        )

        // Esquina inferior
        val pathInferior = Path().apply {
            moveTo(0f, size.height - 120f)

            cubicTo(
                size.width * 0.25f,
                size.height - 150f,
                size.width * 0.45f,
                size.height - 85f,
                size.width * 0.70f,
                size.height - 115f
            )

            cubicTo(
                size.width * 0.85f,
                size.height - 130f,
                size.width * 0.95f,
                size.height - 105f,
                size.width,
                size.height - 90f
            )

            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        drawPath(
            path = pathInferior,
            color = Color(0xFFE1F0FF)
        )
    }

}