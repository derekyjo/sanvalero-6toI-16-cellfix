package com.sanvalero.cellfix

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp activa Hilt en toda la app (inyección de dependencias)
@HiltAndroidApp
class CellFixApp : Application()