package com.isel.leic.ps.ionClassCode.repository.jdbi

import com.isel.leic.ps.ionClassCode.domain.Classroom
import com.isel.leic.ps.ionClassCode.domain.Course
import com.isel.leic.ps.ionClassCode.domain.Student
import com.isel.leic.ps.ionClassCode.domain.TeacherWithoutToken
import com.isel.leic.ps.ionClassCode.domain.dto.CourseDTO
import com.isel.leic.ps.ionClassCode.domain.input.CourseInput
import com.isel.leic.ps.ionClassCode.repository.CourseRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Implementation of the Course methods
 */
class JdbiCourseRepository(private val handle: Handle) : CourseRepository {

    /**
     * Method to check if Course exists by is organization url
     */
    override fun checkIfOrgUrlExists(orgUrl: String): Boolean {
        return handle.createQuery(
            """
            SELECT id FROM Course
            WHERE org_url = :org_url
            """,
        )
            .bind("org_url", orgUrl)
            .mapTo<Int>()
            .firstOrNull() != null
    }

    /**
     * Method to check if Course exists by the name
     */
    override fun checkIfCourseNameExists(name: String): Boolean {
        return handle.createQuery(
            """
            SELECT id FROM Course
            WHERE name = :name
            """,
        )
            .bind("name", name)
            .mapTo<Int>()
            .firstOrNull() != null
    }

