package com.isel.leic.ps.ionClassCode.http.model.output

import com.isel.leic.ps.ionClassCode.domain.requests.JoinTeam
import com.isel.leic.ps.ionClassCode.domain.requests.LeaveTeam

/**
 * Represents a Request Output Model.
 */
data class RequestOutputModel(
    val status: Int,
    val id: Int,
    val title: String,
) : OutputModel

/**
 * Represents a Team Requests Output Model.
 */
data class TeamRequestsOutputModel(
    val joinTeam: List<JoinTeam>,
    val leaveTeam: List<LeaveTeam>,
) : OutputModel

/**
 * Represents a Request Created Output Model.
 */
data class RequestCreatedOutputModel(
    val id: Int,
    val created: Boolean,
) : OutputModel

/**
 * Represents a Request Change Status Output Model.
 */
data class RequestChangeStatusOutputModel(
    val id: Int,
    val changed: Boolean,
) : OutputModel
