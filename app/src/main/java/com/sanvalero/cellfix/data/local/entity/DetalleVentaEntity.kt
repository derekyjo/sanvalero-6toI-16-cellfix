package com.sanvalero.cellfix.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "detalle_venta",
    foreignKeys = [
        ForeignKey(
            entity = VentaEntity::class,
            parentColumns = ["id"],
            childColumns = ["ventaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ventaId"])]
)
data class DetalleVentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ventaId: Long,
    val descripcion: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)
