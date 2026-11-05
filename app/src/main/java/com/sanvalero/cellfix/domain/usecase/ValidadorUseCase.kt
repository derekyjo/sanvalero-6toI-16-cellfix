package com.sanvalero.cellfix.domain.usecase

import javax.inject.Inject

/** Reglas de validación de datos dominicanos y de equipos. */
class ValidadorUseCase @Inject constructor() {

    private val regexEmail = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun cedulaValida(valor: String): Boolean = soloDigitos(valor).length == 11

    fun rncValido(valor: String): Boolean {
        val digitos = soloDigitos(valor).length
        return digitos == 9 || digitos == 11
    }

    fun telefonoValido(valor: String): Boolean = soloDigitos(valor).length >= 10

    fun imeiValido(valor: String): Boolean = soloDigitos(valor).length == 15

    fun emailValido(valor: String): Boolean = valor.isBlank() || regexEmail.matches(valor.trim())

    fun requerido(valor: String): Boolean = valor.isNotBlank()

    private fun soloDigitos(valor: String): String = valor.filter { it.isDigit() }
}
