package com.sanvalero.cellfix.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clientes",
    indices = [Index(value = ["cedula"], unique = true)]
)
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val cedula: String,
    val telefono: String,
    val email: String
)
