package com.sanvalero.cellfix.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Usuario del sistema. La contraseña nunca se guarda en texto plano. */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["username"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val salt: String,
    val rol: String
)
