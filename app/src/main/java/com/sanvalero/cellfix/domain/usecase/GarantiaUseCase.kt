package com.sanvalero.cellfix.domain.usecase

import com.sanvalero.cellfix.domain.model.Garantia
import com.sanvalero.cellfix.domain.repository.CellFixRepository
import javax.inject.Inject

/**
 * Regla de negocio destacada de CellFix: la garantía se controla por el IMEI
 * del equipo y no puede duplicarse mientras siga vigente.
 */
class GarantiaUseCase @Inject constructor(
    private val repository: CellFixRepository,
    private val validador: ValidadorUseCase
) {

    suspend fun crearParaReparacion(
        reparacionId: Long,
        dias: Int = 30,
        observaciones: String = "Garantía generada automáticamente al entregar el equipo."
    ): Result<Garantia> {
        val reparacion = repository.reparacionPorId(reparacionId)
            ?: return Result.failure(IllegalArgumentException("La reparación no existe."))

        val equipo = repository.equipoPorId(reparacion.equipoId)
            ?: return Result.failure(IllegalArgumentException("El equipo de la reparación no existe."))

        if (!validador.imeiValido(equipo.imei)) {
            return Result.failure(IllegalArgumentException("El IMEI del equipo no es válido (15 dígitos)."))
        }

        val ahora = System.currentTimeMillis()

        // Una garantía vigente del mismo equipo (aunque sea de otra reparación) bloquea
        // la creación de una nueva para el mismo IMEI.
        val otrasReparaciones = repository.reparacionesSnapshot()
            .filter { it.equipoId == reparacion.equipoId && it.id != reparacionId }

        val otraVigente = otrasReparaciones.any { otra ->
            val garantia = repository.garantiaPorReparacion(otra.id)
            garantia != null && garantia.fechaFin >= ahora
        }
        if (otraVigente) {
            return Result.failure(
                IllegalStateException("El equipo con IMEI ${equipo.imei} ya tiene una garantía vigente.")
            )
        }

        val existente = repository.garantiaPorReparacion(reparacionId)
        if (existente != null && existente.fechaFin >= ahora) {
            return Result.failure(
                IllegalStateException("El equipo con IMEI ${equipo.imei} ya tiene una garantía vigente.")
            )
        }

        val nueva = Garantia(
            // Si la reparación ya tuvo una garantía vencida, se reutiliza su id para
            // actualizarla y respetar el índice único de reparacionId.
            id = existente?.id ?: 0,
            reparacionId = reparacionId,
            fechaInicio = ahora,
            fechaFin = ahora + (dias.toLong() * DIA_MS),
            dias = dias,
            observaciones = observaciones
        )
        val id = repository.guardarGarantia(nueva)
        return Result.success(nueva.copy(id = id))
    }

    companion object {
        const val DIA_MS = 86_400_000L
    }
}
