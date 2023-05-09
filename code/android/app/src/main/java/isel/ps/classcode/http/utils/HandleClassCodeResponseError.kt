package isel.ps.classcode.http.utils

import isel.ps.classcode.domain.ProblemJson

/**
 * Represents the possible errors that can occur when requesting the ClassCode API
 */
sealed class HandleClassCodeResponseError {
    class FailDeserialize(val error: String) : HandleClassCodeResponseError()
    class FailRequest(val error: ProblemJson) : HandleClassCodeResponseError()
    class FailToGetTheHeader(val error: String) : HandleClassCodeResponseError()
}