package isel.ps.classcode.http.utils

import isel.ps.classcode.domain.CallBackResponse


sealed class HandleRedirectClassCodeResponseError {
    class FailDeserialize(val error: String) : HandleRedirectClassCodeResponseError()
    class FailToGetTheLocation(val error: String) : HandleRedirectClassCodeResponseError()
    class FailFromClasscode(val error: CallBackResponse) : HandleRedirectClassCodeResponseError()
    class Fail(val error: String) : HandleRedirectClassCodeResponseError()
}