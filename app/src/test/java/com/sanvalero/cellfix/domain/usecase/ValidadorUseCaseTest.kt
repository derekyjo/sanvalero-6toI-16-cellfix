package com.sanvalero.cellfix.domain.usecase

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorUseCaseTest {

    private val validador = ValidadorUseCase()

    @Test
    fun `cedula con once digitos es valida`() {
        assertTrue(validador.cedulaValida("001-1234567-8"))
        assertTrue(validador.cedulaValida("00112345678"))
    }

    @Test
    fun `cedula con otra cantidad de digitos no es valida`() {
        assertFalse(validador.cedulaValida("1234567"))
        assertFalse(validador.cedulaValida("001-1234567-89"))
        assertFalse(validador.cedulaValida(""))
    }

    @Test
    fun `rnc acepta nueve u once digitos`() {
        assertTrue(validador.rncValido("101-12345-6"))
        assertTrue(validador.rncValido("10112345678"))
        assertFalse(validador.rncValido("12345"))
    }

    @Test
    fun `telefono requiere al menos diez digitos`() {
        assertTrue(validador.telefonoValido("809-555-0101"))
        assertTrue(validador.telefonoValido("8295550202"))
        assertFalse(validador.telefonoValido("809-555"))
    }

    @Test
    fun `imei requiere exactamente quince digitos`() {
        assertTrue(validador.imeiValido("356789012345671"))
        assertFalse(validador.imeiValido("35678901234567"))
        assertFalse(validador.imeiValido("3567890123456712"))
    }

    @Test
    fun `correo vacio es valido y correo mal formado no`() {
        assertTrue(validador.emailValido(""))
        assertTrue(validador.emailValido("cliente@correo.com"))
        assertFalse(validador.emailValido("cliente-correo"))
        assertFalse(validador.emailValido("@correo.com"))
    }

    @Test
    fun `requerido rechaza cadenas en blanco`() {
        assertTrue(validador.requerido("CellFix"))
        assertFalse(validador.requerido("   "))
    }
}
