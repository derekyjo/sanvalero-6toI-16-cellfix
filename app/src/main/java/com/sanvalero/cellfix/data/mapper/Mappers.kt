package com.sanvalero.cellfix.data.mapper

import com.sanvalero.cellfix.data.local.entity.ClienteEntity
import com.sanvalero.cellfix.data.local.entity.DetalleVentaEntity
import com.sanvalero.cellfix.data.local.entity.EquipoEntity
import com.sanvalero.cellfix.data.local.entity.GarantiaEntity
import com.sanvalero.cellfix.data.local.entity.ReparacionEntity
import com.sanvalero.cellfix.data.local.entity.TecnicoEntity
import com.sanvalero.cellfix.data.local.entity.UsuarioEntity
import com.sanvalero.cellfix.data.local.entity.VentaEntity
import com.sanvalero.cellfix.domain.model.Cliente
import com.sanvalero.cellfix.domain.model.DetalleVenta
import com.sanvalero.cellfix.domain.model.Equipo
import com.sanvalero.cellfix.domain.model.Garantia
import com.sanvalero.cellfix.domain.model.Reparacion
import com.sanvalero.cellfix.domain.model.Tecnico
import com.sanvalero.cellfix.domain.model.Usuario
import com.sanvalero.cellfix.domain.model.Venta

fun UsuarioEntity.toDomain() = Usuario(id, username, passwordHash, salt, rol)
fun ClienteEntity.toDomain() = Cliente(id, nombre, cedula, telefono, email)
fun EquipoEntity.toDomain() = Equipo(id, clienteId, marca, modelo, imei, color)
fun TecnicoEntity.toDomain() = Tecnico(id, nombre, especialidad, telefono, activo)
fun ReparacionEntity.toDomain() = Reparacion(
    id, equipoId, tecnicoId, falla, diagnostico, fechaIngreso, fechaEntrega,
    costoManoObra, costoRepuestos, estado, itbis, total
)
fun GarantiaEntity.toDomain() = Garantia(id, reparacionId, fechaInicio, fechaFin, dias, observaciones)
fun VentaEntity.toDomain() = Venta(id, clienteId, fecha, subtotal, itbis, total, metodoPago)
fun DetalleVentaEntity.toDomain() = DetalleVenta(id, ventaId, descripcion, cantidad, precioUnitario, subtotal)

fun Cliente.toEntity() = ClienteEntity(id, nombre, cedula, telefono, email)
fun Equipo.toEntity() = EquipoEntity(id, clienteId, marca, modelo, imei, color)
fun Tecnico.toEntity() = TecnicoEntity(id, nombre, especialidad, telefono, activo)
fun Reparacion.toEntity() = ReparacionEntity(
    id, equipoId, tecnicoId, falla, diagnostico, fechaIngreso, fechaEntrega,
    costoManoObra, costoRepuestos, estado, itbis, total
)
fun Garantia.toEntity() = GarantiaEntity(id, reparacionId, fechaInicio, fechaFin, dias, observaciones)
fun Venta.toEntity() = VentaEntity(id, clienteId, fecha, subtotal, itbis, total, metodoPago)
fun DetalleVenta.toEntity() = DetalleVentaEntity(id, ventaId, descripcion, cantidad, precioUnitario, subtotal)
