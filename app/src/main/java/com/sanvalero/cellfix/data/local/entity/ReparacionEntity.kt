package com.sanvalero.cellfix.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reparaciones",
    foreignKeys = [
        ForeignKey(
            entity = EquipoEntity::class,
            parentColumns = ["id"],
            childColumns = ["equipoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TecnicoEntity::class,
            parentColumns = ["id"],
            childColumns = ["tecnicoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["equipoId"]), Index(value = ["tecnicoId"])]
)
data class ReparacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipoId: Long,
    val tecnicoId: Long,
    val falla: String,
    val diagnostico: String,
    val fechaIngreso: Long,
    val fechaEntrega: Long?,
    val costoManoObra: Double,
    val costoRepuestos: Double,
    val estado: String,
    val itbis: Double,
    val total: Double
)
