package com.sanvalero.cellfix.ui.screens.clientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanvalero.cellfix.domain.model.Cliente
import com.sanvalero.cellfix.ui.components.BarraSuperior
import com.sanvalero.cellfix.ui.components.CampoBusqueda
import com.sanvalero.cellfix.ui.components.DialogoConfirmar
import com.sanvalero.cellfix.ui.components.EstadoVacio

@Composable
fun ClientesScreen(
    onBack: () -> Unit,
    viewModel: ClienteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var enEdicion by remember { mutableStateOf<Cliente?>(null) }
    var porEliminar by remember { mutableStateOf<Cliente?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        val texto = mensaje ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(texto)
        mensaje = null
    }

    Scaffold(
        topBar = { BarraSuperior("Clientes", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                enEdicion = null
                mostrarFormulario = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo cliente")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            CampoBusqueda(
                valor = state.busqueda,
                onValorChange = viewModel::buscar,
                placeholder = "Buscar por nombre, cédula o teléfono"
            )
            Spacer(Modifier.height(12.dp))

            if (state.clientes.isEmpty()) {
                EstadoVacio("No hay clientes registrados. Usa el botón + para crear el primero.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.clientes, key = { it.id }) { cliente ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        cliente.nombre,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "Cédula: ${cliente.cedula}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "${cliente.telefono}  ·  ${cliente.email.ifBlank { "sin correo" }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = {
                                    enEdicion = cliente
                                    mostrarFormulario = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { porEliminar = cliente }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        FormularioCliente(
            cliente = enEdicion,
            onDismiss = { mostrarFormulario = false },
            onGuardar = { cliente ->
                viewModel.guardar(cliente) { exito, error ->
                    if (exito) {
                        mostrarFormulario = false
                        mensaje = "Cliente guardado correctamente."
                    } else {
                        mensaje = error
                    }
                }
            }
        )
    }

    porEliminar?.let { cliente ->
        DialogoConfirmar(
            titulo = "Eliminar cliente",
            mensaje = "¿Seguro que deseas eliminar a ${cliente.nombre}? También se eliminarán sus equipos.",
            onConfirmar = {
                viewModel.eliminar(cliente) { exito, error ->
                    mensaje = if (exito) "Cliente eliminado." else error
                }
                porEliminar = null
            },
            onCancelar = { porEliminar = null }
        )
    }
}

@Composable
private fun FormularioCliente(
    cliente: Cliente?,
    onDismiss: () -> Unit,
    onGuardar: (Cliente) -> Unit
) {
    var nombre by remember { mutableStateOf(cliente?.nombre ?: "") }
    var cedula by remember { mutableStateOf(cliente?.cedula ?: "") }
    var telefono by remember { mutableStateOf(cliente?.telefono ?: "") }
    var email by remember { mutableStateOf(cliente?.email ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (cliente == null) "Nuevo cliente" else "Editar cliente") },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = cedula,
                    onValueChange = { cedula = it },
                    label = { Text("Cédula (11 dígitos)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onGuardar(
                    Cliente(
                        id = cliente?.id ?: 0,
                        nombre = nombre.trim(),
                        cedula = cedula.trim(),
                        telefono = telefono.trim(),
                        email = email.trim()
                    )
                )
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
