package com.isel.leic.ps.ion_classcode.http.controllers.web

import com.isel.leic.ps.ion_classcode.domain.Student
import com.isel.leic.ps.ion_classcode.domain.Teacher
import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Status
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.input.TeachersPendingInputModel
import com.isel.leic.ps.ion_classcode.http.model.output.CourseOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuStudentOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuTeacherOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.OutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.TeachersPendingOutputModel
import com.isel.leic.ps.ion_classcode.http.model.problem.ErrorMessageModel
import com.isel.leic.ps.ion_classcode.http.model.problem.Problem
import com.isel.leic.ps.ion_classcode.http.services.StudentServices
import com.isel.leic.ps.ion_classcode.http.services.StudentServicesError
import com.isel.leic.ps.ion_classcode.http.services.TeacherServices
import com.isel.leic.ps.ion_classcode.http.services.TeacherServicesError
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.utils.Either
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MenuController(
    private val teacherServices: TeacherServices,
    private val studentServices: StudentServices,
) {

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

    private fun menuTeacher(
        user: User,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.stateMismatch
        return when (val courses = teacherServices.getCourses(user.id)) {
            is Either.Right -> siren(value = MenuTeacherOutputModel(user.name, user.email, courses.value.map { CourseOutputModel(it.id, it.orgUrl, it.name, it.teachers) })) {
                clazz("menu")
                link(rel = LinkRelation("self"), href = Uris.menuUri(), needAuthentication = true)
                link(rel = LinkRelation("credits"), href = Uris.creditsUri())
                link(rel = LinkRelation("logout"), href = Uris.logoutUri(), needAuthentication = true)
                link(rel = LinkRelation("createCourse"), href = Uris.coursesUri(), needAuthentication = true)
                link(rel = LinkRelation("teachersApproval"), href = Uris.teachersApprovalUri(), needAuthentication = true)
                courses.value.forEach {
                    link(rel = LinkRelation("course"), href = Uris.courseUri(it.id), needAuthentication = true)
                }
            }
            is Either.Left -> problemTeacher(courses.value)
        }
    }

    private fun menuStudent(
        user: User,
    ): ResponseEntity<*> {
        if (user !is Student) return Problem.stateMismatch
        val studentSchoolId = studentServices.getStudentSchoolId(user.id)
        if (studentSchoolId is Either.Left) return problemStudent(studentSchoolId.value)
        return when (val courses = studentServices.getCourses(user.id)) {
            is Either.Right -> siren(value = MenuStudentOutputModel(user.name, (studentSchoolId as Either.Right).value, user.email, courses.value.map { CourseOutputModel(it.id, it.orgUrl, it.name, it.teachers) })) {
                link(rel = LinkRelation("self"), href = Uris.menuUri(), needAuthentication = true)
                link(rel = LinkRelation("credits"), href = Uris.creditsUri())
                link(rel = LinkRelation("logout"), href = Uris.logoutUri(), needAuthentication = true)
                courses.value.forEach {
                    link(rel = LinkRelation("course"), href = Uris.courseUri(it.id), needAuthentication = true)
                }
            }
            is Either.Left -> problemStudent(courses.value)
        }
    }

    @GetMapping(Uris.TEACHERS_APPROVAL_PATH, produces = ["application/vnd.siren+json"])
    fun teachersApproval(
        user: User,
    ): ResponseEntity<*> {
        if (user !is Teacher) return Problem.stateMismatch
        return when (val teachers = teacherServices.getTeachersNeedingApproval()) {
            is Either.Right -> siren(value = TeachersPendingOutputModel(teachers.value)) {
                link(rel = LinkRelation("self"), href = Uris.teachersApprovalUri(), needAuthentication = true)
                link(rel = LinkRelation("menu"), href = Uris.menuUri(), needAuthentication = true)
                link(rel = LinkRelation("credits"), href = Uris.creditsUri())
                link(rel = LinkRelation("logout"), href = Uris.logoutUri(), needAuthentication = true)
                teachers.value.forEach {
                    action(
                        name = "approveTeacher",
                        href = Uris.teachersApprovalUri(),
                        method = HttpMethod.POST,
                        type = "application/x-www-form-urlencoded",
                        block = {
                            hiddenField(name = "teacherId", value = it.id.toString())
                        },
                    )
                }
            }

            is Either.Left -> problemTeacher(teachers.value)
        }
    }

    @PostMapping(Uris.TEACHERS_APPROVAL_PATH, produces = ["application/vnd.siren+json"])
    fun teacherApproved(
        input: TeachersPendingInputModel,
    ): ResponseEntity<OutputModel> {
        return when (teacherServices.approveTeachers(input)) {
            is Either.Right ->
                ResponseEntity
                    .status(Status.REDIRECT)
                    .header("Location", Uris.TEACHERS_APPROVAL_PATH)
                    .build()
            is Either.Left ->
                ResponseEntity
                    .status(Status.BAD_REQUEST)
                    .build()
        }
    }
    private fun problemStudent(error: StudentServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            is StudentServicesError.UserNotFound -> Problem.userNotFound
            is StudentServicesError.CourseNotFound -> Problem.courseNotFound
        }
    }

    private fun problemTeacher(error: TeacherServicesError): ResponseEntity<ErrorMessageModel> {
        return when (error) {
            is TeacherServicesError.CourseNotFound -> Problem.courseNotFound
            is TeacherServicesError.TeacherNotFound -> Problem.userNotFound
        }
    }
}
