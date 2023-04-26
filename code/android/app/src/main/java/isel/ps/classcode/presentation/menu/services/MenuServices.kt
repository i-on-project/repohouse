package isel.ps.classcode.presentation.menu.services

import isel.ps.classcode.domain.Course
import isel.ps.classcode.domain.UserInfo
import isel.ps.classcode.presentation.utils.ClassCodeResponseServicesError
import isel.ps.classcode.presentation.utils.Either
import isel.ps.classcode.presentation.utils.GitHubResponseServicesError

interface MenuServices {
    suspend fun getUserInfo(): Either<GitHubResponseServicesError, UserInfo>
    suspend fun getCourses(): Either<ClassCodeResponseServicesError, List<Course>>
    fun logout()
}