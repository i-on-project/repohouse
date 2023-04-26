package com.isel.leic.ps.ion_classcode.http.model.output

/**
 * Represents a Course Archived Result.
 * Can be either CourseArchived or CourseDeleted.
 */
sealed class CourseArchivedResult {
    object CourseArchived : CourseArchivedResult()
    object CourseDeleted : CourseArchivedResult()
}
