package com.sanvalero.cellfix.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tecnicos")
data class TecnicoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val especialidad: String,
    val telefono: String,
    val activo: Boolean = true
)
