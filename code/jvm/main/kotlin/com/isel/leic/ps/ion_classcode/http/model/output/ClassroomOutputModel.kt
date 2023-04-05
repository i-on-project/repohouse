package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.Assigment
import com.isel.leic.ps.ion_classcode.domain.Student
import java.sql.Timestamp

data class ClassroomOutputModel(
    val id: Int,
    val name: String,
    val isArchived: Boolean,
    val lastSync: Timestamp,
    val assigments: List<Assigment>,
    val students: List<Student>,
) : OutputModel

data class ClassroomDeletedOutputModel(
    val id: Int,
    val deleted: Boolean
) : OutputModel