    /**
     * Method to create a Course
     */
    override fun createCourse(course: CourseInput): Course {
        val id = handle.createUpdate(
            """
            INSERT INTO Course (name, org_url, org_id)
            VALUES (:name,:org_url,:org_id)
            RETURNING id
            """,
        )
            .bind("name", course.name)
            .bind("org_url", course.orgUrl)
            .bind("org_id", course.orgId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .first()
        val teacher = handle.createQuery(
            """
                SELECT u.name, u.email, t.id, u.github_username, u.github_id, u.is_created FROM Teacher as t JOIN users u on t.id = u.id
                WHERE t.id = :teacher_id
            """,
        )
            .bind("teacher_id", course.teacherId)
            .mapTo<TeacherWithoutToken>()
            .first()
        return Course(id, course.orgUrl, course.name, course.orgId, listOf(teacher))
    }

    /**
     * Method to delete a Course
     */
    override fun deleteCourse(courseId: Int) {
        handle.createUpdate(
            """
            DELETE FROM teacher_course
            WHERE course = :id
            """,
        )
            .bind("id", courseId)
            .execute()

        handle.createUpdate(
            """
                DELETE FROM Course
            WHERE id = :id
            """,
        )
            .bind("id", courseId)
            .execute()
    }

    /**
     * Method to leave a Course
     */
    override fun leaveCourse(courseId: Int, studentId: Int): Course {
        handle.createUpdate(
            """
            DELETE FROM student_classroom
            WHERE student = :student_id AND classroom IN (SELECT id FROM classroom WHERE course_id = :course_id)
            """,
        )
            .bind("student_id", studentId)
            .bind("course_id", courseId)
            .execute()

        return getTheCourse(courseId = courseId)
    }

    /**
     * Method to archieve a Course
     */
    override fun archiveCourse(courseId: Int) {
        handle.createUpdate(
            """
            UPDATE Course
            SET is_archived = true
            WHERE id = :course_id
            """,
        )
            .bind("course_id", courseId)
            .execute()

        handle.createUpdate(
            """
            UPDATE classroom
            SET is_archived = true
            WHERE course_id = :course_id
            """,
        )
            .bind("course_id", courseId)
            .execute()
    }

    /**
     * Method to add a teacher to a Course
     */
    override fun addTeacherToCourse(teacherId: Int, courseId: Int): Course {
        handle.createUpdate(
            """
            INSERT INTO teacher_course (teacher,course)
            VALUES (:teacher_id,:course_id)
            """,
        )
            .bind("teacher_id", teacherId)
            .bind("course_id", courseId)
            .execute()

        return getTheCourse(courseId = courseId)
    }

    /**
     * Method to get all teachers from a Course
     */
    override fun getCourseTeachers(courseId: Int): List<TeacherWithoutToken> {
        val teachersIds = handle.createQuery(
            """
            SELECT teacher FROM teacher_course
            WHERE course = :course_id
            """,
        )
            .bind("course_id", courseId)
            .mapTo<Int>()
            .list()
        return teachersIds.map { id ->
            handle.createQuery(
                """
                SELECT users.name, email, Users.id, github_username, github_id, is_created FROM Teacher
                JOIN users on teacher.id = users.id
                WHERE teacher.id = :teacher_id
                """,
            )
                .bind("teacher_id", id)
                .mapTo<TeacherWithoutToken>()
                .first()
        }
    }

    /**
     * Method to get all classrooms from a Course
     */
    override fun getCourseAllClassrooms(courseId: Int): List<Classroom> {
        return handle.createQuery(
            """
                SELECT * FROM classroom
                WHERE course_id = :course_id
            """,
        )
            .bind("course_id", courseId)
            .mapTo<Classroom>()
            .list()
    }

    /**
     * Method to get all classrooms from a user in a Course
     */
    override fun getCourseUserClassrooms(courseId: Int, userId: Int, student: Boolean): List<Classroom> {
        return if (student) {
            handle.createQuery(
                """
                SELECT classroom.id,classroom.name,classroom.last_sync,classroom.invite_code,classroom.is_archived,course_id,classroom.teacher_id FROM classroom
                JOIN student_classroom ON classroom.id = student_classroom.classroom
                WHERE student_classroom.student = :user_id AND classroom.course_id = :course_id
            """,
            )
                .bind("course_id", courseId)
                .bind("user_id", userId)
                .mapTo<Classroom>()
                .list()
        } else {
            handle.createQuery(
                """
                    SELECT classroom.id,classroom.name,classroom.last_sync,classroom.invite_code,classroom.is_archived,course_id,classroom.teacher_id FROM classroom
                    WHERE classroom.teacher_id = :user_id AND classroom.course_id = :course_id
                """,
            )
                .bind("course_id", courseId)
                .bind("user_id", userId)
                .mapTo<Classroom>()
                .list()
        }
    }

    /**
     * Method to get all Courses by a teacher
     */
    override fun getAllTeacherCourses(userId: Int): List<Course> {
        val dto = handle.createQuery(
            """
            SELECT distinct id, org_url, name, org_id, (
                SELECT array(SELECT teacher FROM teacher_course WHERE course = Course.id) as teachers
                ), is_archived FROM teacher_course JOIN course ON course.id = teacher_course.course
                WHERE teacher_course.teacher = :teacher_id
            """,
        )
            .bind("teacher_id", userId)
            .mapTo<CourseDTO>()
            .list()
        return dto.map { courseTemp ->
            val teachers = courseTemp.teachers.map { teacherId ->
                handle.createQuery(
                    """
                    SELECT users.name, email, Users.id, github_username, github_id, is_created FROM Teacher
                    JOIN users on teacher.id = users.id
                    WHERE teacher.id = :teacher_id
                    """,
                )
                    .bind("teacher_id", teacherId)
                    .mapTo<TeacherWithoutToken>()
                    .first()
            }
            Course(id = courseTemp.id, orgUrl = courseTemp.orgUrl, name = courseTemp.name, teachers = teachers, orgId = courseTemp.orgId, isArchived = courseTemp.isArchived)
        }
    }

    /**
     * Method to get all Courses by a student
     */
    override fun getAllStudentCourses(userId: Int): List<Course> {
        val dto = handle.createQuery(
            """
                SELECT distinct course.id, org_url, course.name, org_id, (
                    SELECT array(
                        SELECT teacher 
                        FROM teacher_course 
                        WHERE course = Course.id) as teachers
                    ), course.is_archived 
                FROM course 
                JOIN classroom c on course.id = c.course_id 
                JOIN student_classroom sc on c.id = sc.classroom 
                WHERE sc.student = :student_id
            """,
        )
            .bind("student_id", userId)
            .mapTo<CourseDTO>()
            .list()
        return dto.map { courseTemp ->
            val teachers = courseTemp.teachers.map { teacherId ->
                handle.createQuery(
                    """
                    SELECT users.name, email, Users.id, github_username, github_id, is_created FROM Teacher
                    JOIN users on teacher.id = users.id
                    WHERE teacher.id = :teacher_id
                    """,
                )
                    .bind("teacher_id", teacherId)
                    .mapTo<TeacherWithoutToken>()
                    .first()
            }
            Course(id = courseTemp.id, orgUrl = courseTemp.orgUrl, name = courseTemp.name, teachers = teachers, orgId = courseTemp.orgId, isArchived = courseTemp.isArchived)
        }
    }

    /**
     * Method to get all students in a Course
     */
    override fun getStudentInCourse(courseId: Int): List<Student> {
        return handle.createQuery(
            """
            SELECT users.name, users.email, users.id, users.github_username, users.github_id,users.is_created,token FROM users
            JOIN student_classroom on users.id = student_classroom.student
            JOIN classroom on student_classroom.classroom = classroom.id
            JOIN course on classroom.course_id = course.id
            WHERE course.id = :course_id
            """,
        )
            .bind("course_id", courseId)
            .mapTo<Student>()
            .list()
    }

    /**
     * Method to get a Course by is id
     */
    override fun getCourse(courseId: Int): Course? {
        val dto = handle.createQuery(
            """
            SELECT c.id, c.org_url, c.name, c.org_id, (
            SELECT array(SELECT teacher FROM teacher_course WHERE course = c.id) as teachers
            ), is_archived
            FROM course AS c
            WHERE id = :course_id
            """,
        )
            .bind("course_id", courseId)
            .mapTo<CourseDTO>()
            .firstOrNull() ?: return null
        val teachers = dto.teachers.map { teacherId ->
            handle.createQuery(
                """
                SELECT u.name, email, u.id, github_username, github_id, is_created FROM Teacher as t
                JOIN users as u on t.id = u.id
                WHERE t.id = :teacher_id
                """,
            )
                .bind("teacher_id", teacherId)
                .mapTo<TeacherWithoutToken>()
                .first()
        }
        return Course(id = dto.id, orgUrl = dto.orgUrl, name = dto.name, teachers = teachers, orgId = dto.orgId, isArchived = dto.isArchived)
    }

    /**
     * Method to get a Course by is organization url
     */
    override fun getCourseByOrg(orgUrl: String): Course? {
        val dto = handle.createQuery(
            """
                SELECT c.id, c.org_url, c.name, c.org_id, (
                SELECT array(SELECT teacher FROM teacher_course WHERE course = c.id) as teachers
                ), is_archived
                FROM course AS c
                WHERE org_url = :orgUrl
                """,
        )
            .bind("orgUrl", orgUrl)
            .mapTo<CourseDTO>()
            .firstOrNull() ?: return null
        val teachers = getCourseTeachers(courseId = dto.id)
        return Course(id = dto.id, orgUrl = dto.orgUrl, name = dto.name, teachers = teachers, orgId = dto.orgId, isArchived = dto.isArchived)
    }

    /**
     * Method to get a Course by the name
     */
    override fun getCourseByName(name: String): Course? {
        val dto = handle.createQuery(
            """
                SELECT c.id, c.org_url, c.name, c.org_id,(
                SELECT array(SELECT teacher FROM teacher_course WHERE course = c.id) as teachers
                ), is_archived
                FROM course AS c
                WHERE name = :name
                """,
        )
            .bind("name", name)
            .mapTo<CourseDTO>()
            .firstOrNull() ?: return null
        val teachers = getCourseTeachers(courseId = dto.id)
        return Course(id = dto.id, orgUrl = dto.orgUrl, name = dto.name, teachers = teachers, orgId = dto.orgId, isArchived = dto.isArchived)
    }

    /**
     * Method to check if a student is in a course
     */
    override fun isStudentInCourse(studentId: Int, courseId: Int): Boolean =
        handle.createQuery(
            """
            SELECT student FROM student_classroom
            JOIN classroom on student_classroom.classroom = classroom.id
            JOIN course on classroom.course_id = course.id
            WHERE course.id = :courseId AND student_classroom.student = :studentId
            """,
        )
            .bind("studentId", studentId)
            .bind("courseId", courseId)
            .mapTo<Int>()
            .firstOrNull() != null

    /**
     * Method to get a Course by is id
     */
    private fun getTheCourse(courseId: Int): Course {
        val dto = handle.createQuery(
            """
                SELECT c.id, c.org_url, c.name, c.org_id,(
                SELECT array(SELECT teacher FROM teacher_course WHERE course = c.id) as teachers
                ), is_archived
                FROM course AS c
                WHERE id = :course_id
                """,
        )
            .bind("course_id", courseId)
            .mapTo<CourseDTO>()
            .first()
        val teachers = getCourseTeachers(courseId = dto.id)
        return Course(id = dto.id, orgUrl = dto.orgUrl, name = dto.name, teachers = teachers, orgId = dto.orgId, isArchived = dto.isArchived)
    }
}
