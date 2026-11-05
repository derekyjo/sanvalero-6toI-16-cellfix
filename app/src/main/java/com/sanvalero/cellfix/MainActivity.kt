package com.sanvalero.cellfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sanvalero.cellfix.ui.navigation.CellFixRoot
import com.sanvalero.cellfix.ui.theme.CellFixTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CellFixTheme {
                CellFixRoot()
            }
        }
    }
}
