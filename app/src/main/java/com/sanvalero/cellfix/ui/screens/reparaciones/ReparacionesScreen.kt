package com.sanvalero.cellfix.ui.screens.reparaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanvalero.cellfix.domain.model.EquipoDetalle
import com.sanvalero.cellfix.domain.model.EstadoReparacion
import com.sanvalero.cellfix.domain.model.Reparacion
import com.sanvalero.cellfix.domain.model.ReparacionDetalle
import com.sanvalero.cellfix.domain.model.Tecnico
import com.sanvalero.cellfix.ui.components.BarraSuperior
import com.sanvalero.cellfix.ui.components.CampoBusqueda
import com.sanvalero.cellfix.ui.components.ChipEstado
import com.sanvalero.cellfix.ui.components.DialogoConfirmar
import com.sanvalero.cellfix.ui.components.EstadoVacio
import com.sanvalero.cellfix.ui.components.SelectorOpciones
import com.sanvalero.cellfix.util.Formato

@Composable
fun ReparacionesScreen(
    onBack: () -> Unit,
    viewModel: ReparacionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var enEdicion by remember { mutableStateOf<Reparacion?>(null) }
    var porEliminar by remember { mutableStateOf<ReparacionDetalle?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        val texto = mensaje ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(texto)
        mensaje = null
    }

    Scaffold(
        topBar = { BarraSuperior("Reparaciones", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                enEdicion = null
                mostrarFormulario = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva reparación")
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
                placeholder = "Buscar por falla, IMEI, cliente o técnico"
            )
            Spacer(Modifier.height(12.dp))

            if (state.reparaciones.isEmpty()) {
                EstadoVacio("No hay reparaciones registradas. Usa el botón + para crear la primera.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.reparaciones, key = { it.id }) { reparacion ->
                        TarjetaReparacion(
                            reparacion = reparacion,
                            onEditar = {
                                viewModel.cargarReparacion(reparacion.id) { completa ->
                                    if (completa == null) {
                                        mensaje = "La reparación ya no existe."
                                    } else {
                                        enEdicion = completa
                                        mostrarFormulario = true
                                    }
                                }
                            },
                            onEliminar = { porEliminar = reparacion },
                            onCambiarEstado = { nuevoEstado ->
                                viewModel.cambiarEstado(reparacion.id, nuevoEstado) { exito, info ->
                                    if (exito) {
                                        mensaje = info ?: "Estado actualizado a ${EstadoReparacion.etiqueta(nuevoEstado)}."
                                    } else {
                                        mensaje = info
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        FormularioReparacion(
            reparacion = enEdicion,
            equipos = state.equipos,
            tecnicos = state.tecnicos,
            onDismiss = { mostrarFormulario = false },
            onGuardar = { reparacion ->
                viewModel.guardar(reparacion) { exito, error ->
                    if (exito) {
                        mostrarFormulario = false
                        mensaje = "Reparación guardada correctamente."
                    } else {
                        mensaje = error
                    }
                }
            }
        )
    }

    porEliminar?.let { reparacion ->
        DialogoConfirmar(
            titulo = "Eliminar reparación",
            mensaje = "¿Eliminar la reparación del equipo ${reparacion.marca} ${reparacion.modelo} (IMEI ${reparacion.imei})?",
            onConfirmar = {
                viewModel.eliminar(reparacion.id) { exito, error ->
                    mensaje = if (exito) "Reparación eliminada." else error
                }
                porEliminar = null
            },
            onCancelar = { porEliminar = null }
        )
    }
}

@Composable
private fun TarjetaReparacion(
    reparacion: ReparacionDetalle,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onCambiarEstado: (String) -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 2.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${reparacion.marca} ${reparacion.modelo}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    ChipEstado(reparacion.estado)
                }
                Text(
                    "IMEI: ${reparacion.imei}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Cliente: ${reparacion.clienteNombre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Técnico: ${reparacion.tecnicoNombre}  ·  ${Formato.fecha(reparacion.fechaIngreso)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    reparacion.falla,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (reparacion.diagnostico.isNotBlank()) {
                    Text(
                        "Diagnóstico: ${reparacion.diagnostico}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Total: ${Formato.moneda(reparacion.total)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar reparación")
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar reparación")
                }
                Box {
                    IconButton(onClick = { menuAbierto = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Cambiar estado")
                    }
                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false }
                    ) {
                        EstadoReparacion.opciones
                            .filter { it != reparacion.estado }
                            .forEach { estado ->
                                DropdownMenuItem(
                                    text = { Text(EstadoReparacion.etiqueta(estado)) },
                                    onClick = {
                                        menuAbierto = false
                                        onCambiarEstado(estado)
                                    }
                                )
                            }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormularioReparacion(
    reparacion: Reparacion?,
    equipos: List<EquipoDetalle>,
    tecnicos: List<Tecnico>,
    onDismiss: () -> Unit,
    onGuardar: (Reparacion) -> Unit
) {
    var equipoSeleccionado by remember {
        mutableStateOf(equipos.firstOrNull { it.id == reparacion?.equipoId })
    }
    var tecnicoSeleccionado by remember {
        mutableStateOf(tecnicos.firstOrNull { it.id == reparacion?.tecnicoId })
    }
    var falla by remember { mutableStateOf(reparacion?.falla ?: "") }
    var diagnostico by remember { mutableStateOf(reparacion?.diagnostico ?: "") }
    var manoObra by remember {
        mutableStateOf(if (reparacion == null || reparacion.costoManoObra == 0.0) "" else reparacion.costoManoObra.toString())
    }
    var repuestos by remember {
        mutableStateOf(if (reparacion == null || reparacion.costoRepuestos == 0.0) "" else reparacion.costoRepuestos.toString())
    }

    val valorMano = manoObra.toDoubleOrNull() ?: 0.0
    val valorRepuestos = repuestos.toDoubleOrNull() ?: 0.0
    val subtotal = valorMano + valorRepuestos
    val itbis = subtotal * 0.18
    val total = subtotal + itbis

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (reparacion == null) "Nueva reparación" else "Editar reparación") },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Equipo", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                SelectorOpciones(
                    etiqueta = "Seleccionar equipo",
                    opciones = equipos,
                    seleccionada = equipoSeleccionado,
                    texto = { "${it.marca} ${it.modelo} · ${it.imei}" },
                    onSelect = { equipoSeleccionado = it }
                )
                Spacer(Modifier.height(10.dp))

                Text("Técnico", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                SelectorOpciones(
                    etiqueta = "Seleccionar técnico",
                    opciones = tecnicos,
                    seleccionada = tecnicoSeleccionado,
                    texto = { "${it.nombre} · ${it.especialidad}" },
                    onSelect = { tecnicoSeleccionado = it }
                )
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = falla,
                    onValueChange = { falla = it },
                    label = { Text("Falla reportada") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = diagnostico,
                    onValueChange = { diagnostico = it },
                    label = { Text("Diagnóstico (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = manoObra,
                        onValueChange = { manoObra = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Mano de obra") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = repuestos,
                        onValueChange = { repuestos = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Repuestos") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Subtotal: ${Formato.moneda(subtotal)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "ITBIS (18 %): ${Formato.moneda(itbis)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Total: ${Formato.moneda(total)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                if (reparacion != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Estado actual: ${EstadoReparacion.etiqueta(reparacion.estado)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val base = reparacion ?: Reparacion()
                onGuardar(
                    base.copy(
                        equipoId = equipoSeleccionado?.id ?: 0L,
                        tecnicoId = tecnicoSeleccionado?.id ?: 0L,
                        falla = falla.trim(),
                        diagnostico = diagnostico.trim(),
                        costoManoObra = valorMano,
                        costoRepuestos = valorRepuestos
                    )
                )
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
