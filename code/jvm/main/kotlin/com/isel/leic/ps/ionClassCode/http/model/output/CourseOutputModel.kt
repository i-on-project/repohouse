package com.isel.leic.ps.ionClassCode.http.model.output

import com.isel.leic.ps.ionClassCode.domain.Classroom
import com.isel.leic.ps.ionClassCode.domain.Course
import com.isel.leic.ps.ionClassCode.domain.LeaveCourseRequest
import com.isel.leic.ps.ionClassCode.domain.TeacherWithoutToken

/**
 * Represents a Course Output Model.
 */
data class CourseOutputModel(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val orgId: Long,
    val teacher: List<TeacherWithoutToken>,
) : OutputModel

/**
 * Represents a Course with the respective classrooms included Output Model.
 */
data class CourseWithClassroomOutputModel(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val teacher: List<TeacherWithoutToken>,
    val isArchived: Boolean,
    val classrooms: List<Classroom>,
) : OutputModel

data class CourseWithLeaveCourseRequestsOutputModel(
    val course: CourseWithClassroomOutputModel,
    val leaveCourseRequests: List<LeaveCourseRequest> = emptyList()
) : OutputModel

/**
 * Represents a Course Created Output Model.
 */
data class CourseCreatedOutputModel(
    val course: Course,
)

/**
 * Represents a Course Deleted Output Model.
 */
data class CourseDeletedOutputModel(
    val id: Int,
    val deleted: Boolean
) : OutputModel
