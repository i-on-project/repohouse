package com.isel.leic.ps.ionClassCode.utils.cypher

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Class that decrypts a string using a simple AES algorithm.
 */
class AESDecrypt {

    companion object {

        private val encryptionKey: String = System.getenv("CLASSCODE_ENCRYPTION_KEY")
        private val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")

        /**
         * Decrypts a string.
         */
        fun decrypt(encryptedText: String): String {
            val key = SecretKeySpec(encryptionKey.toByteArray(), "AES")
            val iv = ByteArray(cipher.blockSize)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
            val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText))
            return String(decryptedBytes)
        }
    }
}
