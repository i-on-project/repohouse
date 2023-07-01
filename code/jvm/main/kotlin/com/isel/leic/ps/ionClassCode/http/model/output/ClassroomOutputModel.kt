package com.isel.leic.ps.ionClassCode.http.model.output

import com.isel.leic.ps.ionClassCode.domain.Assignment
import com.isel.leic.ps.ionClassCode.domain.StudentWithoutToken
import com.isel.leic.ps.ionClassCode.domain.requests.ArchiveRepo
import java.io.File
import java.sql.Timestamp

/**
 * Represents a Classroom Output Model.
 */
data class ClassroomOutputModel(
    val id: Int,
    val name: String,
    val isArchived: Boolean,
    val lastSync: Timestamp,
    val inviteCode: String,
    val assignments: List<Assignment>,
    val students: List<StudentWithoutToken>,
) : OutputModel

/**
 * Represents a Classroom Archived or Deleted Output Model.
 */
data class ClassroomArchivedOrDeletedOutputModel(
    val id: Int,
    val archived: Boolean,
    val deleted: Boolean,
) : OutputModel

/**
 * Represents a Classroom Model for inner functions.
 */
data class ClassroomModel(
    val id: Int,
    val name: String,
    val isArchived: Boolean,
    val lastSync: Timestamp,
    val inviteCode: String,
    val assignments: List<Assignment>,
    val students: List<StudentWithoutToken>,
) : OutputModel

data class ClassroomModelWithArchiveRequest(
    val classroomModel: ClassroomModel,
    val archiveRequest: List<ArchiveRepo>?,
) : OutputModel

/**
 * Represents a Classroom Invite Model for inner functions.
 */
data class ClassroomInviteModel(
    val courseId: Int,
    val classroom: ClassroomModel,
) : OutputModel

/**
 * Represents a Classroom Invite Output Model.
 */
data class ClassroomInviteOutputModel(
    val courseId: Int,
    val classroom: ClassroomModel,
) : OutputModel

/**
 * Represents a Local Copy Output Model.
 */
data class LocalCopy(
    val fileName: String,
    val file: File,
)
