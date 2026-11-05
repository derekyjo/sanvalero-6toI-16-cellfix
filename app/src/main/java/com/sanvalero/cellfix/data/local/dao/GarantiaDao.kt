package com.sanvalero.cellfix.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sanvalero.cellfix.data.local.entity.GarantiaEntity
import com.sanvalero.cellfix.domain.model.GarantiaDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface GarantiaDao {

    @Query(
        "SELECT g.id AS id, g.reparacionId AS reparacionId, g.fechaInicio AS fechaInicio, " +
            "g.fechaFin AS fechaFin, g.dias AS dias, g.observaciones AS observaciones, " +
            "e.imei AS imei, e.marca AS marca, e.modelo AS modelo, c.nombre AS clienteNombre " +
            "FROM garantias g " +
            "INNER JOIN reparaciones r ON r.id = g.reparacionId " +
            "INNER JOIN equipos e ON e.id = r.equipoId " +
            "INNER JOIN clientes c ON c.id = e.clienteId " +
            "ORDER BY g.fechaFin DESC"
    )
    fun observarTodas(): Flow<List<GarantiaDetalle>>

    @Query(
        "SELECT g.id AS id, g.reparacionId AS reparacionId, g.fechaInicio AS fechaInicio, " +
            "g.fechaFin AS fechaFin, g.dias AS dias, g.observaciones AS observaciones, " +
            "e.imei AS imei, e.marca AS marca, e.modelo AS modelo, c.nombre AS clienteNombre " +
            "FROM garantias g " +
            "INNER JOIN reparaciones r ON r.id = g.reparacionId " +
            "INNER JOIN equipos e ON e.id = r.equipoId " +
            "INNER JOIN clientes c ON c.id = e.clienteId " +
            "WHERE e.imei LIKE '%' || :imei || '%' " +
            "ORDER BY g.fechaFin DESC"
    )
    fun buscarPorImei(imei: String): Flow<List<GarantiaDetalle>>

    @Query("SELECT * FROM garantias WHERE reparacionId = :reparacionId LIMIT 1")
    suspend fun porReparacion(reparacionId: Long): GarantiaEntity?

    @Query("SELECT * FROM garantias ORDER BY fechaFin DESC")
    suspend fun todas(): List<GarantiaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(garantia: GarantiaEntity): Long

    @Delete
    suspend fun eliminar(garantia: GarantiaEntity)

    @Query("SELECT COUNT(*) FROM garantias WHERE fechaFin >= :ahora")
    fun contarActivas(ahora: Long): Flow<Int>
}
