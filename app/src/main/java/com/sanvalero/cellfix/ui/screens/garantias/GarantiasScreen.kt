package com.sanvalero.cellfix.ui.screens.garantias

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanvalero.cellfix.domain.model.GarantiaDetalle
import com.sanvalero.cellfix.ui.components.BarraSuperior
import com.sanvalero.cellfix.ui.components.CampoBusqueda
import com.sanvalero.cellfix.ui.components.DialogoConfirmar
import com.sanvalero.cellfix.ui.components.EstadoVacio
import com.sanvalero.cellfix.ui.theme.RojoError
import com.sanvalero.cellfix.ui.theme.VerdeExito
import com.sanvalero.cellfix.util.Formato

@Composable
fun GarantiasScreen(
    onBack: () -> Unit,
    viewModel: GarantiaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var porEliminar by remember { mutableStateOf<GarantiaDetalle?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        val texto = mensaje ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(texto)
        mensaje = null
    }

    Scaffold(
        topBar = { BarraSuperior("Garantías por IMEI", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                placeholder = "Buscar por IMEI, cliente o equipo"
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "La garantía se controla por el IMEI del equipo. Cada entrega genera 30 días de cobertura.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            if (state.garantias.isEmpty()) {
                EstadoVacio("No hay garantías registradas. Se crean al entregar una reparación.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.garantias, key = { it.id }) { garantia ->
                        TarjetaGarantia(
                            garantia = garantia,
                            onEliminar = { porEliminar = garantia }
                        )
                    }
                }
            }
        }
    }

    porEliminar?.let { garantia ->
        DialogoConfirmar(
            titulo = "Eliminar garantía",
            mensaje = "¿Eliminar la garantía del IMEI ${garantia.imei}? Esta acción no se puede deshacer.",
            onConfirmar = {
                viewModel.eliminar(garantia.id) { exito, error ->
                    mensaje = if (exito) "Garantía eliminada." else error
                }
                porEliminar = null
            },
            onCancelar = { porEliminar = null }
        )
    }
}

@Composable
private fun TarjetaGarantia(garantia: GarantiaDetalle, onEliminar: () -> Unit) {
    val vigente = garantia.fechaFin >= System.currentTimeMillis()
    val color = if (vigente) VerdeExito else RojoError

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = color)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "${garantia.marca} ${garantia.modelo}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "IMEI: ${garantia.imei}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Cliente: ${garantia.clienteNombre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${Formato.fecha(garantia.fechaInicio)}  →  ${Formato.fecha(garantia.fechaFin)}  ·  ${garantia.dias} días",
                    style = MaterialTheme.typography.labelMedium,
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    if (vigente) "VIGENTE" else "VENCIDA",
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
                if (garantia.observaciones.isNotBlank()) {
                    Text(
                        garantia.observaciones,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar garantía", tint = Color.Gray)
            }
        }
    }
}
