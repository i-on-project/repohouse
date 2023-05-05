package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.TeachersPendingInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuStudentOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuTeacherOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeachersPendingOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.services.StudentServices
import com.isel.leic.ps.ion_classcode.services.TeacherServices
import com.isel.leic.ps.ion_classcode.services.UserServices
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Menu Controller
 */
@RestController
class MenuController(
    private val userServices: UserServices,
    private val teacherServices: TeacherServices,
    private val studentServices: StudentServices,
) {

    /**
     * Get all courses belonging to the user
     */
    @GetMapping(Uris.MENU_PATH, produces = ["application/vnd.siren+json"])
    fun menu(
        user: User,
    ): ResponseEntity<*> {
        return if (user is Student) {
            menuStudent(user)
        } else {
            menuTeacher(user)
        }
    }

    /**
     * Menu for teachers
     * Link to approve teachers
     */
    private fun menuTeacher(
        user: User,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.notTeacher
        return when (val courses = userServices.getAllUserCourses(user.id)) {
            is Result.Success -> siren(MenuTeacherOutputModel(user.name, user.email, courses.value.map { CourseOutputModel(it.id, it.orgUrl, it.name, it.teachers) })) {
                clazz("menu")
                link(rel = LinkRelation("self"), href = Uris.MENU_PATH, needAuthentication = true)
            }
            is Result.Problem -> userServices.problem(courses.value)
        }
    }

    /**
     * Menu for students
     */
    private fun menuStudent(
        user: User,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.notStudent
        val studentSchoolId = studentServices.getStudentSchoolId(user.id)
        if (studentSchoolId is Result.Problem) return studentServices.problem(studentSchoolId.value)
        return when (val courses = userServices.getAllUserCourses(user.id)) {
            is Result.Success -> siren(MenuStudentOutputModel(user.name, (studentSchoolId as Result.Success).value, user.email, courses.value.map { CourseOutputModel(it.id, it.orgUrl, it.name, it.teachers) })) {
                clazz("menu")
                link(rel = LinkRelation("self"), href = Uris.MENU_PATH, needAuthentication = true)
            }
            is Result.Problem -> userServices.problem(courses.value)
        }
    }

    /**
     * Get all teachers needing approval
     */
    @GetMapping(Uris.TEACHERS_APPROVAL_PATH, produces = ["application/vnd.siren+json"])
    fun teachersApproval(
        user: User,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.stateMismatch
        return when (val teachers = teacherServices.getTeachersNeedingApproval()) {
            is Result.Success -> siren(TeachersPendingOutputModel(teachers.value)) {
                clazz("teachersApproval")
                link(rel = LinkRelation("self"), href = Uris.TEACHERS_APPROVAL_PATH, needAuthentication = true)
            }
            is Result.Problem -> teacherServices.problem(teachers.value)
        }
    }

    /**
     * Approve teacher
     */
    @PostMapping(Uris.TEACHERS_APPROVAL_PATH, produces = ["application/vnd.siren+json"])
    fun teacherApproved(
        @RequestBody
        input: TeachersPendingInputModel,
    ): ResponseEntity<*> {
        return when (val approved = teacherServices.approveTeachers(input)) {
            is Result.Success -> siren(TeachersPendingOutputModel(approved.value)) {
                clazz("teachersApproval")
                action(title = "approveTeacher", href = Uris.TEACHERS_APPROVAL_PATH, method = HttpMethod.POST, type = "application/x-www-form-urlencoded", block = {
                    rangeField(name = "approved")
                    rangeField(name = "rejected")
                })
            }
            is Result.Problem -> teacherServices.problem(approved.value)
        }
    }
}
