package com.sanvalero.cellfix.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Formatos dominicanos: moneda RD$ y fechas dd/MM/yyyy. */
object Formato {

    private val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    private val fechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
    private val sello = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    fun moneda(valor: Double): String = "RD$ " + String.format(Locale.US, "%,.2f", valor)

    fun fecha(ms: Long): String = fecha.format(Date(ms))

    fun fechaHora(ms: Long): String = fechaHora.format(Date(ms))

    fun selloArchivo(): String = sello.format(Date())
}
