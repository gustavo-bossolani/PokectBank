package com.pokect.bank.kids.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoginTab: Boolean = true,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val selectedAvatar: Int = 1,
    val step: Int = 1,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun toggleTab(isLogin: Boolean) {
        _uiState.update {
            it.copy(
                isLoginTab = isLogin,
                step = 1,
                name = "",
                email = "",
                password = "",
                error = null
            )
        }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value, error = null) }
    }

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value.trim(), error = null) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun selectAvatar(id: Int) {
        _uiState.update { it.copy(selectedAvatar = id) }
    }

    fun nextStep() {
        val state = _uiState.value
        if (state.isLoginTab) {
            login()
        } else {
            // Signup: validate then advance to step 2
            if (state.name.isBlank()) {
                _uiState.update { it.copy(error = "Qual é o seu nome?") }
                return
            }
            if (state.email.isBlank()) {
                _uiState.update { it.copy(error = "Digite seu e-mail para continuar") }
                return
            }
            if (state.password.isBlank()) {
                _uiState.update { it.copy(error = "Digite sua senha secreta") }
                return
            }
            _uiState.update { it.copy(step = 2) }
        }
    }

    fun previousStep() {
        _uiState.update { it.copy(step = 1) }
    }

    fun login() {
        val state = _uiState.value

        if (state.email.isBlank()) {
            _uiState.update { it.copy(error = "Digite seu e-mail para continuar") }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(error = "Digite sua senha secreta") }
            return
        }
        if (!state.isLoginTab && state.name.isBlank()) {
            _uiState.update { it.copy(error = "Qual é o seu nome?") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            delay(1500)
            _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
        }
    }

    fun forgotPassword() {
        _uiState.update { it.copy(error = "Link enviado para o email! 📧") }
        viewModelScope.launch {
            delay(3000)
            _uiState.update { it.copy(error = null) }
        }
    }
}
