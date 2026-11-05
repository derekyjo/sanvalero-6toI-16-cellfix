package com.sanvalero.cellfix

import android.app.Application
import com.sanvalero.cellfix.domain.repository.CellFixRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CellFixApp : Application() {

    @Inject
    lateinit var repository: CellFixRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            runCatching { repository.seedIfEmpty() }
        }
    }
}
