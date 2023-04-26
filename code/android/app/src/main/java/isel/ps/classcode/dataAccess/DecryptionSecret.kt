package isel.ps.classcode.dataAccess

/**
 * The class that represents the data encrypted and the iv that was generated to encrypt the data.
 */
data class DecryptionSecret(val iv: String, val data: String)