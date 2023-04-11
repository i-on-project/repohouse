package com.isel.leic.ps.ion_classcode.http.model.output

/**
 * Represents a Classroom Archived Output Model.
 * Can be either ClassroomArchived or ClassroomDeleted.
 */
sealed class ClassroomArchivedOutputModel {
    object ClassroomArchived : ClassroomArchivedOutputModel()
    object ClassroomDeleted : ClassroomArchivedOutputModel()
}
