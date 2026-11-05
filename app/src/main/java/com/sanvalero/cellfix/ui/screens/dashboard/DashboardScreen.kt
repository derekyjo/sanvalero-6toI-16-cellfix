package com.sanvalero.cellfix.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanvalero.cellfix.domain.model.EstadoReparacion
import com.sanvalero.cellfix.ui.components.BarraSuperior
import com.sanvalero.cellfix.ui.components.TarjetaIndicador
import com.sanvalero.cellfix.ui.theme.AmbarAviso
import com.sanvalero.cellfix.ui.theme.AzulCellFix
import com.sanvalero.cellfix.ui.theme.RojoError
import com.sanvalero.cellfix.ui.theme.VerdeExito
import com.sanvalero.cellfix.util.Formato

private data class Modulo(
    val titulo: String,
    val descripcion: String,
    val ruta: String,
    val icono: ImageVector,
    val color: Color
)

@Composable
fun DashboardScreen(
    esAdmin: Boolean,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val modulos = buildList {
        add(Modulo("Clientes", "Registro y consulta", "clientes", Icons.Default.People, AzulCellFix))
        add(Modulo("Equipos", "IMEI y propietario", "equipos", Icons.Default.PhoneAndroid, AzulCellFix))
        add(Modulo("Reparaciones", "Órdenes del taller", "reparaciones", Icons.Default.Build, AmbarAviso))
        add(Modulo("Garantías", "Control por IMEI", "garantias", Icons.Default.Verified, VerdeExito))
        add(Modulo("Ventas", "Facturación e ITBIS", "ventas", Icons.Default.PointOfSale, VerdeExito))
        if (esAdmin) {
            add(Modulo("Técnicos", "Personal del taller", "tecnicos", Icons.Default.Engineering, AzulCellFix))
            add(Modulo("Reportes", "PDF, CSV y respaldo", "reportes", Icons.Default.Description, RojoError))
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "CellFix · Panel general",
                acciones = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Resumen del negocio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Estado del taller, ventas y garantías",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaIndicador(
                        "Clientes",
                        state.clientes.toString(),
                        Icons.Default.People,
                        AzulCellFix,
                        Modifier.weight(1f)
                    )
                    TarjetaIndicador(
                        "Equipos",
                        state.equipos.toString(),
                        Icons.Default.PhoneAndroid,
                        AzulCellFix,
                        Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaIndicador(
                        "Reparaciones",
                        state.reparaciones.toString(),
                        Icons.Default.Build,
                        AmbarAviso,
                        Modifier.weight(1f)
                    )
                    TarjetaIndicador(
                        "Garantías activas",
                        state.garantiasActivas.toString(),
                        Icons.Default.Verified,
                        VerdeExito,
                        Modifier.weight(1f)
                    )
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Ingresos acumulados",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            Formato.moneda(state.ingresosReparaciones + state.ingresosVentas),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = VerdeExito
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Column {
                                Text("Reparaciones", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    Formato.moneda(state.ingresosReparaciones),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Column {
                                Text("Ventas (${state.cantidadVentas})", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    Formato.moneda(state.ingresosVentas),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Reparaciones por estado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                        GraficoEstados(state.porEstado)
                    }
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Módulos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(modulos.chunked(2)) { fila ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    fila.forEach { modulo ->
                        ModuloCard(modulo, Modifier.weight(1f)) { onNavigate(modulo.ruta) }
                    }
                    if (fila.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuloCard(modulo: Modulo, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(modulo.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(modulo.icono, contentDescription = null, tint = modulo.color)
            }
            Spacer(Modifier.height(10.dp))
            Text(modulo.titulo, fontWeight = FontWeight.SemiBold)
            Text(
                modulo.descripcion,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GraficoEstados(datos: Map<String, Int>) {
    val maximo = (datos.values.maxOrNull() ?: 0).coerceAtLeast(1)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        EstadoReparacion.opciones.forEach { estado ->
            val valor = datos[estado] ?: 0
            val altura = if (valor == 0) 4.dp else (110f * valor / maximo).dp
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(valor.toString(), style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(altura)
                        .background(
                            AzulCellFix,
                            RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                        )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    EstadoReparacion.etiqueta(estado).take(7),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}
