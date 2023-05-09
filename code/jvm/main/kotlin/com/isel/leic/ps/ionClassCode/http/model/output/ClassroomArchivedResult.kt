package com.isel.leic.ps.ionClassCode.http.model.output

/**
 * Represents a Classroom Archived Result.
 * Can be either ClassroomArchived or ClassroomDeleted.
 */
sealed class ClassroomArchivedResult {
    object ClassroomArchived : ClassroomArchivedResult()
    object ClassroomDeleted : ClassroomArchivedResult()
}
