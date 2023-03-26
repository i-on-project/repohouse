package com.isel.leic.ps.ion_classcode.repository.jdbi

import com.isel.leic.ps.ion_classcode.domain.input.CourseInput
import com.isel.leic.ps.ion_classcode.repository.CourseRepository
import org.jdbi.v3.core.Handle

class JdbiCourseRepository(private val handle: Handle): CourseRepository {
    override fun createCourse(course: CourseInput): Int {
        return handle.createUpdate(
            """
            INSERT INTO Course (name, org_url,teacher_id)
            VALUES (:name,:org_url,:teacher_id)
            RETURNING id
            """,
        )
            .bind("name", course.name)
            .bind("org_url", course.orgUrl)
            .bind("teacher_id", course.teacherId)
            .execute()
    }

    override fun deleteCourse(courseId: Int) {
        handle.createUpdate(
            """
            DELETE FROM Course
            WHERE id = :id
            """,
        )
            .bind("id", courseId)
            .execute()
    }

    override fun enterCourse(courseId: Int, studentId: Int) {
        handle.createUpdate(
            """
            INSERT INTO student_course (student,course)
            VALUES (:student_id,:course_id)
            """,
        )
            .bind("student_id", studentId)
            .bind("course_id", courseId)
            .execute()
    }

    override fun leaveCourse(courseId: Int, studentId: Int) {
        handle.createUpdate(
            """
            DELETE FROM student_course
            WHERE student = :student_id AND course = :course_id
            """,
        )
            .bind("student_id", studentId)
            .bind("course_id", courseId)
            .execute()
    }
}