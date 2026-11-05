package com.sanvalero.cellfix.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sanvalero.cellfix.data.local.entity.DetalleVentaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleVentaDao {

    @Query("SELECT * FROM detalle_venta WHERE ventaId = :ventaId ORDER BY id ASC")
    fun porVenta(ventaId: Long): Flow<List<DetalleVentaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(detalles: List<DetalleVentaEntity>): List<Long>

    @Query("DELETE FROM detalle_venta WHERE ventaId = :ventaId")
    suspend fun eliminarPorVenta(ventaId: Long)
}
