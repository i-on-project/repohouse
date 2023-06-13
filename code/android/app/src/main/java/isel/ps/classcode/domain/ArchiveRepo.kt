package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeArchiveRepoDeserialization

/**
 * Represents a ClassCode Archive Repo
 */

data class ArchiveRepo(
    val requestId: Int,
    val creator: Int,
    val state: String,
    val composite: Int,
    val repoId: Int,
    val repoName: String,
) {
    constructor(deserialization: ClassCodeArchiveRepoDeserialization) : this (
        requestId = deserialization.requestId,
        creator = deserialization.creator,
        state = deserialization.state,
        composite = deserialization.composite,
        repoId = deserialization.repoId,
        repoName = deserialization.repoName,
    )
}
