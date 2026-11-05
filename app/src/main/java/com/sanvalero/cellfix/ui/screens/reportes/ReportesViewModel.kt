package com.sanvalero.cellfix.ui.screens.reportes

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanvalero.cellfix.domain.repository.CellFixRepository
import com.sanvalero.cellfix.report.ReportGenerator
import com.sanvalero.cellfix.util.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReportesViewModel @Inject constructor(
    private val repository: CellFixRepository,
    private val reportGenerator: ReportGenerator,
    private val backupManager: BackupManager
) : ViewModel() {

    fun reporteClientes(onDone: (Result<Uri>) -> Unit) {
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                runCatching { reportGenerator.listadoClientes(repository.clientesSnapshot()) }
            }
            onDone(resultado)
        }
    }

    fun reportePeriodo(desde: Long, hasta: Long, onDone: (Result<Uri>) -> Unit) {
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                runCatching {
                    reportGenerator.reparacionesPeriodo(
                        repository.reparacionesDetalleSnapshot(),
                        desde,
                        hasta
                    )
                }
            }
            onDone(resultado)
        }
    }

    fun reportePeriodoCsv(desde: Long, hasta: Long, onDone: (Result<Uri>) -> Unit) {
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                runCatching {
                    reportGenerator.reparacionesPeriodoCsv(
                        repository.reparacionesDetalleSnapshot(),
                        desde,
                        hasta
                    )
                }
            }
            onDone(resultado)
        }
    }

    fun resumenGerencial(onDone: (Result<Uri>) -> Unit) {
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                runCatching {
                    reportGenerator.resumenGerencial(
                        clientes = repository.clientesSnapshot(),
                        reparaciones = repository.reparacionesSnapshot(),
                        ventas = repository.ventasSnapshot(),
                        garantias = repository.garantiasSnapshot(),
                        tecnicos = repository.tecnicosSnapshot()
                    )
                }
            }
            onDone(resultado)
        }
    }

    fun exportarBackup(onDone: (Result<Uri>) -> Unit) {
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                runCatching { backupManager.exportar() }
            }
            onDone(resultado)
        }
    }

    fun importarBackup(uri: Uri, onDone: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                backupManager.importar(uri)
            }
            onDone(resultado)
        }
    }

    fun compartir(uri: Uri, mime: String) {
        reportGenerator.compartir(uri, mime)
    }

    fun compartirBackup(uri: Uri) {
        backupManager.compartir(uri)
    }
}
