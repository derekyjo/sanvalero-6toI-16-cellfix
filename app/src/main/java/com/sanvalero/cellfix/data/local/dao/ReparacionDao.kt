package com.sanvalero.cellfix.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sanvalero.cellfix.data.local.entity.ReparacionEntity
import com.sanvalero.cellfix.domain.model.ReparacionDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface ReparacionDao {

    @Query(
        "SELECT r.id AS id, r.falla AS falla, r.diagnostico AS diagnostico, r.estado AS estado, " +
            "r.fechaIngreso AS fechaIngreso, r.fechaEntrega AS fechaEntrega, r.total AS total, " +
            "e.marca AS marca, e.modelo AS modelo, e.imei AS imei, " +
            "c.nombre AS clienteNombre, t.nombre AS tecnicoNombre " +
            "FROM reparaciones r " +
            "INNER JOIN equipos e ON e.id = r.equipoId " +
            "INNER JOIN clientes c ON c.id = e.clienteId " +
            "INNER JOIN tecnicos t ON t.id = r.tecnicoId " +
            "ORDER BY r.fechaIngreso DESC"
    )
    fun observarTodas(): Flow<List<ReparacionDetalle>>

    @Query(
        "SELECT r.id AS id, r.falla AS falla, r.diagnostico AS diagnostico, r.estado AS estado, " +
            "r.fechaIngreso AS fechaIngreso, r.fechaEntrega AS fechaEntrega, r.total AS total, " +
            "e.marca AS marca, e.modelo AS modelo, e.imei AS imei, " +
            "c.nombre AS clienteNombre, t.nombre AS tecnicoNombre " +
            "FROM reparaciones r " +
            "INNER JOIN equipos e ON e.id = r.equipoId " +
            "INNER JOIN clientes c ON c.id = e.clienteId " +
            "INNER JOIN tecnicos t ON t.id = r.tecnicoId " +
            "WHERE r.falla LIKE '%' || :q || '%' OR r.estado LIKE '%' || :q || '%' " +
            "OR e.imei LIKE '%' || :q || '%' OR e.modelo LIKE '%' || :q || '%' " +
            "OR c.nombre LIKE '%' || :q || '%' " +
            "ORDER BY r.fechaIngreso DESC"
    )
    fun buscar(q: String): Flow<List<ReparacionDetalle>>

    @Query("SELECT * FROM reparaciones WHERE id = :id")
    suspend fun porId(id: Long): ReparacionEntity?

    @Query("SELECT * FROM reparaciones ORDER BY fechaIngreso DESC")
    suspend fun todas(): List<ReparacionEntity>

    @Query(
        "SELECT r.id AS id, r.falla AS falla, r.diagnostico AS diagnostico, r.estado AS estado, " +
            "r.fechaIngreso AS fechaIngreso, r.fechaEntrega AS fechaEntrega, r.total AS total, " +
            "e.marca AS marca, e.modelo AS modelo, e.imei AS imei, " +
            "c.nombre AS clienteNombre, t.nombre AS tecnicoNombre " +
            "FROM reparaciones r " +
            "INNER JOIN equipos e ON e.id = r.equipoId " +
            "INNER JOIN clientes c ON c.id = e.clienteId " +
            "INNER JOIN tecnicos t ON t.id = r.tecnicoId " +
            "ORDER BY r.fechaIngreso DESC"
    )
    suspend fun todasDetalle(): List<ReparacionDetalle>

    @Query("SELECT * FROM reparaciones WHERE equipoId = :equipoId ORDER BY fechaIngreso DESC")
    suspend fun porEquipo(equipoId: Long): List<ReparacionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(reparacion: ReparacionEntity): Long

    @Delete
    suspend fun eliminar(reparacion: ReparacionEntity)

    @Query("SELECT COUNT(*) FROM reparaciones")
    fun contar(): Flow<Int>

    @Query("SELECT COUNT(*) FROM reparaciones WHERE estado = :estado")
    fun contarPorEstado(estado: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(total), 0) FROM reparaciones WHERE estado = 'ENTREGADO'")
    fun totalEntregado(): Flow<Double>
}
