package com.isel.leic.ps.ion_classcode.domain.input

import java.sql.Timestamp

/**
 * GitHub Tag Input Interface
 */
data class TagInput(
    val name: String,
    val isDelivered: Boolean,
    val tagDate: Timestamp,
    val deliveryId: Int,
    val repoId: Int
) {
    init {
        require(name.isNotEmpty()) { "Name must not be empty" }
        require(deliveryId > 0) { "Delivery id must be greater than 0" }
        require(repoId > 0) { "Repo id must be greater than 0" }
    }
}
