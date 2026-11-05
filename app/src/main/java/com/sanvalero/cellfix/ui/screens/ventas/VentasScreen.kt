package com.sanvalero.cellfix.ui.screens.ventas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanvalero.cellfix.domain.model.Cliente
import com.sanvalero.cellfix.domain.model.DetalleVenta
import com.sanvalero.cellfix.domain.model.MetodosPago
import com.sanvalero.cellfix.domain.model.VentaResumen
import com.sanvalero.cellfix.ui.components.BarraSuperior
import com.sanvalero.cellfix.ui.components.DialogoConfirmar
import com.sanvalero.cellfix.ui.components.EstadoVacio
import com.sanvalero.cellfix.ui.components.SelectorOpciones
import com.sanvalero.cellfix.ui.theme.VerdeExito
import com.sanvalero.cellfix.util.Formato

@Composable
fun VentasScreen(
    onBack: () -> Unit,
    viewModel: VentaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var porEliminar by remember { mutableStateOf<VentaResumen?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        val texto = mensaje ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(texto)
        mensaje = null
    }

    Scaffold(
        topBar = { BarraSuperior("Ventas", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Default.PointOfSale, contentDescription = "Nueva venta")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Facturación con ITBIS del 18 % y montos en RD$.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            if (state.ventas.isEmpty()) {
                EstadoVacio("No hay ventas registradas. Usa el botón para registrar la primera.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.ventas, key = { it.id }) { venta ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.PointOfSale,
                                    contentDescription = null,
                                    tint = VerdeExito
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        venta.clienteNombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        "${Formato.fechaHora(venta.fecha)}  ·  ${venta.metodoPago}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Subtotal ${Formato.moneda(venta.subtotal)}  ·  ITBIS ${Formato.moneda(venta.itbis)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Total: ${Formato.moneda(venta.total)}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = VerdeExito
                                    )
                                }
                                IconButton(onClick = { porEliminar = venta }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar venta")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        FormularioVenta(
            clientes = state.clientes,
            onDismiss = { mostrarFormulario = false },
            onGuardar = { clienteId, metodo, items ->
                viewModel.crearVenta(clienteId, metodo, items) { exito, error ->
                    if (exito) {
                        mostrarFormulario = false
                        mensaje = "Venta registrada correctamente."
                    } else {
                        mensaje = error
                    }
                }
            }
        )
    }

    porEliminar?.let { venta ->
        DialogoConfirmar(
            titulo = "Eliminar venta",
            mensaje = "¿Eliminar la venta de ${venta.clienteNombre} por ${Formato.moneda(venta.total)}?",
            onConfirmar = {
                viewModel.eliminar(venta.id) { exito, error ->
                    mensaje = if (exito) "Venta eliminada." else error
                }
                porEliminar = null
            },
            onCancelar = { porEliminar = null }
        )
    }
}

private data class ItemForm(
    val descripcion: String = "",
    val cantidad: String = "1",
    val precio: String = ""
)

@Composable
private fun FormularioVenta(
    clientes: List<Cliente>,
    onDismiss: () -> Unit,
    onGuardar: (Long, String, List<DetalleVenta>) -> Unit
) {
    var clienteSeleccionado by remember { mutableStateOf<Cliente?>(null) }
    var metodo by remember { mutableStateOf(MetodosPago.opciones.first()) }
    val items = remember { mutableStateListOf(ItemForm()) }

    val subtotal = items.sumOf { item ->
        val cantidad = item.cantidad.toIntOrNull() ?: 0
        val precio = item.precio.toDoubleOrNull() ?: 0.0
        cantidad * precio
    }
    val itbis = subtotal * 0.18
    val total = subtotal + itbis

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva venta") },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Cliente", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                SelectorOpciones(
                    etiqueta = "Seleccionar cliente",
                    opciones = clientes,
                    seleccionada = clienteSeleccionado,
                    texto = { "${it.nombre} (${it.cedula})" },
                    onSelect = { clienteSeleccionado = it }
                )
                Spacer(Modifier.height(10.dp))
                Text("Método de pago", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                SelectorOpciones(
                    etiqueta = "Seleccionar método",
                    opciones = MetodosPago.opciones,
                    seleccionada = metodo,
                    texto = { it },
                    onSelect = { metodo = it }
                )
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text(
                    "Artículos",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))

                items.forEachIndexed { indice, item ->
                    Column(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Artículo ${indice + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.weight(1f)
                            )
                            if (items.size > 1) {
                                IconButton(onClick = { items.removeAt(indice) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Quitar artículo")
                                }
                            }
                        }
                        OutlinedTextField(
                            value = item.descripcion,
                            onValueChange = { items[indice] = item.copy(descripcion = it) },
                            label = { Text("Descripción") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = item.cantidad,
                                onValueChange = {
                                    items[indice] = item.copy(cantidad = it.filter { c -> c.isDigit() })
                                },
                                label = { Text("Cantidad") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = item.precio,
                                onValueChange = {
                                    items[indice] = item.copy(
                                        precio = it.filter { c -> c.isDigit() || c == '.' }
                                    )
                                },
                                label = { Text("Precio unit.") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }

                OutlinedButton(
                    onClick = { items.add(ItemForm()) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Agregar artículo")
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text("Subtotal: ${Formato.moneda(subtotal)}", style = MaterialTheme.typography.bodySmall)
                Text("ITBIS (18 %): ${Formato.moneda(itbis)}", style = MaterialTheme.typography.bodySmall)
                Text(
                    "Total: ${Formato.moneda(total)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val detalles = items
                    .filter {
                        it.descripcion.isNotBlank() &&
                            (it.cantidad.toIntOrNull() ?: 0) > 0 &&
                            (it.precio.toDoubleOrNull() ?: 0.0) > 0
                    }
                    .map {
                        val cantidad = it.cantidad.toIntOrNull() ?: 0
                        val precio = it.precio.toDoubleOrNull() ?: 0.0
                        DetalleVenta(
                            descripcion = it.descripcion.trim(),
                            cantidad = cantidad,
                            precioUnitario = precio,
                            subtotal = cantidad * precio
                        )
                    }
                onGuardar(clienteSeleccionado?.id ?: 0L, metodo, detalles)
            }) { Text("Registrar venta") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
