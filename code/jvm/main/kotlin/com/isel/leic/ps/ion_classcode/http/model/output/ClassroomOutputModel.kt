package com.isel.leic.ps.ion_classcode.http.model.output

import com.isel.leic.ps.ion_classcode.domain.Assignment
import com.isel.leic.ps.ion_classcode.domain.Student
import java.io.File
import java.sql.Timestamp
import org.springframework.core.io.InputStreamResource

/**
 * Represents a Classroom Output Model.
 */
data class ClassroomOutputModel(
    val id: Int,
    val name: String,
    val isArchived: Boolean,
    val lastSync: Timestamp,
    val assignments: List<Assignment>,
    val students: List<Student>,
) : OutputModel

/**
 * Represents a Classroom Deleted Output Model.
 */
data class ClassroomDeletedOutputModel(
    val id: Int,
    val deleted: Boolean
) : OutputModel

/**
 * Represents a Classroom Model for inner functions.
 */
data class ClassroomModel(
    val id: Int,
    val name: String,
    val isArchived: Boolean,
    val lastSync: Timestamp,
    val assignments: List<Assignment>,
    val students: List<Student>,
) : OutputModel


data class LocalCopy(
    val fileName: String,
    val file:File
)
