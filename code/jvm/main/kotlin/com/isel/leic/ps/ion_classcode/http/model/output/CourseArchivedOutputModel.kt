package com.isel.leic.ps.ion_classcode.http.model.output

sealed class CourseArchivedOutputModel {
    object CourseArchived : CourseArchivedOutputModel()
    object CourseDeleted : CourseArchivedOutputModel()
}


