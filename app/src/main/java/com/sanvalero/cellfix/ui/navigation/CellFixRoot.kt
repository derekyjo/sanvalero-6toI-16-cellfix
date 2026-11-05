package com.sanvalero.cellfix.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sanvalero.cellfix.domain.model.Roles
import com.sanvalero.cellfix.ui.screens.clientes.ClientesScreen
import com.sanvalero.cellfix.ui.screens.dashboard.DashboardScreen
import com.sanvalero.cellfix.ui.screens.equipos.EquiposScreen
import com.sanvalero.cellfix.ui.screens.garantias.GarantiasScreen
import com.sanvalero.cellfix.ui.screens.login.LoginScreen
import com.sanvalero.cellfix.ui.screens.reparaciones.ReparacionesScreen
import com.sanvalero.cellfix.ui.screens.reportes.ReportesScreen
import com.sanvalero.cellfix.ui.screens.tecnicos.TecnicosScreen
import com.sanvalero.cellfix.ui.screens.ventas.VentasScreen
import com.sanvalero.cellfix.ui.session.SessionViewModel

private object Rutas {
    const val DASHBOARD = "dashboard"
    const val CLIENTES = "clientes"
    const val EQUIPOS = "equipos"
    const val TECNICOS = "tecnicos"
    const val REPARACIONES = "reparaciones"
    const val GARANTIAS = "garantias"
    const val VENTAS = "ventas"
    const val REPORTES = "reportes"
}

/**
 * Raíz de la aplicación: si no hay sesión muestra el login; si hay sesión
 * monta el grafo de navegación autenticado.
 */
@Composable
fun CellFixRoot(viewModel: SessionViewModel = hiltViewModel()) {
    val usuario by viewModel.usuario.collectAsState()

    if (usuario == null) {
        LoginScreen(onLogin = {})
    } else {
        val actual = usuario
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Rutas.DASHBOARD
        ) {
            composable(Rutas.DASHBOARD) {
                DashboardScreen(
                    esAdmin = actual?.rol == Roles.ADMIN,
                    onNavigate = { ruta ->
                        navController.navigate(ruta) { launchSingleTop = true }
                    },
                    onLogout = { viewModel.salir() }
                )
            }
            composable(Rutas.CLIENTES) {
                ClientesScreen(onBack = { navController.popBackStack() })
            }
            composable(Rutas.EQUIPOS) {
                EquiposScreen(onBack = { navController.popBackStack() })
            }
            composable(Rutas.TECNICOS) {
                TecnicosScreen(onBack = { navController.popBackStack() })
            }
            composable(Rutas.REPARACIONES) {
                ReparacionesScreen(onBack = { navController.popBackStack() })
            }
            composable(Rutas.GARANTIAS) {
                GarantiasScreen(onBack = { navController.popBackStack() })
            }
            composable(Rutas.VENTAS) {
                VentasScreen(onBack = { navController.popBackStack() })
            }
            composable(Rutas.REPORTES) {
                ReportesScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
