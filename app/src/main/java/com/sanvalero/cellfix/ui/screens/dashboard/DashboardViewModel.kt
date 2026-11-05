package com.sanvalero.cellfix.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanvalero.cellfix.domain.model.EstadoReparacion
import com.sanvalero.cellfix.domain.repository.CellFixRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    repository: CellFixRepository
) : ViewModel() {

    data class UiState(
        val clientes: Int = 0,
        val equipos: Int = 0,
        val reparaciones: Int = 0,
        val garantiasActivas: Int = 0,
        val cantidadVentas: Int = 0,
        val ingresosReparaciones: Double = 0.0,
        val ingresosVentas: Double = 0.0,
        val porEstado: Map<String, Int> = emptyMap()
    )

    private data class Conteos(val clientes: Int, val equipos: Int, val reparaciones: Int)
    private data class Dinero(
        val garantias: Int,
        val ventas: Int,
        val reparaciones: Double,
        val ventasDinero: Double
    )

    private val conteos = combine(
        repository.contarClientes(),
        repository.contarEquipos(),
        repository.contarReparaciones()
    ) { clientes, equipos, reparaciones -> Conteos(clientes, equipos, reparaciones) }

    private val dinero = combine(
        repository.contarGarantiasActivas(),
        repository.contarVentas(),
        repository.totalReparacionesEntregadas(),
        repository.totalVentas()
    ) { garantias, ventas, reparaciones, ventasDinero ->
        Dinero(garantias, ventas, reparaciones, ventasDinero)
    }

    private val estados = combine(
        EstadoReparacion.opciones.map { repository.contarReparacionesPorEstado(it) }
    ) { valores ->
        EstadoReparacion.opciones.zip(valores.toList()).toMap()
    }

    val state: StateFlow<UiState> = combine(conteos, dinero, estados) { c, d, e ->
        UiState(
            clientes = c.clientes,
            equipos = c.equipos,
            reparaciones = c.reparaciones,
            garantiasActivas = d.garantias,
            cantidadVentas = d.ventas,
            ingresosReparaciones = d.reparaciones,
            ingresosVentas = d.ventasDinero,
            porEstado = e
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())
}
