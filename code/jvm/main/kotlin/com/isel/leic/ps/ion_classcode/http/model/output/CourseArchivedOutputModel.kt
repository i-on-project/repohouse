package com.isel.leic.ps.ion_classcode.http.model.output

sealed class CourseArchivedModel {
    object CourseArchived : CourseArchivedModel()
    object CourseDeleted : CourseArchivedModel()
}


