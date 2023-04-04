package com.isel.leic.ps.ion_classcode.http.model.output

data class ErrorOutputModel(
    val state: Int,
    val error: String,
    val message: String
):OutputModel
