package com.sanvalero.cellfix.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Garantía generada al entregar una reparación. Se consulta por IMEI del equipo. */
@Entity(
    tableName = "garantias",
    foreignKeys = [
        ForeignKey(
            entity = ReparacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["reparacionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["reparacionId"], unique = true)]
)
data class GarantiaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reparacionId: Long,
    val fechaInicio: Long,
    val fechaFin: Long,
    val dias: Int,
    val observaciones: String
)
