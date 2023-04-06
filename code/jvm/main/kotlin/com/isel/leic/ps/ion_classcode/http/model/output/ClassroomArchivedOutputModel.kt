package com.isel.leic.ps.ion_classcode.http.model.output

sealed class ClassroomArchivedModel {
    object ClassroomArchived : ClassroomArchivedModel()
    object ClassroomDeleted : ClassroomArchivedModel()
}


