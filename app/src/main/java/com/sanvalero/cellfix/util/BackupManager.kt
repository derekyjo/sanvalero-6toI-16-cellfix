package com.sanvalero.cellfix.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/** Exporta e importa el archivo de la base de datos Room. */
@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val nombreBaseDatos = "cellfix.db"

    fun exportar(): Uri {
        val origen = context.getDatabasePath(nombreBaseDatos)
        if (!origen.exists()) {
            throw IllegalStateException("La base de datos todavía no existe.")
        }
        val carpeta = File(context.getExternalFilesDir(null), "backups")
        if (!carpeta.exists()) carpeta.mkdirs()
        val destino = File(carpeta, "cellfix_backup_${Formato.selloArchivo()}.db")
        origen.copyTo(destino, overwrite = true)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            destino
        )
    }

    fun importar(uri: Uri): Result<Unit> = runCatching {
        val destino = context.getDatabasePath(nombreBaseDatos)
        destino.parentFile?.mkdirs()
        context.contentResolver.openInputStream(uri)?.use { entrada ->
            FileOutputStream(destino).use { salida ->
                entrada.copyTo(salida)
            }
        } ?: throw IllegalStateException("No se pudo leer el archivo seleccionado.")

        // Room usa diario TRUNCATE, pero eliminamos restos por seguridad.
        File("${destino.path}-wal").delete()
        File("${destino.path}-shm").delete()
        Unit
    }

    fun compartir(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Compartir respaldo")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
