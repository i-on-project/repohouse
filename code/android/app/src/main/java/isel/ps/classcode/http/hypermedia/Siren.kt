package isel.ps.classcode.http.hypermedia

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.type.TypeFactory
import okhttp3.MediaType.Companion.toMediaType
import java.net.URI


/**
 * For details regarding the Siren media type, see <a href="https://github.com/kevinswiber/siren">Siren</a>
 */

private const val APPLICATION_TYPE = "application"
private const val SIREN_SUBTYPE = "vnd.siren+json"

val SirenMediaType = "$APPLICATION_TYPE/$SIREN_SUBTYPE".toMediaType()

/**
 * Class whose instances represent links as they are represented in Siren.
 */
data class SirenLink(
    @JsonProperty("href") val href: URI,
    @JsonProperty("rel") val rel: List<String>,
    @JsonProperty("needAuthentication") val needAuthentication: Boolean? = null,
)

/**
 * Class whose instances represent actions that are included in a siren entity.
 */
data class SirenAction(
    val href: URI,
    val title: String? = null,
    val method: String? = null,
    val requiredAuthentication: Boolean? = null,
    val fields: List<Field>? = null
) {
    /**
     * Represents action's fields
     */
    data class Field(
        val name: String,
        val type: String? = null,
        val value: String? = null,
    )
}

data class SirenEntity<T>(
    @JsonProperty("class") val cls: List<String>? = null,
    @JsonProperty("properties") val properties: T,
    @JsonProperty("actions") val actions: List<SirenAction>? = null,
    @JsonProperty("links") val links: List<SirenLink>? = null,
    @JsonProperty("entities") val entities: List<SubEntity>? = null,
) {
    companion object {
        inline fun <reified T> getType() =
            TypeFactory.defaultInstance().constructParametricType(SirenEntity::class.java, T::class.java)
    }
}

/**
 * Base class for admissible sub entities, namely, [EmbeddedLink] and [EmbeddedEntity].
 * Notice that this is a closed class hierarchy.
 */
sealed class SubEntity

data class EmbeddedLink(
    val href: URI,
    val rel: List<String>,
    val requiredAuthentication: Boolean? = null,
) : SubEntity()

data class EmbeddedEntity<T>(
    val cls: List<String>? = null,
    val properties: T? =null,
    val actions: List<SirenAction>? = null,
    val links: List<SirenLink>? = null,
) : SubEntity() {
    companion object {
        inline fun <reified T> getType() = TypeFactory.defaultInstance().constructParametricType(EmbeddedEntity::class.java, T::class.java)
    }
}