package com.sanvalero.cellfix.ui.screens.tecnicos

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
import androidx.compose.material3.Switch
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
import com.sanvalero.cellfix.domain.model.Tecnico
import com.sanvalero.cellfix.ui.components.BarraSuperior
import com.sanvalero.cellfix.ui.components.DialogoConfirmar
import com.sanvalero.cellfix.ui.components.EstadoVacio
import com.sanvalero.cellfix.ui.theme.VerdeExito

@Composable
fun TecnicosScreen(
    onBack: () -> Unit,
    viewModel: TecnicoViewModel = hiltViewModel()
) {
    val tecnicos by viewModel.state.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var enEdicion by remember { mutableStateOf<Tecnico?>(null) }
    var porEliminar by remember { mutableStateOf<Tecnico?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        val texto = mensaje ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(texto)
        mensaje = null
    }

    Scaffold(
        topBar = { BarraSuperior("Técnicos", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                enEdicion = null
                mostrarFormulario = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo técnico")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (tecnicos.isEmpty()) {
                EstadoVacio("No hay técnicos registrados.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tecnicos, key = { it.id }) { tecnico ->
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
                                        tecnico.nombre,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        tecnico.especialidad,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "${tecnico.telefono}  ·  ${if (tecnico.activo) "Activo" else "Inactivo"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (tecnico.activo) VerdeExito else MaterialTheme.colorScheme.error
                                    )
                                }
                                IconButton(onClick = {
                                    enEdicion = tecnico
                                    mostrarFormulario = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { porEliminar = tecnico }) {
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
        FormularioTecnico(
            tecnico = enEdicion,
            onDismiss = { mostrarFormulario = false },
            onGuardar = { tecnico ->
                viewModel.guardar(tecnico) { exito, error ->
                    if (exito) {
                        mostrarFormulario = false
                        mensaje = "Técnico guardado correctamente."
                    } else {
                        mensaje = error
                    }
                }
            }
        )
    }

    porEliminar?.let { tecnico ->
        DialogoConfirmar(
            titulo = "Eliminar técnico",
            mensaje = "¿Eliminar a ${tecnico.nombre}? No se podrá si tiene reparaciones asociadas.",
            onConfirmar = {
                viewModel.eliminar(tecnico) { exito, error ->
                    mensaje = if (exito) "Técnico eliminado." else error
                }
                porEliminar = null
            },
            onCancelar = { porEliminar = null }
        )
    }
}

@Composable
private fun FormularioTecnico(
    tecnico: Tecnico?,
    onDismiss: () -> Unit,
    onGuardar: (Tecnico) -> Unit
) {
    var nombre by remember { mutableStateOf(tecnico?.nombre ?: "") }
    var especialidad by remember { mutableStateOf(tecnico?.especialidad ?: "") }
    var telefono by remember { mutableStateOf(tecnico?.telefono ?: "") }
    var activo by remember { mutableStateOf(tecnico?.activo ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (tecnico == null) "Nuevo técnico" else "Editar técnico") },
        text = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = especialidad,
                    onValueChange = { especialidad = it },
                    label = { Text("Especialidad") },
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
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = activo, onCheckedChange = { activo = it })
                    Spacer(Modifier.height(0.dp))
                    Text("  Activo para recibir reparaciones")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onGuardar(
                    Tecnico(
                        id = tecnico?.id ?: 0,
                        nombre = nombre.trim(),
                        especialidad = especialidad.trim(),
                        telefono = telefono.trim(),
                        activo = activo
                    )
                )
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
