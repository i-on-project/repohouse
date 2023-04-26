package isel.ps.classcode.dataAccess

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

data class DecryptionSecret(val iv: String, val data: String)
data class EncryptionSecret(val data: String)
class CryptoManager {

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, getKey())
    }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false) // MUDAR ISTO PARA PRECISAR DE USER AUTHENTICATION
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(data: String): DecryptionSecret {
        val encryptedBytes = encryptCipher.doFinal(data.toByteArray())
        val encryptedBytes64 = base64Encoder.encodeToString(encryptedBytes)
        val iv64 = base64Encoder.encodeToString(encryptCipher.iv)
        return DecryptionSecret(iv = iv64, data = encryptedBytes64)
    }

    fun decrypt(decryptionSecret: DecryptionSecret): EncryptionSecret {
        val iv = base64Decoder.decode(decryptionSecret.iv)
        val data = base64Decoder.decode(decryptionSecret.data)
        val token = getDecryptCipherForIv(iv = iv).doFinal(data)
        return EncryptionSecret(data = String(token))
    }

    companion object {
        private val base64Encoder = Base64.getEncoder()
        private val base64Decoder = Base64.getDecoder()
        private const val ALIAS = "githubToken"

        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}