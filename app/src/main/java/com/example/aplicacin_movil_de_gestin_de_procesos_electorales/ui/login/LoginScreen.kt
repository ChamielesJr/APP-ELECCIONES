package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.R

// Paleta de colores institucional basada en la referencia visual
private val NavyBlue = Color(0xFF102B5C)
private val InstitutionalBlue = Color(0xFF1761C5)
private val BackgroundBlue = Color(0xFFEAF4FF)
private val TextGrayBlue = Color(0xFF607493)
private val FieldBackground = Color(0xFFF1F5FA)
private val FieldBorder = Color(0xFFE2E8F0)

/**
 * Pantalla principal de Inicio de Sesión (Login) para la aplicación
 * "Elecciones Estudiantiles — Distrito 13D02".
 *
 * @param onLoginSuccess Callback invocado tras un inicio de sesión exitoso.
 * @param viewModel ViewModel que gestiona el estado y autenticación local.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onSnackbarDismissed()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Fondo con formas onduladas decorativas
            BackgroundWaves()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Selector de idioma superior derecho
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 0.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    LanguageSelector(
                        onSelectLanguage = { viewModel.onLanguageClick() }
                    )
                }

                // Logotipo institucional oficial (los textos están integrados en la imagen)
                InstitutionalLogoPlaceholder(
                    modifier = Modifier.padding(top = 0.dp, bottom = 0.dp)
                )

                // Tarjeta de inicio de sesión
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color(0x1A1761C5)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Encabezado de la tarjeta
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        color = BackgroundBlue,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                PersonIcon(
                                    tint = InstitutionalBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Bienvenido",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyBlue
                                    )
                                )
                                Text(
                                    text = "Inicia sesión con tu cuenta institucional",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = TextGrayBlue
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo de correo electrónico
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = NavyBlue, fontSize = 15.sp),
                            placeholder = {
                                Text(
                                    text = "Correo electrónico",
                                    color = TextGrayBlue.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                EmailIcon(
                                    tint = TextGrayBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            isError = uiState.emailError != null,
                            singleLine = true,
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = NavyBlue,
                                unfocusedTextColor = NavyBlue,
                                focusedContainerColor = FieldBackground,
                                unfocusedContainerColor = FieldBackground,
                                errorContainerColor = FieldBackground,
                                focusedBorderColor = InstitutionalBlue,
                                unfocusedBorderColor = Color.Transparent,
                                errorBorderColor = MaterialTheme.colorScheme.error,
                                focusedLeadingIconColor = TextGrayBlue,
                                unfocusedLeadingIconColor = TextGrayBlue,
                                cursorColor = InstitutionalBlue
                            )
                        )
                        if (uiState.emailError != null) {
                            Text(
                                text = uiState.emailError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Campo de contraseña
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = NavyBlue, fontSize = 15.sp),
                            placeholder = {
                                Text(
                                    text = "Contraseña",
                                    color = TextGrayBlue.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                LockIcon(
                                    tint = TextGrayBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { viewModel.onTogglePasswordVisibility() },
                                    enabled = !uiState.isLoading
                                ) {
                                    VisibilityToggleIcon(
                                        visible = uiState.isPasswordVisible,
                                        tint = TextGrayBlue
                                    )
                                }
                            },
                            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = uiState.passwordError != null,
                            singleLine = true,
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = NavyBlue,
                                unfocusedTextColor = NavyBlue,
                                focusedContainerColor = FieldBackground,
                                unfocusedContainerColor = FieldBackground,
                                errorContainerColor = FieldBackground,
                                focusedBorderColor = InstitutionalBlue,
                                unfocusedBorderColor = Color.Transparent,
                                errorBorderColor = MaterialTheme.colorScheme.error,
                                focusedLeadingIconColor = TextGrayBlue,
                                unfocusedLeadingIconColor = TextGrayBlue,
                                focusedTrailingIconColor = TextGrayBlue,
                                unfocusedTrailingIconColor = TextGrayBlue,
                                cursorColor = InstitutionalBlue
                            )
                        )
                        if (uiState.passwordError != null) {
                            Text(
                                text = uiState.passwordError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Opciones de Recordarme y Recuperación
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = uiState.rememberMe,
                                    onCheckedChange = { viewModel.onRememberMeChange(it) },
                                    enabled = !uiState.isLoading,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = InstitutionalBlue,
                                        uncheckedColor = TextGrayBlue
                                    )
                                )
                                Text(
                                    text = "Recordarme",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        color = TextGrayBlue
                                    )
                                )
                            }

                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = InstitutionalBlue,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier.clickable(enabled = !uiState.isLoading) {
                                    viewModel.onForgotPasswordClick()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Botón principal de Iniciar sesión
                        Button(
                            onClick = { viewModel.login(onSuccess = onLoginSuccess) },
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = InstitutionalBlue,
                                contentColor = Color.White
                            )
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.5.dp,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Iniciar sesión",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    ArrowForwardIcon(
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Línea divisoria decorativa inferior de la tarjeta
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = FieldBorder
                            )
                            Text(
                                text = "  Acceso para personal autorizado  ",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = TextGrayBlue
                                )
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = FieldBorder
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pie de página institucional
                Text(
                    text = "“Líderes hoy, mejores ciudadanos mañana”",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextGrayBlue,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Distrito 13D02",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextGrayBlue,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Dibujo decorativo de ondas suaves de fondo (superior e inferior).
 */
