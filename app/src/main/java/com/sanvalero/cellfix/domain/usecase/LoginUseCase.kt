package com.sanvalero.cellfix.domain.usecase

import com.sanvalero.cellfix.domain.model.Usuario
import com.sanvalero.cellfix.domain.repository.CellFixRepository
import javax.inject.Inject

/** Valida credenciales y devuelve el usuario autenticado. */
class LoginUseCase @Inject constructor(
    private val repository: CellFixRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<Usuario> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Escribe el usuario y la contraseña."))
        }
        val usuario = repository.login(username, password)
            ?: return Result.failure(IllegalArgumentException("Usuario o contraseña incorrectos."))
        return Result.success(usuario)
    }
}
