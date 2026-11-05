package com.sanvalero.cellfix.report

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.sanvalero.cellfix.domain.model.Cliente
import com.sanvalero.cellfix.domain.model.EstadoReparacion
import com.sanvalero.cellfix.domain.model.Garantia
import com.sanvalero.cellfix.domain.model.Reparacion
import com.sanvalero.cellfix.domain.model.ReparacionDetalle
import com.sanvalero.cellfix.domain.model.Tecnico
import com.sanvalero.cellfix.domain.model.Venta
import com.sanvalero.cellfix.util.Formato
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Genera los reportes obligatorios con la clase PdfDocument de Android y CSV.
 * El archivo se guarda en Descargas/CellFix (visible) y en la carpeta privada
 * de la app para compartirlo con Intent.ACTION_SEND.
 */
@Singleton
class ReportGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun listadoClientes(clientes: List<Cliente>): Uri {
        val lineas = mutableListOf<String>()
        lineas += "## Listado general de clientes"
        lineas += "Total de clientes registrados: ${clientes.size}"
        lineas += ""
        lineas += "Nombre / Cédula / Teléfono / Correo"
        clientes.forEach {
            lineas += "${it.nombre}  |  ${it.cedula}  |  ${it.telefono}  |  ${it.email.ifBlank { "-" }}"
        }
        return crearPdf(
            titulo = "Listado general de clientes",
            subtitulo = "Reporte de todos los clientes registrados en el sistema",
            lineas = lineas,
            nombre = "clientes_${Formato.selloArchivo()}.pdf"
        )
    }

    fun reparacionesPeriodo(
        reparaciones: List<ReparacionDetalle>,
        desde: Long,
        hasta: Long
    ): Uri {
        val filtradas = reparaciones.filter { it.fechaIngreso in desde..hasta }
        val lineas = mutableListOf<String>()
        lineas += "## Reporte por período"
        lineas += "Desde: ${Formato.fecha(desde)}   Hasta: ${Formato.fecha(hasta)}"
        lineas += "Cantidad de reparaciones: ${filtradas.size}"
        lineas += ""
        lineas += "Fecha / IMEI / Cliente / Equipo / Estado / Total"
        filtradas.forEach {
            lineas += "${Formato.fecha(it.fechaIngreso)}  |  ${it.imei}  |  ${it.clienteNombre}  |  " +
                "${it.marca} ${it.modelo}  |  ${EstadoReparacion.etiqueta(it.estado)}  |  ${Formato.moneda(it.total)}"
        }
        lineas += ""
        lineas += "## Total del período: ${Formato.moneda(filtradas.sumOf { it.total })}"
        return crearPdf(
            titulo = "Reparaciones por período",
            subtitulo = "Movimientos registrados entre dos fechas",
            lineas = lineas,
            nombre = "reparaciones_${Formato.selloArchivo()}.pdf"
        )
    }

    fun reparacionesPeriodoCsv(
        reparaciones: List<ReparacionDetalle>,
        desde: Long,
        hasta: Long
    ): Uri {
        val filtradas = reparaciones.filter { it.fechaIngreso in desde..hasta }
        val sb = StringBuilder()
        sb.appendLine("Fecha,IMEI,Cliente,Equipo,Estado,Total")
        filtradas.forEach {
            sb.appendLine(
                listOf(
                    Formato.fecha(it.fechaIngreso),
                    it.imei,
                    escapar(it.clienteNombre),
                    escapar("${it.marca} ${it.modelo}"),
                    EstadoReparacion.etiqueta(it.estado),
                    String.format(Locale.US, "%.2f", it.total)
                ).joinToString(",")
            )
        }
        return guardar(
            bytes = sb.toString().toByteArray(Charsets.UTF_8),
            nombre = "reparaciones_${Formato.selloArchivo()}.csv",
            mime = "text/csv"
        )
    }

    fun resumenGerencial(
        clientes: List<Cliente>,
        reparaciones: List<Reparacion>,
        ventas: List<Venta>,
        garantias: List<Garantia>,
        tecnicos: List<Tecnico>
    ): Uri {
        val ahora = System.currentTimeMillis()
        val garantiasActivas = garantias.count { it.fechaFin >= ahora }
        val totalReparaciones = reparaciones.sumOf { it.total }
        val totalVentas = ventas.sumOf { it.total }
        val porEstado = reparaciones.groupingBy { it.estado }.eachCount()

        val lineas = mutableListOf<String>()
        lineas += "## Indicadores generales"
        lineas += "Clientes registrados: ${clientes.size}"
        lineas += "Equipos en el sistema: ${reparaciones.map { it.equipoId }.distinct().size}"
        lineas += "Reparaciones registradas: ${reparaciones.size}"
        lineas += "Técnicos activos: ${tecnicos.count { it.activo }}"
        lineas += "Garantías vigentes: $garantiasActivas"
        lineas += ""
        lineas += "## Ingresos"
        lineas += "Total facturado por reparaciones entregadas: ${Formato.moneda(totalReparaciones)}"
        lineas += "Total facturado por ventas: ${Formato.moneda(totalVentas)}"
        lineas += "Ingreso combinado: ${Formato.moneda(totalReparaciones + totalVentas)}"
        lineas += ""
        lineas += "## Reparaciones por estado"
        EstadoReparacion.opciones.forEach { estado ->
            lineas += "${EstadoReparacion.etiqueta(estado)}: ${porEstado[estado] ?: 0}"
        }
        lineas += ""
        lineas += "## Top 5 clientes por reparaciones"
        reparaciones.groupingBy { it.equipoId }.eachCount().entries
            .sortedByDescending { it.value }
            .take(5)
            .forEach { (equipoId, cantidad) ->
                lineas += "Equipo #$equipoId: $cantidad reparación(es)"
            }

        return crearPdf(
            titulo = "Resumen gerencial",
            subtitulo = "Indicadores, totales y estado del taller",
            lineas = lineas,
            nombre = "resumen_gerencial_${Formato.selloArchivo()}.pdf"
        )
    }

    fun compartir(uri: Uri, mime: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Compartir reporte")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun crearPdf(titulo: String, subtitulo: String, lineas: List<String>, nombre: String): Uri {
        val documento = PdfDocument()
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
        }

        var numeroPagina = 1
        var pagina = documento.startPage(
            PdfDocument.PageInfo.Builder(595, 842, numeroPagina).create()
        )
        var canvas = pagina.canvas
        var y = 0f

        fun encabezado() {
            paint.color = Color.rgb(15, 23, 42)
            canvas.drawRect(0f, 0f, 595f, 80f, paint)
            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 20f
            canvas.drawText("CellFix SRL", 40f, 34f, paint)
            paint.typeface = Typeface.DEFAULT
            paint.textSize = 10f
            canvas.drawText("Ventas y reparaciones de celulares", 40f, 50f, paint)
            canvas.drawText(
                "Fecha de emisión: ${Formato.fechaHora(System.currentTimeMillis())}",
                40f,
                66f,
                paint
            )
            paint.color = Color.BLACK
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 15f
            canvas.drawText(titulo, 40f, 108f, paint)
            paint.typeface = Typeface.DEFAULT
            paint.textSize = 10f
            paint.color = Color.DKGRAY
            canvas.drawText(subtitulo, 40f, 126f, paint)
            paint.color = Color.BLACK
            paint.textSize = 10f
            y = 155f
        }

        fun pie() {
            paint.color = Color.GRAY
            paint.textSize = 9f
            canvas.drawText("CellFix SRL · Reporte generado por la aplicación", 40f, 818f, paint)
            canvas.drawText("Página $numeroPagina", 505f, 818f, paint)
            paint.color = Color.BLACK
        }

        fun nuevaPagina() {
            pie()
            documento.finishPage(pagina)
            numeroPagina++
            pagina = documento.startPage(
                PdfDocument.PageInfo.Builder(595, 842, numeroPagina).create()
            )
            canvas = pagina.canvas
            encabezado()
        }

        encabezado()
        for (linea in lineas) {
            if (y > 790f) nuevaPagina()
            if (linea.startsWith("## ")) {
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = 12f
                canvas.drawText(linea.removePrefix("## "), 40f, y, paint)
                paint.typeface = Typeface.DEFAULT
                paint.textSize = 10f
                y += 19f
            } else {
                paint.textSize = 10f
                canvas.drawText(linea, 40f, y, paint)
                y += 15f
            }
        }
        pie()
        documento.finishPage(pagina)

        val salida = ByteArrayOutputStream()
        documento.writeTo(salida)
        documento.close()

        return guardar(salida.toByteArray(), nombre, "application/pdf")
    }

    private fun guardar(bytes: ByteArray, nombre: String, mime: String): Uri {
        val carpeta = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "reportes")
        if (!carpeta.exists()) carpeta.mkdirs()
        val archivo = File(carpeta, nombre)
        FileOutputStream(archivo).use { it.write(bytes) }

        // Copia visible en Descargas/CellFix en Android 10 o superior.
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val valores = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, nombre)
                    put(MediaStore.MediaColumns.MIME_TYPE, mime)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS + "/CellFix"
                    )
                }
                val uri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    valores
                )
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
                }
            }
        } catch (_: Exception) {
            // Si no se puede publicar, el archivo igual queda disponible para compartir.
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
    }

    private fun escapar(valor: String): String {
        val limpio = valor.replace("\"", "\"\"")
        return if (limpio.contains(',') || limpio.contains('"') || limpio.contains('\n')) {
            "\"$limpio\""
        } else {
            limpio
        }
    }
}
