package com.isel.leic.ps.ionClassCode.tokenHash

import java.security.MessageDigest
import java.util.Base64

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

    override fun getTokenHash(token: String): String = hash(token)

    private fun hash(input: String): String {
        val messageDigest = MessageDigest.getInstance(algorithm)
        return Base64.getUrlEncoder().encodeToString(
            messageDigest.digest(
                Charsets.UTF_8.encode(input).array()
            )
        )
    }
}
