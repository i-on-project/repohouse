package com.isel.leic.ps.ionClassCode.infra

import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

/**
 * Represents a JsonHome document.
 */
data class JsonHome(
    val api: API,
    val resources: List<Resource>,
)

/**
 * Represents the API section of a JsonHome document.
 */
data class API(
    val title: String = "i-on ClassCode",
    val links: APILinks,
)

/**
 * Represents the links section of a JsonHome document.
 */
data class APILinks(
    val author: List<String> = listOf("A48309@alunos.isel.pt", "A48322@alunos.isel.pt", "A48348@alunos.isel.pt"),
    val describedBy: String = "https://github.com/i-on-project/repohouse/tree/main/docs",
)

/**
 * Represents a resource section of a JsonHome document.
 */
data class Resource(
    val tag: String,
    val hrefTemplate: String,
    val hints: Hints,
)

/**
 * Represents the hints section of a JsonHome document.
 */
data class Hints(
    val allow: List<String>,
    val format: String = "application/json"
)

/**
 * Represents a builder scope for a JsonHome document.
 */
class JsonHomeBuilderScope {

    private val api = API(links = APILinks())
    private val resources = mutableListOf<Resource>()

    fun resource(hrefTemplate: String, tag: String, methods: List<HttpMethod>) {
        resources.add(Resource(hrefTemplate, tag, Hints(allow = methods.map { it.name() })))
    }

    fun build(): JsonHome = JsonHome(api, resources)
}

/**
 * Builds a JsonHome document.
 */
fun jsonHome(block: JsonHomeBuilderScope.() -> Unit): ResponseEntity<JsonHome> {
    val scope = JsonHomeBuilderScope()
    scope.block()
    return ResponseEntity.ok(scope.build())
}
