package com.sanvalero.cellfix.ui.session

import androidx.lifecycle.ViewModel
import com.sanvalero.cellfix.data.session.SessionManager
import com.sanvalero.cellfix.domain.model.Usuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val session: SessionManager
) : ViewModel() {

    val usuario: StateFlow<Usuario?> = session.usuario

    fun salir() {
        session.cerrar()
    }
}
