package com.sanvalero.cellfix.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sanvalero.cellfix.data.local.entity.TecnicoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TecnicoDao {

    @Query("SELECT * FROM tecnicos ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<TecnicoEntity>>

    @Query("SELECT * FROM tecnicos WHERE activo = 1 ORDER BY nombre ASC")
    fun observarActivos(): Flow<List<TecnicoEntity>>

    @Query("SELECT * FROM tecnicos WHERE id = :id")
    suspend fun porId(id: Long): TecnicoEntity?

    @Query("SELECT * FROM tecnicos ORDER BY nombre ASC")
    suspend fun todos(): List<TecnicoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(tecnico: TecnicoEntity): Long

    @Delete
    suspend fun eliminar(tecnico: TecnicoEntity)

    @Query("SELECT COUNT(*) FROM tecnicos")
    fun contar(): Flow<Int>
}
