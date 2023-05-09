package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity

typealias ClassCodeTeacherAssignmentDto = SirenEntity<ClassCodeTeacherAssignmentDeserialization>
val ClassCodeTeacherAssignmentDtoType = SirenEntity.getType<ClassCodeTeacherAssignmentDeserialization>()

/**
 * Class used to deserialize the ClassCodeTeacherAssignment response from the database
 */
data class ClassCodeTeacherAssignmentDeserialization(
    @JsonProperty("assignment")val assignment: ClassCodeAssignmentDeserialization,
    @JsonProperty("deliveries")val deliveries: List<ClassCodeDeliveryDeserialization>,
    @JsonProperty("teams")val teams: List<ClassCodeTeamDeserialization>
)
