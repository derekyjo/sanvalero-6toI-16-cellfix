package com.sanvalero.cellfix.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculadoraUseCaseTest {

    private val calculadora = CalculadoraUseCase()

    @Test
    fun `calcular venta aplica itbis de 18 por ciento`() {
        val totales = calculadora.calcularVenta(1_000.0)

        assertEquals(1_000.0, totales.subtotal, 0.001)
        assertEquals(180.0, totales.itbis, 0.001)
        assertEquals(1_180.0, totales.total, 0.001)
    }

    @Test
    fun `calcular reparacion suma mano de obra y repuestos`() {
        val totales = calculadora.calcularReparacion(
            costoManoObra = 2_500.0,
            costoRepuestos = 1_800.0
        )

        assertEquals(4_300.0, totales.subtotal, 0.001)
        assertEquals(774.0, totales.itbis, 0.001)
        assertEquals(5_074.0, totales.total, 0.001)
    }

    @Test
    fun `calcular reparacion sin itbis deja el impuesto en cero`() {
        val totales = calculadora.calcularReparacion(
            costoManoObra = 100.0,
            costoRepuestos = 50.0,
            aplicaItbis = false
        )

        assertEquals(150.0, totales.subtotal, 0.001)
        assertEquals(0.0, totales.itbis, 0.001)
        assertEquals(150.0, totales.total, 0.001)
    }

    @Test
    fun `calcular venta redondea a dos decimales`() {
        val totales = calculadora.calcularVenta(99.99)

        assertEquals(99.99, totales.subtotal, 0.001)
        assertEquals(18.0, totales.itbis, 0.001)
        assertEquals(117.99, totales.total, 0.001)
    }

    @Test
    fun `redondear deja dos decimales`() {
        assertEquals(10.13, calculadora.redondear(10.126), 0.0001)
        assertEquals(10.12, calculadora.redondear(10.124), 0.0001)
    }
}
