package com.isel.leic.ps.ion_classcode.http.model.output

/**
 * Represents a Course Archived Output Model.
 * Can be either CourseArchived or CourseDeleted.
 */
sealed class CourseArchivedModel {
    object CourseArchived : CourseArchivedModel()
    object CourseDeleted : CourseArchivedModel()
}


