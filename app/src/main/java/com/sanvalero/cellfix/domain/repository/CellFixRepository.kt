package com.sanvalero.cellfix.domain.repository

import com.sanvalero.cellfix.domain.model.Cliente
import com.sanvalero.cellfix.domain.model.DetalleVenta
import com.sanvalero.cellfix.domain.model.Equipo
import com.sanvalero.cellfix.domain.model.EquipoDetalle
import com.sanvalero.cellfix.domain.model.Garantia
import com.sanvalero.cellfix.domain.model.GarantiaDetalle
import com.sanvalero.cellfix.domain.model.Reparacion
import com.sanvalero.cellfix.domain.model.ReparacionDetalle
import com.sanvalero.cellfix.domain.model.Tecnico
import com.sanvalero.cellfix.domain.model.Usuario
import com.sanvalero.cellfix.domain.model.Venta
import com.sanvalero.cellfix.domain.model.VentaResumen
import kotlinx.coroutines.flow.Flow

/**
 * Contrato único de acceso a datos. La interfaz (ViewModel) nunca toca los DAO:
 * siempre pasa por aquí.
 */
interface CellFixRepository {

    // Autenticación y datos de demostración
    suspend fun login(username: String, password: String): Usuario?
    suspend fun seedIfEmpty()

    // Clientes
    fun observarClientes(): Flow<List<Cliente>>
    fun buscarClientes(q: String): Flow<List<Cliente>>
    suspend fun clientePorId(id: Long): Cliente?
    suspend fun guardarCliente(cliente: Cliente): Long
    suspend fun eliminarCliente(cliente: Cliente)
    suspend fun clientesSnapshot(): List<Cliente>

    // Equipos
    fun observarEquipos(): Flow<List<EquipoDetalle>>
    fun buscarEquipos(q: String): Flow<List<EquipoDetalle>>
    suspend fun equipoPorId(id: Long): Equipo?
    suspend fun equipoPorImei(imei: String): Equipo?
    suspend fun guardarEquipo(equipo: Equipo): Long
    suspend fun eliminarEquipo(equipo: Equipo)

    // Técnicos
    fun observarTecnicos(soloActivos: Boolean = false): Flow<List<Tecnico>>
    suspend fun tecnicoPorId(id: Long): Tecnico?
    suspend fun guardarTecnico(tecnico: Tecnico): Long
    suspend fun eliminarTecnico(tecnico: Tecnico)
    suspend fun tecnicosSnapshot(): List<Tecnico>

    // Reparaciones
    fun observarReparaciones(): Flow<List<ReparacionDetalle>>
    fun buscarReparaciones(q: String): Flow<List<ReparacionDetalle>>
    suspend fun reparacionPorId(id: Long): Reparacion?
    suspend fun guardarReparacion(reparacion: Reparacion): Long
    suspend fun eliminarReparacion(reparacion: Reparacion)
    suspend fun reparacionesSnapshot(): List<Reparacion>
    suspend fun reparacionesDetalleSnapshot(): List<ReparacionDetalle>
    fun contarReparaciones(): Flow<Int>
    fun contarReparacionesPorEstado(estado: String): Flow<Int>
    fun totalReparacionesEntregadas(): Flow<Double>

    // Garantías
    fun observarGarantias(): Flow<List<GarantiaDetalle>>
    fun buscarGarantiasPorImei(imei: String): Flow<List<GarantiaDetalle>>
    suspend fun garantiaPorReparacion(reparacionId: Long): Garantia?
    suspend fun guardarGarantia(garantia: Garantia): Long
    suspend fun eliminarGarantia(garantia: Garantia)
    suspend fun garantiasSnapshot(): List<Garantia>
    fun contarGarantiasActivas(ahora: Long = System.currentTimeMillis()): Flow<Int>

    // Ventas
    fun observarVentas(): Flow<List<VentaResumen>>
    fun observarDetalleVenta(ventaId: Long): Flow<List<DetalleVenta>>
    suspend fun crearVenta(venta: Venta, detalles: List<DetalleVenta>): Long
    suspend fun eliminarVenta(venta: Venta)
    suspend fun ventasSnapshot(): List<Venta>
    fun contarVentas(): Flow<Int>
    fun totalVentas(): Flow<Double>

    // Indicadores
    fun contarClientes(): Flow<Int>
    fun contarEquipos(): Flow<Int>
}
