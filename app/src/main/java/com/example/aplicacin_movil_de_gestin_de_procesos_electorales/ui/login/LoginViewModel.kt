package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.login

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.init.AdminInitResult
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.init.AdminInitializer
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.AuthRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.AuthResult
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val isSuccess: Boolean = false,
    val currentUser: UsuarioEntity? = null,
    val snackbarMessage: String? = null,
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val authRepository = AuthRepository(db.usuarioDao())

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Inicializar la cuenta de administrador en entornos de desarrollo (debug)
        viewModelScope.launch {
            try {
                val initResult = withContext(Dispatchers.IO) {
                    AdminInitializer.initialize(db.usuarioDao())
                }
                if (initResult is AdminInitResult.PendingCredentials) {
                    _uiState.update { it.copy(snackbarMessage = initResult.message) }
                }
            } catch (_: Exception) {
                // Excepción capturada para proteger el hilo de inicio
            }
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                loginError = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                loginError = null
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onRememberMeChange(checked: Boolean) {
        _uiState.update { it.copy(rememberMe = checked) }
    }

    fun onSnackbarDismissed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun onForgotPasswordClick() {
        _uiState.update {
            it.copy(snackbarMessage = "La recuperación de contraseña estará disponible próximamente.")
        }
    }

    fun onLanguageClick() {
        _uiState.update {
            it.copy(snackbarMessage = "Idioma predeterminado: Español (ES)")
        }
    }

    fun login(onSuccess: () -> Unit) {
        val currentEmail = _uiState.value.email.trim()
        val currentPassword = _uiState.value.password

        var isValid = true
        var emailErr: String? = null
        var passwordErr: String? = null

        if (currentEmail.isEmpty()) {
            emailErr = "El correo electrónico es obligatorio"
            isValid = false
        } else if (!currentEmail.contains("@") || !currentEmail.contains(".")) {
            emailErr = "Ingrese un correo electrónico válido"
            isValid = false
        }

        if (currentPassword.isEmpty()) {
            passwordErr = "La contraseña es obligatoria"
            isValid = false
        }

        if (!isValid) {
            _uiState.update {
                it.copy(
                    emailError = emailErr,
                    passwordError = passwordErr
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, loginError = null) }

        viewModelScope.launch {
            try {
                // Las consultas a Room se ejecutan en Dispatchers.IO
                val result = withContext(Dispatchers.IO) {
                    authRepository.authenticate(currentEmail, currentPassword)
                }

                // La actualización de sesión, UI y navegación se ejecuta en el Hilo Principal (Main)
                when (result) {
                    is AuthResult.Success -> {
                        SessionManager.setAuthenticatedUser(result.usuario)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                currentUser = result.usuario,
                                loginError = null
                            )
                        }
                        onSuccess()
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loginError = result.message,
                                snackbarMessage = result.message
                            )
                        }
                    }
                }
            } catch (e: SQLiteException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginError = "Error de acceso a la base de datos: ${e.localizedMessage}",
                        snackbarMessage = "Error de acceso a la base de datos: ${e.localizedMessage}"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginError = "Error en la autenticación: ${e.localizedMessage}",
                        snackbarMessage = "Error en la autenticación: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
}
