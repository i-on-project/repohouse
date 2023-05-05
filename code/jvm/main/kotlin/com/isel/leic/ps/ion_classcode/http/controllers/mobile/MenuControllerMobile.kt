package com.isel.leic.ps.ion_classcode.http.controllers.mobile

import com.isel.leic.ps.ion_classcode.domain.User
import com.isel.leic.ps.ion_classcode.http.Uris
import com.isel.leic.ps.ion_classcode.http.model.output.CourseOutputModel
import com.isel.leic.ps.ion_classcode.http.model.output.MenuTeacherOutputModel
import com.isel.leic.ps.ion_classcode.infra.LinkRelation
import com.isel.leic.ps.ion_classcode.infra.siren
import com.isel.leic.ps.ion_classcode.services.UserServices
import com.isel.leic.ps.ion_classcode.utils.Result
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MenuControllerMobile(
    private val userServices: UserServices,
) {

    /**
     * Get all courses belonging to the user
     */
    @GetMapping(Uris.MOBILE_MENU_PATH, produces = ["application/vnd.siren+json"])
    fun menu(
        user: User,
    ): ResponseEntity<*> {
        return when (val courses = userServices.getAllUserCourses(userId = user.id)) {
            is Result.Success -> siren(MenuTeacherOutputModel(name = user.name, email = user.email, courses = courses.value.map { CourseOutputModel(it.id, it.orgUrl, it.name, it.teachers) })) {
                clazz(value = "menu")
                link(rel = LinkRelation(value = "self"), href = Uris.MENU_PATH, needAuthentication = true)
            }
            is Result.Problem -> userServices.problem(error = courses.value)
        }
    }
}
