package com.isel.leic.ps.ion_classcode.domain.input

data class TagInput(
    val name: String,
    val isDelivered: Boolean,
    val tagDate: Int,
    val deliveryId: Int,
    val repoId: Int
) {
    init {
        require(name.isNotEmpty()) { "Name must not be empty" }
        require(tagDate > 0) { "Tag date must be greater than 0" }
        require(deliveryId > 0) { "Delivery id must be greater than 0" }
        require(repoId > 0) { "Repo id must be greater than 0" }
    }
}
