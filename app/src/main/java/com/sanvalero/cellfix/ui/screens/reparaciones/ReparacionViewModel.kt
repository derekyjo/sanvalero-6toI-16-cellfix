package com.sanvalero.cellfix.ui.screens.reparaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanvalero.cellfix.domain.model.EquipoDetalle
import com.sanvalero.cellfix.domain.model.EstadoReparacion
import com.sanvalero.cellfix.domain.model.Reparacion
import com.sanvalero.cellfix.domain.model.ReparacionDetalle
import com.sanvalero.cellfix.domain.model.Tecnico
import com.sanvalero.cellfix.domain.repository.CellFixRepository
import com.sanvalero.cellfix.domain.usecase.CalculadoraUseCase
import com.sanvalero.cellfix.domain.usecase.GarantiaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReparacionViewModel @Inject constructor(
    private val repository: CellFixRepository,
    private val calculadora: CalculadoraUseCase,
    private val garantiaUseCase: GarantiaUseCase
) : ViewModel() {

    data class UiState(
        val reparaciones: List<ReparacionDetalle> = emptyList(),
        val equipos: List<EquipoDetalle> = emptyList(),
        val tecnicos: List<Tecnico> = emptyList(),
        val busqueda: String = ""
    )

    private val _busqueda = MutableStateFlow("")

    val state: StateFlow<UiState> = combine(
        repository.observarReparaciones(),
        repository.observarEquipos(),
        repository.observarTecnicos(),
        _busqueda
    ) { reparaciones, equipos, tecnicos, consulta ->
        val filtradas = if (consulta.isBlank()) {
            reparaciones
        } else {
            reparaciones.filter {
                it.falla.contains(consulta, ignoreCase = true) ||
                    it.imei.contains(consulta, ignoreCase = true) ||
                    it.clienteNombre.contains(consulta, ignoreCase = true) ||
                    it.tecnicoNombre.contains(consulta, ignoreCase = true) ||
                    EstadoReparacion.etiqueta(it.estado).contains(consulta, ignoreCase = true)
            }
        }
        UiState(filtradas, equipos, tecnicos, consulta)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun buscar(consulta: String) {
        _busqueda.value = consulta
    }

    fun cargarReparacion(id: Long, onLoaded: (Reparacion?) -> Unit) {
        viewModelScope.launch {
            onLoaded(repository.reparacionPorId(id))
        }
    }

    fun guardar(reparacion: Reparacion, onResult: (Boolean, String?) -> Unit) {
        val error = when {
            reparacion.equipoId <= 0L -> "Selecciona el equipo a reparar."
            reparacion.tecnicoId <= 0L -> "Selecciona el técnico responsable."
            reparacion.falla.isBlank() -> "Describe la falla reportada por el cliente."
            reparacion.costoManoObra < 0 || reparacion.costoRepuestos < 0 ->
                "Los costos no pueden ser negativos."
            else -> null
        }
        if (error != null) {
            onResult(false, error)
            return
        }
        val totales = calculadora.calcularReparacion(
            reparacion.costoManoObra,
            reparacion.costoRepuestos
        )
        val definitiva = reparacion.copy(itbis = totales.itbis, total = totales.total)
        viewModelScope.launch {
            runCatching { repository.guardarReparacion(definitiva) }
                .onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.message ?: "No se pudo guardar la reparación.") }
        }
    }

    fun cambiarEstado(reparacionId: Long, nuevoEstado: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val reparacion = repository.reparacionPorId(reparacionId)
            if (reparacion == null) {
                onResult(false, "La reparación ya no existe.")
                return@launch
            }
            val fechaEntrega = if (nuevoEstado == EstadoReparacion.ENTREGADO) {
                System.currentTimeMillis()
            } else {
                reparacion.fechaEntrega
            }
            runCatching {
                repository.guardarReparacion(
                    reparacion.copy(estado = nuevoEstado, fechaEntrega = fechaEntrega)
                )
            }.onSuccess {
                if (nuevoEstado == EstadoReparacion.ENTREGADO) {
                    val resultado = try {
                        garantiaUseCase.crearParaReparacion(
                            reparacionId = reparacionId,
                            dias = 30,
                            observaciones = "Garantía de 30 días generada al entregar el equipo."
                        )
                    } catch (error: Exception) {
                        Result.failure(error)
                    }
                    val mensaje = if (resultado.isSuccess) {
                        "Equipo entregado. Garantía de 30 días registrada."
                    } else {
                        "Equipo entregado. ${resultado.exceptionOrNull()?.message ?: ""}"
                    }
                    onResult(true, mensaje)
                } else {
                    onResult(true, null)
                }
            }.onFailure {
                onResult(false, it.message ?: "No se pudo cambiar el estado.")
            }
        }
    }

    fun eliminar(reparacionId: Long, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val reparacion = repository.reparacionPorId(reparacionId)
            if (reparacion == null) {
                onResult(false, "La reparación ya no existe.")
                return@launch
            }
            runCatching { repository.eliminarReparacion(reparacion) }
                .onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.message ?: "No se pudo eliminar la reparación.") }
        }
    }
}
