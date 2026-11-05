package com.sanvalero.cellfix.data.session

import com.sanvalero.cellfix.domain.model.Roles
import com.sanvalero.cellfix.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Guarda el usuario autenticado durante la sesión de la aplicación. */
@Singleton
class SessionManager @Inject constructor() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    fun iniciar(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun cerrar() {
        _usuario.value = null
    }

    fun esAdmin(): Boolean = _usuario.value?.rol == Roles.ADMIN
}
