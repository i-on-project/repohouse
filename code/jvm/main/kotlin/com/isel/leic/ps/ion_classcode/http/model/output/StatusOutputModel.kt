package com.isel.leic.ps.ion_classcode.http.model.output

/**
 * Represents a Status Output Model.
 */
data class StatusOutputModel(
    val statusInfo: String,
    val message: String
) : OutputModel
