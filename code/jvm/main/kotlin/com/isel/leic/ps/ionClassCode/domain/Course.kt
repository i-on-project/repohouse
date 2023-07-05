package com.isel.leic.ps.ionClassCode.domain

import com.isel.leic.ps.ionClassCode.domain.requests.LeaveCourse
import com.isel.leic.ps.ionClassCode.http.model.output.LeaveClassroomRequest

/**
 * Course Domain Interface
 */
data class Course(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val orgId: Long,
    val teachers: List<TeacherWithoutToken>,
    val isArchived: Boolean = false,
)

/**
 * Course with Classrooms included Domain Interface
 */
data class CourseWithClassrooms(
    val id: Int,
    val orgUrl: String,
    val name: String,
    val orgId: Long,
    val teachers: List<TeacherWithoutToken>,
    val isArchived: Boolean = false,
    val students: List<Student> = emptyList(),
    val classrooms: List<Classroom> = emptyList(),
)

data class LeaveCourseRequest(
    val leaveCourse: LeaveCourse,
    val leaveClassRoomRequests: List<LeaveClassroomRequest>,
)

data class CourseWithLeaveCourseRequests(
    val course: CourseWithClassrooms,
    val leaveCourseRequests: List<LeaveCourseRequest> = emptyList(),
)
