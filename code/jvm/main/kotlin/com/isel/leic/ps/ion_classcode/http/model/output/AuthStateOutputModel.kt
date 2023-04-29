package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.User

data class AuthStateOutputModel(
    val user: User,
    val authenticated: Boolean
)
