package com.isel.leic.ps.ionClassCode.utils.cypher

import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Class that encrypts a string using a simple AES algorithm.
 */
class AESEncrypt {

    companion object {

        private val encryptionKey: String = System.getenv("CLASSCODE_ENCRYPTION_KEY")
        private val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")

        /**
         * Encrypts a string.
         */
        fun encrypt(stringToEncrypt: String): String {
            val key = SecretKeySpec(encryptionKey.toByteArray(), "AES")
            val iv = ByteArray(cipher.blockSize)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
            val encryptedBytes = cipher.doFinal(stringToEncrypt.toByteArray())
            return Base64.getEncoder().encodeToString(encryptedBytes)
        }

        /**
         * Encrypts an access token using a code.
         */
        fun encryptAccessToken(accessToken: String, code: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val keyBytes = digest.digest(code.toByteArray())
            val key = SecretKeySpec(keyBytes, "AES")
            val iv = ByteArray(cipher.blockSize)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
            val encryptedBytes = cipher.doFinal(accessToken.toByteArray())
            return Base64.getEncoder().encodeToString(encryptedBytes)
        }
    }
}
