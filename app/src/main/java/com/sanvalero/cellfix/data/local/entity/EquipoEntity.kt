package com.sanvalero.cellfix.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Equipo (teléfono) que pertenece a un cliente. El IMEI es único. */
@Entity(
    tableName = "equipos",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["clienteId"]),
        Index(value = ["imei"], unique = true)
    ]
)
data class EquipoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val marca: String,
    val modelo: String,
    val imei: String,
    val color: String
)
