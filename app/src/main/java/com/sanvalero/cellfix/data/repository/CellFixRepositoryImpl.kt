package com.sanvalero.cellfix.data.repository

import androidx.room.withTransaction
import com.sanvalero.cellfix.data.local.AppDatabase
import com.sanvalero.cellfix.data.local.dao.ClienteDao
import com.sanvalero.cellfix.data.local.dao.DetalleVentaDao
import com.sanvalero.cellfix.data.local.dao.EquipoDao
import com.sanvalero.cellfix.data.local.dao.GarantiaDao
import com.sanvalero.cellfix.data.local.dao.ReparacionDao
import com.sanvalero.cellfix.data.local.dao.TecnicoDao
import com.sanvalero.cellfix.data.local.dao.UsuarioDao
import com.sanvalero.cellfix.data.local.dao.VentaDao
import com.sanvalero.cellfix.data.local.entity.ClienteEntity
import com.sanvalero.cellfix.data.local.entity.DetalleVentaEntity
import com.sanvalero.cellfix.data.local.entity.EquipoEntity
import com.sanvalero.cellfix.data.local.entity.GarantiaEntity
import com.sanvalero.cellfix.data.local.entity.ReparacionEntity
import com.sanvalero.cellfix.data.local.entity.TecnicoEntity
import com.sanvalero.cellfix.data.local.entity.UsuarioEntity
import com.sanvalero.cellfix.data.local.entity.VentaEntity
import com.sanvalero.cellfix.data.mapper.toDomain
import com.sanvalero.cellfix.data.mapper.toEntity
import com.sanvalero.cellfix.domain.model.Cliente
import com.sanvalero.cellfix.domain.model.DetalleVenta
import com.sanvalero.cellfix.domain.model.Equipo
import com.sanvalero.cellfix.domain.model.EquipoDetalle
import com.sanvalero.cellfix.domain.model.EstadoReparacion
import com.sanvalero.cellfix.domain.model.Garantia
import com.sanvalero.cellfix.domain.model.GarantiaDetalle
import com.sanvalero.cellfix.domain.model.Reparacion
import com.sanvalero.cellfix.domain.model.ReparacionDetalle
import com.sanvalero.cellfix.domain.model.Roles
import com.sanvalero.cellfix.domain.model.Tecnico
import com.sanvalero.cellfix.domain.model.Usuario
import com.sanvalero.cellfix.domain.model.Venta
import com.sanvalero.cellfix.domain.model.VentaResumen
import com.sanvalero.cellfix.domain.repository.CellFixRepository
import com.sanvalero.cellfix.util.CryptoUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val DIA_MS = 86_400_000L

