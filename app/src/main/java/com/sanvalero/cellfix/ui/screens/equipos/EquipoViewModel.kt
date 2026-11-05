package com.sanvalero.cellfix.ui.screens.equipos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanvalero.cellfix.domain.model.Cliente
import com.sanvalero.cellfix.domain.model.Equipo
import com.sanvalero.cellfix.domain.model.EquipoDetalle
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
class EquipoViewModel @Inject constructor(
    private val repository: CellFixRepository,
    private val validador: ValidadorUseCase
) : ViewModel() {

    data class UiState(
        val equipos: List<EquipoDetalle> = emptyList(),
        val clientes: List<Cliente> = emptyList(),
        val busqueda: String = ""
    )

    private val _busqueda = MutableStateFlow("")

    val state: StateFlow<UiState> = combine(
        repository.observarEquipos(),
        repository.observarClientes(),
        _busqueda
    ) { equipos, clientes, consulta ->
        val filtrados = if (consulta.isBlank()) {
            equipos
        } else {
            equipos.filter {
                it.marca.contains(consulta, ignoreCase = true) ||
                    it.modelo.contains(consulta, ignoreCase = true) ||
                    it.imei.contains(consulta, ignoreCase = true) ||
                    it.clienteNombre.contains(consulta, ignoreCase = true)
            }
        }
        UiState(filtrados, clientes, consulta)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun buscar(consulta: String) {
        _busqueda.value = consulta
    }

    fun guardar(equipo: Equipo, onResult: (Boolean, String?) -> Unit) {
        val error = when {
            equipo.clienteId <= 0L -> "Selecciona el cliente dueño del equipo."
            !validador.requerido(equipo.marca) -> "La marca es obligatoria."
            !validador.requerido(equipo.modelo) -> "El modelo es obligatorio."
            !validador.imeiValido(equipo.imei) -> "El IMEI debe tener exactamente 15 dígitos."
            else -> null
        }
        if (error != null) {
            onResult(false, error)
            return
        }
        viewModelScope.launch {
            val existente = repository.equipoPorImei(equipo.imei)
            if (existente != null && existente.id != equipo.id) {
                onResult(false, "Ya existe un equipo registrado con ese IMEI.")
                return@launch
            }
            runCatching { repository.guardarEquipo(equipo) }
                .onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.message ?: "No se pudo guardar el equipo.") }
        }
    }

    fun eliminar(equipoId: Long, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val equipo = repository.equipoPorId(equipoId)
            if (equipo == null) {
                onResult(false, "El equipo ya no existe.")
                return@launch
            }
            runCatching { repository.eliminarEquipo(equipo) }
                .onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.message ?: "No se pudo eliminar el equipo.") }
        }
    }
}
