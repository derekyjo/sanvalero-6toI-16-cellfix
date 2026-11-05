package com.sanvalero.cellfix.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sanvalero.cellfix.domain.model.EstadoReparacion
import com.sanvalero.cellfix.ui.theme.AmbarAviso
import com.sanvalero.cellfix.ui.theme.AzulCellFix
import com.sanvalero.cellfix.ui.theme.RojoError
import com.sanvalero.cellfix.ui.theme.VerdeExito
import com.sanvalero.cellfix.util.Formato
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    onBack: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(titulo, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun TarjetaIndicador(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, contentDescription = null, tint = color)
                Spacer(Modifier.width(8.dp))
                Text(
                    titulo,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                valor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CampoBusqueda(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String = "Buscar...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
fun EstadoVacio(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            texto,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun <T> SelectorOpciones(
    etiqueta: String,
    opciones: List<T>,
    seleccionada: T?,
    texto: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var abierto by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedButton(
            onClick = { abierto = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = seleccionada?.let(texto) ?: etiqueta,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(texto(opcion)) },
                    onClick = {
                        onSelect(opcion)
                        abierto = false
                    }
                )
            }
        }
    }
}

@Composable
fun SelectorFecha(
    etiqueta: String,
    valor: Long,
    onCambio: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendario = remember(valor) {
        Calendar.getInstance().apply { timeInMillis = valor }
    }
    OutlinedButton(
        onClick = {
            DatePickerDialog(
                context,
                { _, anio, mes, dia ->
                    val elegido = Calendar.getInstance()
                    elegido.set(anio, mes, dia, 0, 0, 0)
                    elegido.set(Calendar.MILLISECOND, 0)
                    onCambio(elegido.timeInMillis)
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.CalendarMonth, contentDescription = null)
        Spacer(Modifier.width(6.dp))
        Text("$etiqueta: ${Formato.fecha(valor)}")
    }
}

@Composable
fun ChipEstado(estado: String) {
    val color = when (estado) {
        EstadoReparacion.ENTREGADO -> VerdeExito
        EstadoReparacion.LISTO -> AzulCellFix
        EstadoReparacion.CANCELADO -> RojoError
        else -> AmbarAviso
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            EstadoReparacion.etiqueta(estado),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DialogoConfirmar(
    titulo: String,
    mensaje: String,
    textoConfirmar: String = "Eliminar",
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = { Text(mensaje) },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text(textoConfirmar, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