@Composable
private fun BackgroundWaves(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Onda decorativa superior
        val topPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, h * 0.16f)
            cubicTo(
                w * 0.35f, h * 0.20f,
                w * 0.65f, h * 0.10f,
                w, h * 0.14f
            )
            lineTo(w, 0f)
            close()
        }
        drawPath(path = topPath, color = BackgroundBlue)

        // Onda decorativa inferior
        val bottomPath = Path().apply {
            moveTo(0f, h)
            lineTo(0f, h * 0.88f)
            cubicTo(
                w * 0.35f, h * 0.84f,
                w * 0.70f, h * 0.92f,
                w, h * 0.86f
            )
            lineTo(w, h)
            close()
        }
        drawPath(path = bottomPath, color = BackgroundBlue)
    }
}

/**
 * Selector visual de idioma ubicado en la esquina superior derecha.
 */
@Composable
private fun LanguageSelector(
    onSelectLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onSelectLanguage() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EcuadorFlag()
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ES",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
            )
            Spacer(modifier = Modifier.width(2.dp))
            DropdownArrowIcon(
                tint = TextGrayBlue,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Representación visual de la bandera de Ecuador.
 */
@Composable
private fun EcuadorFlag(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(width = 18.dp, height = 12.dp)
            .clip(RoundedCornerShape(2.dp))
    ) {
        val w = size.width
        val h = size.height

        // Amarillo (50% superior)
        drawRect(
            color = Color(0xFFFFD100),
            topLeft = Offset(0f, 0f),
            size = Size(w, h * 0.5f)
        )
        // Azul (25% medio)
        drawRect(
            color = Color(0xFF0033A0),
            topLeft = Offset(0f, h * 0.5f),
            size = Size(w, h * 0.25f)
        )
        // Rojo (25% inferior)
        drawRect(
            color = Color(0xFFDA291C),
            topLeft = Offset(0f, h * 0.75f),
            size = Size(w, h * 0.25f)
        )
    }
}

/**
 * Logotipo institucional oficial de Elecciones Estudiantiles.
 */
@Composable
private fun InstitutionalLogoPlaceholder(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.logo_elecciones),
        contentDescription = "Logotipo Elecciones Estudiantiles",
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        contentScale = ContentScale.Fit
    )
}

/**
 * Ícono para alternar la visibilidad de la contraseña.
 */
