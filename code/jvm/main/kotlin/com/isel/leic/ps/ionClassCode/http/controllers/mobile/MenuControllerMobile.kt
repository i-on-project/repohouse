package com.isel.leic.ps.ionClassCode.http.controllers.mobile

import com.isel.leic.ps.ionClassCode.domain.User
import com.isel.leic.ps.ionClassCode.http.Uris
import com.isel.leic.ps.ionClassCode.http.model.output.CourseOutputModel
import com.isel.leic.ps.ionClassCode.http.model.output.MenuTeacherOutputModel
import com.isel.leic.ps.ionClassCode.infra.LinkRelation
import com.isel.leic.ps.ionClassCode.infra.siren
import com.isel.leic.ps.ionClassCode.services.UserServices
import com.isel.leic.ps.ionClassCode.utils.Result
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
            is Result.Success -> siren(MenuTeacherOutputModel(name = user.name, email = user.email, courses = courses.value.map { CourseOutputModel(it.id, it.orgUrl, it.name, it.orgId, it.teachers) })) {
                clazz(value = "menu")
                link(rel = LinkRelation(value = "self"), href = Uris.MENU_PATH, needAuthentication = true)
            }
            is Result.Problem -> userServices.problem(error = courses.value)
        }
    }
}
