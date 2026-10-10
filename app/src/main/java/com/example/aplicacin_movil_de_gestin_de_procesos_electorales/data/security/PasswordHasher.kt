package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Componente de seguridad para el hash y verificación de contraseñas utilizando PBKDF2 con HMAC-SHA256.
 */
object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val DEFAULT_ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val SALT_SIZE = 16

    /**
     * Genera un hash seguro para la contraseña dada utilizando PBKDF2 con HMAC-SHA256 y una sal aleatoria.
     * Retorna una representación codificada con el formato: "iteraciones:salBase64:hashBase64".
     */
    fun hashPassword(password: CharArray, iterations: Int = DEFAULT_ITERATIONS): String {
        val random = SecureRandom()
        val salt = ByteArray(SALT_SIZE)
        random.nextBytes(salt)

        val spec = PBEKeySpec(password, salt, iterations, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        val hash = factory.generateSecret(spec).encoded

        val saltBase64 = Base64.getEncoder().encodeToString(salt)
        val hashBase64 = Base64.getEncoder().encodeToString(hash)

        return "$iterations:$saltBase64:$hashBase64"
    }

    /**
     * Compara de forma segura una contraseña en texto claro contra su hash almacenado.
     * Utiliza comparación de tiempo constante para evitar ataques de temporización.
     */
    fun verifyPassword(password: CharArray, storedHash: String): Boolean {
        if (storedHash.isBlank()) return false
        val parts = storedHash.split(":")
        if (parts.size != 3) return false

        val iterations = parts[0].toIntOrNull() ?: return false
        val salt = try {
            Base64.getDecoder().decode(parts[1])
        } catch (_: Exception) {
            return false
        }
        val expectedHash = try {
            Base64.getDecoder().decode(parts[2])
        } catch (_: Exception) {
            return false
        }

        val spec = PBEKeySpec(password, salt, iterations, expectedHash.size * 8)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        val actualHash = factory.generateSecret(spec).encoded

        return MessageDigest.isEqual(expectedHash, actualHash)
    }
}
