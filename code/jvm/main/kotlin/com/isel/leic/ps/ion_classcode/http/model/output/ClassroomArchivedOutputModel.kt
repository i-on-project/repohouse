package com.isel.leic.ps.ion_classcode.http.model.output

sealed class ClassroomArchivedOutputModel {
    object ClassroomArchived : ClassroomArchivedOutputModel()
    object ClassroomDeleted : ClassroomArchivedOutputModel()
}


