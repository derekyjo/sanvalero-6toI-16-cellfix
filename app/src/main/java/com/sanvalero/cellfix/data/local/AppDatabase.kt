package com.sanvalero.cellfix.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sanvalero.cellfix.data.local.dao.ClienteDao
import com.sanvalero.cellfix.data.local.dao.DetalleVentaDao
import com.sanvalero.cellfix.data.local.dao.EquipoDao
import com.sanvalero.cellfix.data.local.dao.GarantiaDao
import com.sanvalero.cellfix.data.local.dao.ReparacionDao
import com.sanvalero.cellfix.data.local.dao.TecnicoDao
import com.sanvalero.cellfix.data.local.dao.UsuarioDao
import com.sanvalero.cellfix.data.local.dao.VentaDao
import com.sanvalero.cellfix.data.local.entity.ClienteEntity
import com.sanvalero.cellfix.data.local.entity.DetalleVentaEntity
import com.sanvalero.cellfix.data.local.entity.EquipoEntity
import com.sanvalero.cellfix.data.local.entity.GarantiaEntity
import com.sanvalero.cellfix.data.local.entity.ReparacionEntity
import com.sanvalero.cellfix.data.local.entity.TecnicoEntity
import com.sanvalero.cellfix.data.local.entity.UsuarioEntity
import com.sanvalero.cellfix.data.local.entity.VentaEntity

@Database(
    entities = [
        UsuarioEntity::class,
        ClienteEntity::class,
        EquipoEntity::class,
        TecnicoEntity::class,
        ReparacionEntity::class,
        GarantiaEntity::class,
        VentaEntity::class,
        DetalleVentaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun clienteDao(): ClienteDao
    abstract fun equipoDao(): EquipoDao
    abstract fun tecnicoDao(): TecnicoDao
    abstract fun reparacionDao(): ReparacionDao
    abstract fun garantiaDao(): GarantiaDao
    abstract fun ventaDao(): VentaDao
    abstract fun detalleVentaDao(): DetalleVentaDao
}
