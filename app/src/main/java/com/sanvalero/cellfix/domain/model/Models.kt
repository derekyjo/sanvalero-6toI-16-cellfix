package com.sanvalero.cellfix.domain.model

/** Modelos de negocio usados por la capa de dominio y la interfaz. */

data class Usuario(
    val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val salt: String,
    val rol: String
)

data class Cliente(
    val id: Long = 0,
    val nombre: String = "",
    val cedula: String = "",
    val telefono: String = "",
    val email: String = ""
)

data class Equipo(
    val id: Long = 0,
    val clienteId: Long = 0,
    val marca: String = "",
    val modelo: String = "",
    val imei: String = "",
    val color: String = ""
)

data class Tecnico(
    val id: Long = 0,
    val nombre: String = "",
    val especialidad: String = "",
    val telefono: String = "",
    val activo: Boolean = true
)

data class Reparacion(
    val id: Long = 0,
    val equipoId: Long = 0,
    val tecnicoId: Long = 0,
    val falla: String = "",
    val diagnostico: String = "",
    val fechaIngreso: Long = System.currentTimeMillis(),
    val fechaEntrega: Long? = null,
    val costoManoObra: Double = 0.0,
    val costoRepuestos: Double = 0.0,
    val estado: String = EstadoReparacion.RECIBIDO,
    val itbis: Double = 0.0,
    val total: Double = 0.0
)

data class Garantia(
    val id: Long = 0,
    val reparacionId: Long = 0,
    val fechaInicio: Long = System.currentTimeMillis(),
    val fechaFin: Long = System.currentTimeMillis(),
    val dias: Int = 30,
    val observaciones: String = ""
)

data class Venta(
    val id: Long = 0,
    val clienteId: Long = 0,
    val fecha: Long = System.currentTimeMillis(),
    val subtotal: Double = 0.0,
    val itbis: Double = 0.0,
    val total: Double = 0.0,
    val metodoPago: String = "EFECTIVO"
)

data class DetalleVenta(
    val id: Long = 0,
    val ventaId: Long = 0,
    val descripcion: String = "",
    val cantidad: Int = 1,
    val precioUnitario: Double = 0.0,
    val subtotal: Double = 0.0
)

/** Modelos de lectura con datos combinados de varias tablas. */

data class EquipoDetalle(
    val id: Long,
    val clienteId: Long,
    val marca: String,
    val modelo: String,
    val imei: String,
    val color: String,
    val clienteNombre: String
)

data class ReparacionDetalle(
    val id: Long,
    val falla: String,
    val diagnostico: String,
    val estado: String,
    val fechaIngreso: Long,
    val fechaEntrega: Long?,
    val total: Double,
    val marca: String,
    val modelo: String,
    val imei: String,
    val clienteNombre: String,
    val tecnicoNombre: String
)

data class GarantiaDetalle(
    val id: Long,
    val reparacionId: Long,
    val fechaInicio: Long,
    val fechaFin: Long,
    val dias: Int,
    val observaciones: String,
    val imei: String,
    val marca: String,
    val modelo: String,
    val clienteNombre: String
)

data class VentaResumen(
    val id: Long,
    val fecha: Long,
    val subtotal: Double,
    val itbis: Double,
    val total: Double,
    val metodoPago: String,
    val clienteNombre: String
)

object EstadoReparacion {
    const val RECIBIDO = "RECIBIDO"
    const val DIAGNOSTICO = "DIAGNOSTICO"
    const val EN_REPARACION = "EN_REPARACION"
    const val LISTO = "LISTO"
    const val ENTREGADO = "ENTREGADO"
    const val CANCELADO = "CANCELADO"

    val opciones = listOf(RECIBIDO, DIAGNOSTICO, EN_REPARACION, LISTO, ENTREGADO, CANCELADO)

    fun etiqueta(estado: String): String = when (estado) {
        RECIBIDO -> "Recibido"
        DIAGNOSTICO -> "En diagnóstico"
        EN_REPARACION -> "En reparación"
        LISTO -> "Listo para entrega"
        ENTREGADO -> "Entregado"
        CANCELADO -> "Cancelado"
        else -> estado
    }
}

object Roles {
    const val ADMIN = "ADMIN"
    const val OPERADOR = "OPERADOR"

    fun etiqueta(rol: String): String = if (rol == ADMIN) "Administrador" else "Operador"
}

object MetodosPago {
    val opciones = listOf("EFECTIVO", "TARJETA", "TRANSFERENCIA", "CREDITO")
}
