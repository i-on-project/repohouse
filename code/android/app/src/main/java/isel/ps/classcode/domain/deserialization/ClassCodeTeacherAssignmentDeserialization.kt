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
    @JsonProperty("teamsCreated")val teamsCreated: List<ClassCodeTeamDeserialization>,
    @JsonProperty("createTeamComposites")val createTeamComposites: List<ClassCodeCreateTeamCompositeDeserialization>,
)
