package com.sanvalero.cellfix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AzulCellFix = Color(0xFF0EA5E9)
val AzulOscuro = Color(0xFF0369A1)
val FondoOscuro = Color(0xFF0F172A)
val SuperficieOscura = Color(0xFF1E293B)
val VerdeExito = Color(0xFF22C55E)
val AmbarAviso = Color(0xFFF59E0B)
val RojoError = Color(0xFFEF4444)

private val ColoresClaros = lightColorScheme(
    primary = AzulCellFix,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBAE6FD),
    onPrimaryContainer = Color(0xFF082F49),
    secondary = AzulOscuro,
    onSecondary = Color.White,
    tertiary = VerdeExito,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF334155),
    error = RojoError
)

private val ColoresOscuros = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF082F49),
    primaryContainer = Color(0xFF075985),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = Color(0xFF7DD3FC),
    onSecondary = Color(0xFF082F49),
    tertiary = Color(0xFF4ADE80),
    background = FondoOscuro,
    onBackground = Color(0xFFE2E8F0),
    surface = SuperficieOscura,
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = Color(0xFFFCA5A5)
)

@Composable
fun CellFixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ColoresOscuros else ColoresClaros,
        typography = Typography(),
        content = content
    )
}
