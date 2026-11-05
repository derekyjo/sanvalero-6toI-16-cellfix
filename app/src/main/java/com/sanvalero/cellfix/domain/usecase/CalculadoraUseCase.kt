package com.sanvalero.cellfix.domain.usecase

import javax.inject.Inject
import kotlin.math.round

/** Calcula ITBIS (18 %) y totales de reparaciones y ventas. */
class CalculadoraUseCase @Inject constructor() {

    data class Totales(val subtotal: Double, val itbis: Double, val total: Double)

    fun calcularReparacion(
        costoManoObra: Double,
        costoRepuestos: Double,
        aplicaItbis: Boolean = true
    ): Totales {
        val subtotal = redondear(costoManoObra + costoRepuestos)
        val itbis = if (aplicaItbis) redondear(subtotal * ITBIS) else 0.0
        return Totales(subtotal, itbis, redondear(subtotal + itbis))
    }

    fun calcularVenta(subtotal: Double): Totales {
        val base = redondear(subtotal)
        val itbis = redondear(base * ITBIS)
        return Totales(base, itbis, redondear(base + itbis))
    }

    fun redondear(valor: Double): Double = round(valor * 100.0) / 100.0

    companion object {
        const val ITBIS = 0.18
    }
}
