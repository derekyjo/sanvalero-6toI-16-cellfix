package com.sanvalero.cellfix.util

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Utilidades de hash de contraseñas. Nunca se guarda la contraseña en texto plano. */
object CryptoUtils {

    private const val ITERACIONES = 60_000
    private const val LONGITUD_CLAVE = 256

    fun nuevaSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun hash(password: String, salt: String): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            Base64.getDecoder().decode(salt),
            ITERACIONES,
            LONGITUD_CLAVE
        )
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val clave = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(clave)
    }

    fun verificar(password: String, salt: String, hashEsperado: String): Boolean {
        return hash(password, salt) == hashEsperado
    }
}
