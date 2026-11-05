package com.sanvalero.cellfix.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sanvalero.cellfix.data.local.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Query("SELECT * FROM clientes ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<ClienteEntity>>

    @Query(
        "SELECT * FROM clientes WHERE nombre LIKE '%' || :q || '%' " +
            "OR cedula LIKE '%' || :q || '%' OR telefono LIKE '%' || :q || '%' " +
            "ORDER BY nombre ASC"
    )
    fun buscar(q: String): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun porId(id: Long): ClienteEntity?

    @Query("SELECT * FROM clientes ORDER BY nombre ASC")
    suspend fun todos(): List<ClienteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(cliente: ClienteEntity): Long

    @Delete
    suspend fun eliminar(cliente: ClienteEntity)

    @Query("SELECT COUNT(*) FROM clientes")
    fun contar(): Flow<Int>
}
