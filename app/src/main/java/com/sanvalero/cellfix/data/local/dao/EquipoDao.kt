package com.sanvalero.cellfix.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sanvalero.cellfix.data.local.entity.EquipoEntity
import com.sanvalero.cellfix.domain.model.EquipoDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {

    @Query(
        "SELECT e.id AS id, e.clienteId AS clienteId, e.marca AS marca, e.modelo AS modelo, " +
            "e.imei AS imei, e.color AS color, c.nombre AS clienteNombre " +
            "FROM equipos e INNER JOIN clientes c ON c.id = e.clienteId " +
            "ORDER BY e.marca ASC, e.modelo ASC"
    )
    fun observarTodos(): Flow<List<EquipoDetalle>>

    @Query(
        "SELECT e.id AS id, e.clienteId AS clienteId, e.marca AS marca, e.modelo AS modelo, " +
            "e.imei AS imei, e.color AS color, c.nombre AS clienteNombre " +
            "FROM equipos e INNER JOIN clientes c ON c.id = e.clienteId " +
            "WHERE e.marca LIKE '%' || :q || '%' OR e.modelo LIKE '%' || :q || '%' " +
            "OR e.imei LIKE '%' || :q || '%' OR c.nombre LIKE '%' || :q || '%' " +
            "ORDER BY e.marca ASC, e.modelo ASC"
    )
    fun buscar(q: String): Flow<List<EquipoDetalle>>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun porId(id: Long): EquipoEntity?

    @Query("SELECT * FROM equipos WHERE imei = :imei LIMIT 1")
    suspend fun porImei(imei: String): EquipoEntity?

    @Query("SELECT * FROM equipos ORDER BY marca ASC")
    suspend fun todos(): List<EquipoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(equipo: EquipoEntity): Long

    @Delete
    suspend fun eliminar(equipo: EquipoEntity)

    @Query("SELECT COUNT(*) FROM equipos")
    fun contar(): Flow<Int>
}
