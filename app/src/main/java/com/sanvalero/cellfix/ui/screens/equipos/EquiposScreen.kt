package com.sanvalero.cellfix.ui.screens.equipos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.sanvalero.cellfix.domain.model.Equipo
import com.sanvalero.cellfix.domain.model.EquipoDetalle
import com.sanvalero.cellfix.ui.components.BarraSuperior
import com.sanvalero.cellfix.ui.components.CampoBusqueda
import com.sanvalero.cellfix.ui.components.DialogoConfirmar
import com.sanvalero.cellfix.ui.components.EstadoVacio
import com.sanvalero.cellfix.ui.components.SelectorOpciones

@Composable
fun EquiposScreen(
    onBack: () -> Unit,
    viewModel: EquipoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var enEdicion by remember { mutableStateOf<Equipo?>(null) }
    var porEliminar by remember { mutableStateOf<EquipoDetalle?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        val texto = mensaje ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(texto)
        mensaje = null
    }

    Scaffold(
        topBar = { BarraSuperior("Equipos", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                enEdicion = null
                mostrarFormulario = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo equipo")
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
                placeholder = "Buscar por marca, modelo, IMEI o cliente"
            )
            Spacer(Modifier.height(12.dp))

            if (state.equipos.isEmpty()) {
                EstadoVacio("No hay equipos registrados.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.equipos, key = { it.id }) { equipo ->
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
                                        "${equipo.marca} ${equipo.modelo}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "IMEI: ${equipo.imei}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Dueño: ${equipo.clienteNombre}  ·  ${equipo.color}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = {
                                    enEdicion = Equipo(
                                        id = equipo.id,
                                        clienteId = equipo.clienteId,
                                        marca = equipo.marca,
                                        modelo = equipo.modelo,
                                        imei = equipo.imei,
                                        color = equipo.color
                                    )
                                    mostrarFormulario = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { porEliminar = equipo }) {
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
        FormularioEquipo(
            equipo = enEdicion,
            clientes = state.clientes,
            onDismiss = { mostrarFormulario = false },
            onGuardar = { equipo ->
                viewModel.guardar(equipo) { exito, error ->
                    if (exito) {
                        mostrarFormulario = false
                        mensaje = "Equipo guardado correctamente."
                    } else {
                        mensaje = error
                    }
                }
            }
        )
    }

    porEliminar?.let { equipo ->
        DialogoConfirmar(
            titulo = "Eliminar equipo",
            mensaje = "¿Eliminar el equipo ${equipo.marca} ${equipo.modelo} (IMEI ${equipo.imei})?",
            onConfirmar = {
                viewModel.eliminar(equipo.id) { exito, error ->
                    mensaje = if (exito) "Equipo eliminado." else error
                }
                porEliminar = null
            },
            onCancelar = { porEliminar = null }
        )
    }
}

@Composable
private fun FormularioEquipo(
    equipo: Equipo?,
    clientes: List<Cliente>,
    onDismiss: () -> Unit,
    onGuardar: (Equipo) -> Unit
) {
    var clienteSeleccionado by remember {
        mutableStateOf(clientes.firstOrNull { it.id == equipo?.clienteId })
    }
    var marca by remember { mutableStateOf(equipo?.marca ?: "") }
    var modelo by remember { mutableStateOf(equipo?.modelo ?: "") }
    var imei by remember { mutableStateOf(equipo?.imei ?: "") }
    var color by remember { mutableStateOf(equipo?.color ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (equipo == null) "Nuevo equipo" else "Editar equipo") },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Cliente dueño", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                SelectorOpciones(
                    etiqueta = "Seleccionar cliente",
                    opciones = clientes,
                    seleccionada = clienteSeleccionado,
                    texto = { "${it.nombre} (${it.cedula})" },
                    onSelect = { clienteSeleccionado = it }
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = marca,
                    onValueChange = { marca = it },
                    label = { Text("Marca") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = modelo,
                    onValueChange = { modelo = it },
                    label = { Text("Modelo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = imei,
                    onValueChange = { imei = it },
                    label = { Text("IMEI (15 dígitos)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onGuardar(
                    Equipo(
                        id = equipo?.id ?: 0,
                        clienteId = clienteSeleccionado?.id ?: 0,
                        marca = marca.trim(),
                        modelo = modelo.trim(),
                        imei = imei.trim(),
                        color = color.trim()
                    )
                )
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
