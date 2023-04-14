package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.requests.JoinTeam
import com.isel.leic.ps.ion_classcode.domain.requests.LeaveTeam

/**
 * Represents a Register Output Model.
 */
data class RegisterOutputModel(
    val name: String,
    val email: String,
    val GitHubUsername: String,
) : OutputModel
