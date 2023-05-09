package isel.ps.classcode.dataAccess

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


enum class TypeOfData(val alias: String) {
    GITHUB_TOKEN(alias = "githubToken"),
    CLASSCODE_COOKIE(alias = "classcodeCookie")
}
/**
 * The class that will be used to encrypt and decrypt the token. Uses a androidKeyStore.
 * The key is generated if it doesn't exist, and if it exists it is used to encrypt and decrypt the token.
 * The algorithm used was AED, the block mode was CBC and the padding was PKCS7.
 * Importanto to notice, the key to be use need a user authentication.
 */

class CryptoManager {

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun encryptCipher(alias: String): Cipher =
        Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey(alias = alias))
    }

    private fun getDecryptCipherForIv(iv: ByteArray, alias: String): Cipher =
        Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(alias = alias), IvParameterSpec(iv))
    }

    private fun getKey(alias: String): SecretKey {
        val existingKey = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey(alias = alias)
    }

    private fun createKey(alias: String): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(true) // MUDAR ISTO PARA PRECISAR DE USER AUTHENTICATION
                    .setUserAuthenticationParameters(10000, KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(data: String, typeOfData: TypeOfData): DecryptionSecret {
        val encryptCipher = encryptCipher(alias = typeOfData.alias)
        val encryptedBytes = encryptCipher.doFinal(data.toByteArray())
        val encryptedBytes64 = base64Encoder.encodeToString(encryptedBytes)
        val iv64 = base64Encoder.encodeToString(encryptCipher.iv)
        return DecryptionSecret(iv = iv64, data = encryptedBytes64)
    }

    fun decrypt(decryptionSecret: DecryptionSecret, typeOfData: TypeOfData): EncryptionSecret {
        val iv = base64Decoder.decode(decryptionSecret.iv)
        val data = base64Decoder.decode(decryptionSecret.data)
        val token = getDecryptCipherForIv(iv = iv, alias = typeOfData.alias).doFinal(data)
        return EncryptionSecret(data = String(token))
    }

    companion object {
        private val base64Encoder = Base64.getEncoder()
        private val base64Decoder = Base64.getDecoder()
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}