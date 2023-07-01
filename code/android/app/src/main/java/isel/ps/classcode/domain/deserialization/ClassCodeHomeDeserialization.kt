package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity

typealias ClassCodeHomeDto = SirenEntity<ClassCodeHomeDeserialization>
val ClassCodeHomeType = SirenEntity.getType<ClassCodeHomeDeserialization>()

/**
 * Class used to deserialize the ClassCodeHome response from the database
 */

data class ClassCodeHomeDeserialization(
    @JsonProperty("title") val title: String = "i-on ClassCode",
    @JsonProperty("description") val description: String = "Easy academic collaboration: Create, manage, and share projects on GitHub with ease.",
    @JsonProperty("subDescription") val subDescription: String = "Perfect for faculty and students.",
    @JsonProperty("est") val est: String = "2023",
)
