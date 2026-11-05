package com.sanvalero.cellfix.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sanvalero.cellfix.data.local.entity.VentaEntity
import com.sanvalero.cellfix.domain.model.VentaResumen
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {

    @Query(
        "SELECT v.id AS id, v.fecha AS fecha, v.subtotal AS subtotal, v.itbis AS itbis, " +
            "v.total AS total, v.metodoPago AS metodoPago, c.nombre AS clienteNombre " +
            "FROM ventas v INNER JOIN clientes c ON c.id = v.clienteId " +
            "ORDER BY v.fecha DESC"
    )
    fun observarTodas(): Flow<List<VentaResumen>>

    @Query("SELECT * FROM ventas WHERE id = :id")
    suspend fun porId(id: Long): VentaEntity?

    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    suspend fun todas(): List<VentaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(venta: VentaEntity): Long

    @Delete
    suspend fun eliminar(venta: VentaEntity)

    @Query("SELECT COUNT(*) FROM ventas")
    fun contar(): Flow<Int>

    @Query("SELECT COALESCE(SUM(total), 0) FROM ventas")
    fun totalVendido(): Flow<Double>
}
