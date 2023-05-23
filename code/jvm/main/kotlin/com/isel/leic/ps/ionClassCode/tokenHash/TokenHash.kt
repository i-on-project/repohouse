package com.isel.leic.ps.ionClassCode.tokenHash

import java.security.MessageDigest
import java.util.Base64

/**
 * Interface for the token hash encryptor.
 */
interface TokenHash {

    fun getTokenHash(token: String): String
}

/**
 * Generic MAC hash encryptor.
 * @param algorithm cypher algorithm
 */
class GenericTokenHash(private val algorithm: String) : TokenHash {

    init {
        MessageDigest.getInstance(algorithm)
    }

    /**
     * Hashes the input string.
     */
    override fun getTokenHash(token: String): String = hash(token)

    /**
     * Method to hash a string.
     */
    private fun hash(input: String): String {
        val messageDigest = MessageDigest.getInstance(algorithm)
        return Base64.getUrlEncoder().encodeToString(
            messageDigest.digest(
                Charsets.UTF_8.encode(input).array()
            )
        )
    }
}
