package isel.ps.classcode.presentation.classroom.services

import com.fasterxml.jackson.databind.ObjectMapper
import isel.ps.classcode.dataAccess.sessionStore.SessionStore
import okhttp3.OkHttpClient

class RealClassroomServices(private val sessionStore: SessionStore, private val objectMapper: ObjectMapper, private val httpClient: OkHttpClient): ClassroomServices {

}