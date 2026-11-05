package com.sanvalero.cellfix.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sanvalero.cellfix.data.local.AppDatabase
import com.sanvalero.cellfix.data.local.dao.ClienteDao
import com.sanvalero.cellfix.data.local.dao.DetalleVentaDao
import com.sanvalero.cellfix.data.local.dao.EquipoDao
import com.sanvalero.cellfix.data.local.dao.GarantiaDao
import com.sanvalero.cellfix.data.local.dao.ReparacionDao
import com.sanvalero.cellfix.data.local.dao.TecnicoDao
import com.sanvalero.cellfix.data.local.dao.UsuarioDao
import com.sanvalero.cellfix.data.local.dao.VentaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "cellfix.db")
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUsuarioDao(db: AppDatabase): UsuarioDao = db.usuarioDao()

    @Provides
    fun provideClienteDao(db: AppDatabase): ClienteDao = db.clienteDao()

    @Provides
    fun provideEquipoDao(db: AppDatabase): EquipoDao = db.equipoDao()

    @Provides
    fun provideTecnicoDao(db: AppDatabase): TecnicoDao = db.tecnicoDao()

    @Provides
    fun provideReparacionDao(db: AppDatabase): ReparacionDao = db.reparacionDao()

    @Provides
    fun provideGarantiaDao(db: AppDatabase): GarantiaDao = db.garantiaDao()

    @Provides
    fun provideVentaDao(db: AppDatabase): VentaDao = db.ventaDao()

    @Provides
    fun provideDetalleVentaDao(db: AppDatabase): DetalleVentaDao = db.detalleVentaDao()
}
