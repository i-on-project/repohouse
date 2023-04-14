package com.isel.leic.ps.ion_classcode.infra

import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

data class JsonHome(
    val api: API,
    val resources: List<Resource>,
)

data class API(
    val title: String = "i-on ClassCode",
    val links: APILinks,
)

data class APILinks(
    val author: List<String> = listOf("A48309@alunos.isel.pt", "A48322@alunos.isel.pt", "A48348@alunos.isel.pt"),
    val describedBy: String = "https://github.com/i-on-project/repohouse/tree/main/docs",
)

data class Resource(
    val hrefTemplate: String,
    val hints: Hints,
)

data class Hints(
    val allow: List<HttpMethod>,
    val format: String = "application/json"
)

class JsonHomeBuilderScope {

    private val api = API(links = APILinks())
    private val resources = mutableListOf<Resource>()

    fun resource(hrefTemplate: String, methods: List<HttpMethod>) {
        resources.add(Resource(hrefTemplate, Hints(allow = methods)))
    }

    fun build(): JsonHome = JsonHome(api, resources)
}

fun jsonHome(block: JsonHomeBuilderScope.() -> Unit): ResponseEntity<JsonHome> {
    val scope = JsonHomeBuilderScope()
    scope.block()
    return ResponseEntity.ok(scope.build())
}
