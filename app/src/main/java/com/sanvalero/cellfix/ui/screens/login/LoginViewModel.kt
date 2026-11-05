package com.sanvalero.cellfix.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanvalero.cellfix.data.session.SessionManager
import com.sanvalero.cellfix.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val session: SessionManager
) : ViewModel() {

    data class UiState(
        val username: String = "",
        val password: String = "",
        val cargando: Boolean = false,
        val error: String? = null,
        val exito: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun onUsername(valor: String) {
        _state.update { it.copy(username = valor, error = null) }
    }

    fun onPassword(valor: String) {
        _state.update { it.copy(password = valor, error = null) }
    }

    fun consumirExito() {
        _state.update { it.copy(exito = false) }
    }

    fun entrar() {
        val actual = _state.value
        if (actual.cargando) return
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }
            loginUseCase(actual.username, actual.password)
                .onSuccess { usuario ->
                    session.iniciar(usuario)
                    _state.update { it.copy(cargando = false, exito = true) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(cargando = false, error = error.message ?: "No se pudo iniciar sesión.")
                    }
                }
        }
    }
}
