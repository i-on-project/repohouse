package isel.ps.classcode.presentation.login.services

import isel.ps.classcode.domain.AuthInfo
import isel.ps.classcode.presentation.utils.Either
import isel.ps.classcode.presentation.utils.GitHubResponseServicesError

/**
 * Represents the services that can be used on the login
 */
interface LoginServices {
    suspend fun tradeAndStoreAccessToken(code: String): Either<GitHubResponseServicesError, AuthInfo>
}