package com.sanvalero.cellfix.ui.screens.tecnicos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanvalero.cellfix.domain.model.Tecnico
import com.sanvalero.cellfix.domain.repository.CellFixRepository
import com.sanvalero.cellfix.domain.usecase.ValidadorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TecnicoViewModel @Inject constructor(
    private val repository: CellFixRepository,
    private val validador: ValidadorUseCase
) : ViewModel() {

    val state: StateFlow<List<Tecnico>> = repository.observarTecnicos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun guardar(tecnico: Tecnico, onResult: (Boolean, String?) -> Unit) {
        val error = when {
            !validador.requerido(tecnico.nombre) -> "El nombre es obligatorio."
            !validador.requerido(tecnico.especialidad) -> "La especialidad es obligatoria."
            !validador.telefonoValido(tecnico.telefono) -> "El teléfono debe tener al menos 10 dígitos."
            else -> null
        }
        if (error != null) {
            onResult(false, error)
            return
        }
        viewModelScope.launch {
            runCatching { repository.guardarTecnico(tecnico) }
                .onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.message ?: "No se pudo guardar el técnico.") }
        }
    }

    fun eliminar(tecnico: Tecnico, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            runCatching { repository.eliminarTecnico(tecnico) }
                .onSuccess { onResult(true, null) }
                .onFailure {
                    onResult(
                        false,
                        "No se pudo eliminar. Probablemente el técnico tiene reparaciones asociadas."
                    )
                }
        }
    }
}