@Singleton
class CellFixRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val usuarioDao: UsuarioDao,
    private val clienteDao: ClienteDao,
    private val equipoDao: EquipoDao,
    private val tecnicoDao: TecnicoDao,
    private val reparacionDao: ReparacionDao,
    private val garantiaDao: GarantiaDao,
    private val ventaDao: VentaDao,
    private val detalleVentaDao: DetalleVentaDao
) : CellFixRepository {

    override suspend fun login(username: String, password: String): Usuario? {
        val usuario = usuarioDao.buscarPorUsername(username.trim()) ?: return null
        return if (CryptoUtils.verificar(password, usuario.salt, usuario.passwordHash)) {
            usuario.toDomain()
        } else {
            null
        }
    }

    override suspend fun seedIfEmpty() {
        if (usuarioDao.contar() > 0) return

        db.withTransaction {
            val salAdmin = CryptoUtils.nuevaSal()
            usuarioDao.insertar(
                UsuarioEntity(
                    username = "admin",
                    passwordHash = CryptoUtils.hash("admin123", salAdmin),
                    salt = salAdmin,
                    rol = Roles.ADMIN
                )
            )
            val salOperador = CryptoUtils.nuevaSal()
            usuarioDao.insertar(
                UsuarioEntity(
                    username = "operador",
                    passwordHash = CryptoUtils.hash("operador123", salOperador),
                    salt = salOperador,
                    rol = Roles.OPERADOR
                )
            )

            val ahora = System.currentTimeMillis()

            val cliente1 = clienteDao.guardar(
                ClienteEntity(
                    nombre = "María Rodríguez",
                    cedula = "001-1234567-8",
                    telefono = "809-555-0101",
                    email = "maria@correo.com"
                )
            )
            val cliente2 = clienteDao.guardar(
                ClienteEntity(
                    nombre = "Luis Fernández",
                    cedula = "402-7654321-0",
                    telefono = "829-555-0202",
                    email = "luis@correo.com"
                )
            )
            val cliente3 = clienteDao.guardar(
                ClienteEntity(
                    nombre = "Ana Martínez",
                    cedula = "031-9876543-2",
                    telefono = "849-555-0303",
                    email = "ana@correo.com"
                )
            )

            val tecnico1 = tecnicoDao.guardar(
                TecnicoEntity(
                    nombre = "Carlos Pérez",
                    especialidad = "Microelectrónica",
                    telefono = "809-555-1000"
                )
            )
            val tecnico2 = tecnicoDao.guardar(
                TecnicoEntity(
                    nombre = "Yanibel Soto",
                    especialidad = "Software y flasheo",
                    telefono = "829-555-2000"
                )
            )

            val equipo1 = equipoDao.guardar(
                EquipoEntity(
                    clienteId = cliente1,
                    marca = "Samsung",
                    modelo = "Galaxy A54",
                    imei = "356789012345671",
                    color = "Negro"
                )
            )
            equipoDao.guardar(
                EquipoEntity(
                    clienteId = cliente2,
                    marca = "Apple",
                    modelo = "iPhone 13",
                    imei = "356789012345672",
                    color = "Azul"
                )
            )
            equipoDao.guardar(
                EquipoEntity(
                    clienteId = cliente3,
                    marca = "Xiaomi",
                    modelo = "Redmi Note 12",
                    imei = "356789012345673",
                    color = "Gris"
                )
            )

            val reparacion1 = reparacionDao.guardar(
                ReparacionEntity(
                    equipoId = equipo1,
                    tecnicoId = tecnico1,
                    falla = "No enciende después de caída",
                    diagnostico = "Conector de carga dañado",
                    fechaIngreso = ahora - (5 * DIA_MS),
                    fechaEntrega = ahora - (3 * DIA_MS),
                    costoManoObra = 2_500.0,
                    costoRepuestos = 1_800.0,
                    estado = EstadoReparacion.ENTREGADO,
                    itbis = 774.0,
                    total = 5_074.0
                )
            )
            reparacionDao.guardar(
                ReparacionEntity(
                    equipoId = equipo1,
                    tecnicoId = tecnico2,
                    falla = "Pantalla con líneas",
                    diagnostico = "Pendiente de revisión",
                    fechaIngreso = ahora - DIA_MS,
                    fechaEntrega = null,
                    costoManoObra = 1_500.0,
                    costoRepuestos = 4_200.0,
                    estado = EstadoReparacion.EN_REPARACION,
                    itbis = 1_026.0,
                    total = 6_726.0
                )
            )

            garantiaDao.guardar(
                GarantiaEntity(
                    reparacionId = reparacion1,
                    fechaInicio = ahora - (3 * DIA_MS),
                    fechaFin = ahora + (27 * DIA_MS),
                    dias = 30,
                    observaciones = "Garantía de 30 días por cambio de conector de carga."
                )
            )

            val venta1 = ventaDao.guardar(
                VentaEntity(
                    clienteId = cliente1,
                    fecha = ahora - (2 * DIA_MS),
                    subtotal = 3_500.0,
                    itbis = 630.0,
                    total = 4_130.0,
                    metodoPago = "EFECTIVO"
                )
            )
            detalleVentaDao.insertarTodos(
                listOf(
                    DetalleVentaEntity(
                        ventaId = venta1,
                        descripcion = "Forro protector reforzado",
                        cantidad = 2,
                        precioUnitario = 750.0,
                        subtotal = 1_500.0
                    ),
                    DetalleVentaEntity(
                        ventaId = venta1,
                        descripcion = "Cargador rápido 25W",
                        cantidad = 1,
                        precioUnitario = 2_000.0,
                        subtotal = 2_000.0
                    )
                )
            )
        }
    }

    override fun observarClientes(): Flow<List<Cliente>> =
        clienteDao.observarTodos().map { lista -> lista.map { it.toDomain() } }

    override fun buscarClientes(q: String): Flow<List<Cliente>> =
        clienteDao.buscar(q).map { lista -> lista.map { it.toDomain() } }

    override suspend fun clientePorId(id: Long): Cliente? = clienteDao.porId(id)?.toDomain()

    override suspend fun guardarCliente(cliente: Cliente): Long =
        clienteDao.guardar(cliente.toEntity())

    override suspend fun eliminarCliente(cliente: Cliente) =
        clienteDao.eliminar(cliente.toEntity())

    override suspend fun clientesSnapshot(): List<Cliente> =
        clienteDao.todos().map { it.toDomain() }

    override fun observarEquipos(): Flow<List<EquipoDetalle>> = equipoDao.observarTodos()
    override fun buscarEquipos(q: String): Flow<List<EquipoDetalle>> = equipoDao.buscar(q)
    override suspend fun equipoPorId(id: Long): Equipo? = equipoDao.porId(id)?.toDomain()
    override suspend fun equipoPorImei(imei: String): Equipo? =
        equipoDao.porImei(imei.trim())?.toDomain()

    override suspend fun guardarEquipo(equipo: Equipo): Long =
        equipoDao.guardar(equipo.toEntity())

    override suspend fun eliminarEquipo(equipo: Equipo) =
        equipoDao.eliminar(equipo.toEntity())

    override fun observarTecnicos(soloActivos: Boolean): Flow<List<Tecnico>> {
        val flujo = if (soloActivos) tecnicoDao.observarActivos() else tecnicoDao.observarTodos()
        return flujo.map { lista -> lista.map { it.toDomain() } }
    }

    override suspend fun tecnicoPorId(id: Long): Tecnico? = tecnicoDao.porId(id)?.toDomain()
    override suspend fun guardarTecnico(tecnico: Tecnico): Long =
        tecnicoDao.guardar(tecnico.toEntity())

    override suspend fun eliminarTecnico(tecnico: Tecnico) =
        tecnicoDao.eliminar(tecnico.toEntity())

    override suspend fun tecnicosSnapshot(): List<Tecnico> =
        tecnicoDao.todos().map { it.toDomain() }

    override fun observarReparaciones(): Flow<List<ReparacionDetalle>> =
        reparacionDao.observarTodas()

    override fun buscarReparaciones(q: String): Flow<List<ReparacionDetalle>> =
        reparacionDao.buscar(q)

    override suspend fun reparacionPorId(id: Long): Reparacion? =
        reparacionDao.porId(id)?.toDomain()

    override suspend fun guardarReparacion(reparacion: Reparacion): Long =
        reparacionDao.guardar(reparacion.toEntity())

    override suspend fun eliminarReparacion(reparacion: Reparacion) =
        reparacionDao.eliminar(reparacion.toEntity())

    override suspend fun reparacionesSnapshot(): List<Reparacion> =
        reparacionDao.todas().map { it.toDomain() }

    override suspend fun reparacionesDetalleSnapshot(): List<ReparacionDetalle> =
        reparacionDao.todasDetalle()

    override fun contarReparaciones(): Flow<Int> = reparacionDao.contar()
    override fun contarReparacionesPorEstado(estado: String): Flow<Int> =
        reparacionDao.contarPorEstado(estado)

    override fun totalReparacionesEntregadas(): Flow<Double> = reparacionDao.totalEntregado()

    override fun observarGarantias(): Flow<List<GarantiaDetalle>> = garantiaDao.observarTodas()
    override fun buscarGarantiasPorImei(imei: String): Flow<List<GarantiaDetalle>> =
        garantiaDao.buscarPorImei(imei)

    override suspend fun garantiaPorReparacion(reparacionId: Long): Garantia? =
        garantiaDao.porReparacion(reparacionId)?.toDomain()

    override suspend fun guardarGarantia(garantia: Garantia): Long =
        garantiaDao.guardar(garantia.toEntity())

    override suspend fun eliminarGarantia(garantia: Garantia) =
        garantiaDao.eliminar(garantia.toEntity())

    override suspend fun garantiasSnapshot(): List<Garantia> =
        garantiaDao.todas().map { it.toDomain() }

    override fun contarGarantiasActivas(ahora: Long): Flow<Int> =
        garantiaDao.contarActivas(ahora)

    override fun observarVentas(): Flow<List<VentaResumen>> = ventaDao.observarTodas()
    override fun observarDetalleVenta(ventaId: Long): Flow<List<DetalleVenta>> =
        detalleVentaDao.porVenta(ventaId).map { lista -> lista.map { it.toDomain() } }

    override suspend fun crearVenta(venta: Venta, detalles: List<DetalleVenta>): Long =
        db.withTransaction {
            val ventaId = ventaDao.guardar(venta.copy(id = 0).toEntity())
            detalleVentaDao.insertarTodos(
                detalles.map { it.copy(id = 0, ventaId = ventaId).toEntity() }
            )
            ventaId
        }

    override suspend fun eliminarVenta(venta: Venta) =
        ventaDao.eliminar(venta.copy(id = venta.id).toEntity())

    override suspend fun ventasSnapshot(): List<Venta> =
        ventaDao.todas().map { it.toDomain() }

    override fun contarVentas(): Flow<Int> = ventaDao.contar()
    override fun totalVentas(): Flow<Double> = ventaDao.totalVendido()

    override fun contarClientes(): Flow<Int> = clienteDao.contar()
    override fun contarEquipos(): Flow<Int> = equipoDao.contar()
}