@Composable
private fun VisibilityToggleIcon(
    visible: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = TextGrayBlue
) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val eyePath = Path().apply {
            moveTo(w * 0.1f, h * 0.5f)
            quadraticTo(w * 0.5f, h * 0.15f, w * 0.9f, h * 0.5f)
            quadraticTo(w * 0.5f, h * 0.85f, w * 0.1f, h * 0.5f)
            close()
        }
        drawPath(path = eyePath, color = tint, style = Stroke(width = strokeWidth))

        drawCircle(
            color = tint,
            radius = w * 0.18f,
            center = Offset(w * 0.5f, h * 0.5f)
        )

        if (!visible) {
            drawLine(
                color = tint,
                start = Offset(w * 0.15f, h * 0.85f),
                end = Offset(w * 0.85f, h * 0.15f),
                strokeWidth = strokeWidth * 1.2f,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Ícono vectorial para usuario / bienvenido.
 */
@Composable
private fun PersonIcon(
    modifier: Modifier = Modifier,
    tint: Color = TextGrayBlue
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()

        drawCircle(
            color = tint,
            radius = w * 0.22f,
            center = Offset(w * 0.5f, h * 0.32f),
            style = Stroke(width = strokeWidth)
        )

        val bodyPath = Path().apply {
            moveTo(w * 0.18f, h * 0.82f)
            quadraticTo(w * 0.18f, h * 0.58f, w * 0.5f, h * 0.58f)
            quadraticTo(w * 0.82f, h * 0.58f, w * 0.82f, h * 0.82f)
        }
        drawPath(path = bodyPath, color = tint, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

/**
 * Ícono vectorial para correo electrónico.
 */
@Composable
private fun EmailIcon(
    modifier: Modifier = Modifier,
    tint: Color = TextGrayBlue
) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val left = w * 0.1f
        val top = h * 0.22f
        val right = w * 0.9f
        val bottom = h * 0.78f

        val envPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = left,
                    top = top,
                    right = right,
                    bottom = bottom,
                    cornerRadius = CornerRadius(3.dp.toPx())
                )
            )
        }
        drawPath(path = envPath, color = tint, style = Stroke(width = strokeWidth))

        val flapPath = Path().apply {
            moveTo(left, top)
            lineTo(w * 0.5f, h * 0.52f)
            lineTo(right, top)
        }
        drawPath(path = flapPath, color = tint, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

/**
 * Ícono vectorial para candado (contraseña).
 */
@Composable
private fun LockIcon(
    modifier: Modifier = Modifier,
    tint: Color = TextGrayBlue
) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val shacklePath = Path().apply {
            moveTo(w * 0.3f, h * 0.45f)
            lineTo(w * 0.3f, h * 0.3f)
            cubicTo(w * 0.3f, h * 0.15f, w * 0.7f, h * 0.15f, w * 0.7f, h * 0.3f)
            lineTo(w * 0.7f, h * 0.45f)
        }
        drawPath(path = shacklePath, color = tint, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

        val bodyPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = w * 0.2f,
                    top = h * 0.42f,
                    right = w * 0.8f,
                    bottom = h * 0.85f,
                    cornerRadius = CornerRadius(3.dp.toPx())
                )
            )
        }
        drawPath(path = bodyPath, color = tint, style = Stroke(width = strokeWidth))

        drawCircle(
            color = tint,
            radius = w * 0.07f,
            center = Offset(w * 0.5f, h * 0.6f)
        )
    }
}

/**
 * Ícono vectorial para flecha hacia la derecha.
 */
@Composable
private fun ArrowForwardIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.2.dp.toPx()

        drawLine(
            color = tint,
            start = Offset(w * 0.15f, h * 0.5f),
            end = Offset(w * 0.82f, h * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        val headPath = Path().apply {
            moveTo(w * 0.55f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.5f)
            lineTo(w * 0.55f, h * 0.75f)
        }
        drawPath(path = headPath, color = tint, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

/**
 * Ícono vectorial para flecha desplegable de idioma.
 */
@Composable
private fun DropdownArrowIcon(
    modifier: Modifier = Modifier,
    tint: Color = TextGrayBlue
) {
    Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val arrowPath = Path().apply {
            moveTo(w * 0.2f, h * 0.35f)
            lineTo(w * 0.5f, h * 0.68f)
            lineTo(w * 0.8f, h * 0.35f)
        }
        drawPath(path = arrowPath, color = tint, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}
