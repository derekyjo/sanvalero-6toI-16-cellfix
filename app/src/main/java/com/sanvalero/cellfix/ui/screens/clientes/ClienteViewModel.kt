package com.sanvalero.cellfix.ui.screens.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanvalero.cellfix.domain.model.Cliente
import com.sanvalero.cellfix.domain.repository.CellFixRepository
import com.sanvalero.cellfix.domain.usecase.ValidadorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClienteViewModel @Inject constructor(
    private val repository: CellFixRepository,
    private val validador: ValidadorUseCase
) : ViewModel() {

    data class UiState(
        val clientes: List<Cliente> = emptyList(),
        val busqueda: String = ""
    )

    private val _busqueda = MutableStateFlow("")

    val state: StateFlow<UiState> = combine(
        repository.observarClientes(),
        _busqueda
    ) { lista, consulta ->
        val filtrada = if (consulta.isBlank()) {
            lista
        } else {
            lista.filter {
                it.nombre.contains(consulta, ignoreCase = true) ||
                    it.cedula.contains(consulta, ignoreCase = true) ||
                    it.telefono.contains(consulta, ignoreCase = true)
            }
        }
        UiState(filtrada, consulta)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun buscar(consulta: String) {
        _busqueda.value = consulta
    }

    fun guardar(cliente: Cliente, onResult: (Boolean, String?) -> Unit) {
        val error = when {
            !validador.requerido(cliente.nombre) -> "El nombre es obligatorio."
            !validador.cedulaValida(cliente.cedula) -> "La cédula debe tener 11 dígitos."
            !validador.telefonoValido(cliente.telefono) -> "El teléfono debe tener al menos 10 dígitos."
            !validador.emailValido(cliente.email) -> "El correo electrónico no es válido."
            else -> null
        }
        if (error != null) {
            onResult(false, error)
            return
        }
        viewModelScope.launch {
            runCatching { repository.guardarCliente(cliente) }
                .onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.message ?: "No se pudo guardar el cliente.") }
        }
    }

    fun eliminar(cliente: Cliente, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            runCatching { repository.eliminarCliente(cliente) }
                .onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.message ?: "No se pudo eliminar el cliente.") }
        }
    }
}
