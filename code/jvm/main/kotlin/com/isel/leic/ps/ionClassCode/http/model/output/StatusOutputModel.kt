package com.isel.leic.ps.ionClassCode.http.model.output

/**
 * Represents a Status Output Model.
 */
data class StatusOutputModel(
    val statusInfo: String,
    val message: String,
) : OutputModel
