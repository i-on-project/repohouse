package com.isel.leic.ps.ion_repohouse.http.model.output

import java.net.URI

/**
 * Siren representation of HTTP responses.
 * @param cls class
 * @param properties specific response data
 * @param actions context related actions
 * @param links context related request links
 */
data class JsonOutputModel(
    val cls: List<String>,
    val properties: OutputModel,
    val actions: List<Action>,
    val links: List<Link>
)

/**
 * Generalization contract of all output models.
 */
interface OutputModel

/**
 * Generalizes common elements among actions and links in Siren representation.
 * @property href action or link uri
 * @property requiredAuthentication if said action or link operation requires authentication
 */
interface LinkOutputModel {
    val href: URI
    val requiredAuthentication: Boolean
}

data class Link(
    override val href: URI,
    val rel: List<String>,
    override val requiredAuthentication: Boolean
) : LinkOutputModel

data class Action(
    override val href: URI,
    val title: String,
    val method: MethodType,
    override val requiredAuthentication: Boolean,
    val fields: List<DataType>
) : LinkOutputModel

/**
 * Siren representation of parameters.
 * @property name parameter name
 * @property type parameter type
 */
interface DataType {
    val name: String
    val type: String
}

data class DataTypeWithoutValue(
    override val name: String,
    override val type: String
) : DataType

data class DataTypeWithValue(
    override val name: String,
    override val type: String,
    val value: String
) : DataType

/**
 * Represents all HTTP methods types.
 */
enum class MethodType {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH
}