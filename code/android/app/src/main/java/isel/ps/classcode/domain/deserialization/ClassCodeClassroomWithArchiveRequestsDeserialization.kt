package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity

typealias ClassCodeClassroomWithArchiveRequestsDto = SirenEntity<ClassCodeClassroomWithArchiveRequestsDeserialization>
val ClassCodeClassroomWithArchiveRequestsDtoType = SirenEntity.getType<ClassCodeClassroomWithArchiveRequestsDeserialization>()

/**
 * Class used to deserialize the ClassCodeClassroomWithAssignments response from the database
 */
data class ClassCodeClassroomWithArchiveRequestsDeserialization(
    @JsonProperty("classroomModel") val classroomModel: ClassCodeClassroomWithAssignmentsDeserialization,
    @JsonProperty("archiveRequest") val archiveRequest: List<ClassCodeArchiveRepoDeserialization>?,
    @JsonProperty("leaveClassrooms") val leaveClassrooms: List<ClassCodeLeaveClassroomRequestDeserialization>,

)
