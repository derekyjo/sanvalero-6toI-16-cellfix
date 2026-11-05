package com.sanvalero.cellfix.ui.screens.garantias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanvalero.cellfix.domain.model.GarantiaDetalle
import com.sanvalero.cellfix.domain.repository.CellFixRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GarantiaViewModel @Inject constructor(
    private val repository: CellFixRepository
) : ViewModel() {

    data class UiState(
        val garantias: List<GarantiaDetalle> = emptyList(),
        val busqueda: String = ""
    )

    private val _busqueda = MutableStateFlow("")

    val state: StateFlow<UiState> = combine(
        repository.observarGarantias(),
        _busqueda
    ) { lista, consulta ->
        val filtradas = if (consulta.isBlank()) {
            lista
        } else {
            lista.filter {
                it.imei.contains(consulta, ignoreCase = true) ||
                    it.clienteNombre.contains(consulta, ignoreCase = true) ||
                    it.marca.contains(consulta, ignoreCase = true) ||
                    it.modelo.contains(consulta, ignoreCase = true)
            }
        }
        UiState(filtradas, consulta)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun buscar(consulta: String) {
        _busqueda.value = consulta
    }

    fun eliminar(garantiaId: Long, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val garantia = repository.garantiasSnapshot().firstOrNull { it.id == garantiaId }
            if (garantia == null) {
                onResult(false, "La garantía ya no existe.")
                return@launch
            }
            runCatching { repository.eliminarGarantia(garantia) }
                .onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.message ?: "No se pudo eliminar la garantía.") }
        }
    }
}
