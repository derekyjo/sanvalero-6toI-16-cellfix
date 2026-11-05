package com.sanvalero.cellfix.ui.screens.reportes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanvalero.cellfix.ui.components.BarraSuperior
import com.sanvalero.cellfix.ui.components.SelectorFecha
import com.sanvalero.cellfix.ui.theme.AmbarAviso
import com.sanvalero.cellfix.ui.theme.AzulCellFix
import com.sanvalero.cellfix.ui.theme.RojoError
import com.sanvalero.cellfix.ui.theme.VerdeExito
import java.util.Calendar

@Composable
fun ReportesScreen(
    onBack: () -> Unit,
    viewModel: ReportesViewModel = hiltViewModel()
) {
    val ahora = remember { System.currentTimeMillis() }
    var desde by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                timeInMillis = ahora
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        )
    }
    var hasta by remember { mutableStateOf(ahora) }

    var mensaje by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        val texto = mensaje ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(texto)
        mensaje = null
    }

    val selectorImportar = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        viewModel.importarBackup(uri) { resultado ->
            mensaje = if (resultado.isSuccess) {
                "Respaldo importado. Cierra y vuelve a abrir la app para cargar los datos."
            } else {
                resultado.exceptionOrNull()?.message ?: "No se pudo importar el respaldo."
            }
        }
    }

    Scaffold(
        topBar = { BarraSuperior("Reportes y respaldo", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Reportes obligatorios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Los archivos se guardan en Descargas/CellFix y se comparten con las apps del teléfono.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TarjetaReporte(
                titulo = "Listado general de clientes",
                descripcion = "Reporte PDF con todos los clientes, cédulas y contactos.",
                icono = Icons.Default.Description,
                color = AzulCellFix,
                textoBoton = "Generar PDF"
            ) {
                viewModel.reporteClientes { resultado ->
                    resultado.onSuccess { uri ->
                        viewModel.compartir(uri, "application/pdf")
                        mensaje = "Listado de clientes generado."
                    }.onFailure {
                        mensaje = it.message ?: "No se pudo generar el listado."
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                "Reporte por período",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectorFecha(
                    etiqueta = "Desde",
                    valor = desde,
                    onCambio = { desde = it },
                    modifier = Modifier.weight(1f)
                )
                SelectorFecha(
                    etiqueta = "Hasta",
                    valor = hasta,
                    onCambio = { hasta = it },
                    modifier = Modifier.weight(1f)
                )
            }

            TarjetaReporte(
                titulo = "Reparaciones del período",
                descripcion = "PDF con las reparaciones registradas entre las dos fechas.",
                icono = Icons.Default.PictureAsPdf,
                color = RojoError,
                textoBoton = "Generar PDF"
            ) {
                viewModel.reportePeriodo(desde, finDelDia(hasta)) { resultado ->
                    resultado.onSuccess { uri ->
                        viewModel.compartir(uri, "application/pdf")
                        mensaje = "Reporte del período generado en PDF."
                    }.onFailure {
                        mensaje = it.message ?: "No se pudo generar el reporte."
                    }
                }
            }

            TarjetaReporte(
                titulo = "Reparaciones del período (CSV)",
                descripcion = "Archivo CSV para abrir en Excel o Google Sheets.",
                icono = Icons.Default.TableChart,
                color = VerdeExito,
                textoBoton = "Generar CSV"
            ) {
                viewModel.reportePeriodoCsv(desde, finDelDia(hasta)) { resultado ->
                    resultado.onSuccess { uri ->
                        viewModel.compartir(uri, "text/csv")
                        mensaje = "Reporte del período generado en CSV."
                    }.onFailure {
                        mensaje = it.message ?: "No se pudo generar el CSV."
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                "Resumen gerencial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TarjetaReporte(
                titulo = "Resumen gerencial",
                descripcion = "PDF con indicadores, ingresos, garantías y estados del taller.",
                icono = Icons.Default.Description,
                color = AmbarAviso,
                textoBoton = "Generar resumen"
            ) {
                viewModel.resumenGerencial { resultado ->
                    resultado.onSuccess { uri ->
                        viewModel.compartir(uri, "application/pdf")
                        mensaje = "Resumen gerencial generado."
                    }.onFailure {
                        mensaje = it.message ?: "No se pudo generar el resumen."
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                "Respaldo de la base de datos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Exporta el archivo SQLite para guardarlo o moverlo a otro teléfono. Para restaurar, selecciona un respaldo .db.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.exportarBackup { resultado ->
                                    resultado.onSuccess { uri ->
                                        viewModel.compartirBackup(uri)
                                        mensaje = "Respaldo exportado."
                                    }.onFailure {
                                        mensaje = it.message ?: "No se pudo exportar el respaldo."
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Exportar")
                        }
                        OutlinedButton(
                            onClick = {
                                selectorImportar.launch(
                                    arrayOf(
                                        "application/octet-stream",
                                        "application/x-sqlite3",
                                        "*/*"
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Importar")
                        }
                    }
                }
            }
        }
    }
}

private fun finDelDia(ms: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = ms
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}

@Composable
private fun TarjetaReporte(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    color: Color,
    textoBoton: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            Button(onClick = onClick, shape = RoundedCornerShape(12.dp)) {
                Icon(icono, contentDescription = null, tint = color)
                Spacer(Modifier.width(8.dp))
                Text(textoBoton)
            }
        }
    }
}
