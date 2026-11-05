package com.sanvalero.cellfix.ui.screens.ventas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanvalero.cellfix.domain.model.Cliente
import com.sanvalero.cellfix.domain.model.DetalleVenta
import com.sanvalero.cellfix.domain.model.Venta
import com.sanvalero.cellfix.domain.model.VentaResumen
import com.sanvalero.cellfix.domain.repository.CellFixRepository
import com.sanvalero.cellfix.domain.usecase.CalculadoraUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VentaViewModel @Inject constructor(
    private val repository: CellFixRepository,
    private val calculadora: CalculadoraUseCase
) : ViewModel() {

    data class UiState(
        val ventas: List<VentaResumen> = emptyList(),
        val clientes: List<Cliente> = emptyList()
    )

    val state: StateFlow<UiState> = combine(
        repository.observarVentas(),
        repository.observarClientes()
    ) { ventas, clientes -> UiState(ventas, clientes) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun crearVenta(
        clienteId: Long,
        metodoPago: String,
        items: List<DetalleVenta>,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (clienteId <= 0L) {
            onResult(false, "Selecciona el cliente de la venta.")
            return
        }
        val detalle = items
            .filter { it.descripcion.isNotBlank() && it.cantidad > 0 && it.precioUnitario > 0 }
            .map {
                it.copy(subtotal = calculadora.redondear(it.cantidad * it.precioUnitario))
            }
        if (detalle.isEmpty()) {
            onResult(false, "Agrega al menos un artículo con cantidad y precio válidos.")
            return
        }

        val subtotal = calculadora.redondear(detalle.sumOf { it.subtotal })
        val totales = calculadora.calcularVenta(subtotal)
        val venta = Venta(
            clienteId = clienteId,
            subtotal = totales.subtotal,
            itbis = totales.itbis,
            total = totales.total,
            metodoPago = metodoPago
        )

        viewModelScope.launch {
            runCatching { repository.crearVenta(venta, detalle) }
                .onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.message ?: "No se pudo registrar la venta.") }
        }
    }

    fun eliminar(ventaId: Long, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val venta = repository.ventasSnapshot().firstOrNull { it.id == ventaId }
            if (venta == null) {
                onResult(false, "La venta ya no existe.")
                return@launch
            }
            runCatching { repository.eliminarVenta(venta) }
                .onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.message ?: "No se pudo eliminar la venta.") }
        }
    }
}
