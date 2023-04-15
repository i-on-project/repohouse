package com.isel.leic.ps.ion_classcode.infra

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import java.net.URI
import java.sql.Timestamp

/**
 * This code was made by the teacher Pedro Felix in DAW class.
 * https://github.com/isel-leic-daw/s2223i-51d-51n-public/blob/main/code/tic-tac-tow-service/src/main/kotlin/pt/isel/daw/tictactow/infra/Siren.kt
 */
data class SirenModel<T>(
    @get:JsonProperty("class")
    val clazz: List<String>,
    val properties: T,
    val links: List<LinkModel>,
    val entities: List<EntityModel<*>>,
    val actions: List<ActionModel>,
)

data class LinkModel(
    val rel: List<String>,
    val href: String,
    val needAuthentication: Boolean,
)

data class EntityModel<T>(
    val properties: T,
    val links: List<LinkModel>,
    val rel: List<String>,
)

data class ActionModel(
    val title: String,
    val href: String,
    val method: String,
    val type: String,
    val fields: List<FieldModel>,
)

data class FieldModel(
    val name: String,
    val type: String,
    val value: Any? = null,
)

class SirenBuilderScope<T>(
    private val properties: T,
) {
    private val links = mutableListOf<LinkModel>()
    private val entities = mutableListOf<EntityModel<*>>()
    private val classes = mutableListOf<String>()
    private val actions = mutableListOf<ActionModel>()

    fun clazz(value: String) {
        classes.add(value)
    }

    fun link(href: String, rel: LinkRelation, needAuthentication: Boolean = false) {
        links.add(
            LinkModel(
                rel = listOf(rel.value),
                href = href,
                needAuthentication = needAuthentication,
            ),
        )
    }

    fun <U> entity(value: U, rel: LinkRelation, block: EntityBuilderScope<U>.() -> Unit) {
        val scope = EntityBuilderScope(properties = value, rel = listOf(rel.value))
        scope.block()
        entities.add(scope.build())
    }

    fun action(title: String, href: String, method: HttpMethod, type: String, block: ActionBuilderScope.() -> Unit) {
        val scope = ActionBuilderScope(name = title, hrefTemplate = href, method = method, type = type)
        scope.block()
        actions.add(scope.build())
    }

    fun build(): SirenModel<T> = SirenModel(
        clazz = classes,
        properties = properties,
        links = links,
        entities = entities,
        actions = actions,
    )
}

class EntityBuilderScope<T>(
    val properties: T,
    val rel: List<String>,
) {
    private val links = mutableListOf<LinkModel>()

    fun link(href: URI, rel: LinkRelation, needAuthentication: Boolean = false) {
        links.add(
            LinkModel(
                rel = listOf(rel.value),
                href = href.toASCIIString(),
                needAuthentication = needAuthentication,
            ),
        )
    }

    fun build(): EntityModel<T> = EntityModel(
        properties = properties,
        links = links,
        rel = rel,
    )
}

class ActionBuilderScope(
    private val name: String,
    private val hrefTemplate: String,
    private val method: HttpMethod,
    private val type: String,
) {
    private val fields = mutableListOf<FieldModel>()

    fun textField(name: String, value: String? = null) {
        fields.add(FieldModel(name, "text", value))
    }

    fun timestampField(name: String, value: Timestamp? = null) {
        fields.add(FieldModel(name, "timestamp", value))
    }

    fun numberField(name: String, value: Int? = null) {
        fields.add(FieldModel(name, "number", value))
    }

    fun rangeField(name: String, value: Any? = null) {
        fields.add(FieldModel(name, "range", value))
    }

    fun hiddenField(name: String, value: Any? = null) {
        fields.add(FieldModel(name, "hidden", value))
    }

    fun build() = ActionModel(name, hrefTemplate, method.name(), type, fields)
}

fun <T> siren(value: T, block: SirenBuilderScope<T>.() -> Unit): ResponseEntity<SirenModel<T>> {
    val scope = SirenBuilderScope(value)
    scope.block()
    return ResponseEntity.ok(scope.build())
}
