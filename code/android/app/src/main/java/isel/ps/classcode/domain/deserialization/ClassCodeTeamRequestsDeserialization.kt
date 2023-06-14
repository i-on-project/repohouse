package isel.ps.classcode.domain.deserialization

import com.fasterxml.jackson.annotation.JsonProperty
import isel.ps.classcode.http.hypermedia.SirenEntity

typealias ClassCodeTeamRequestDto = SirenEntity<ClassCodeTeamRequestsDeserialization>
val ClassCodeTeamRequestsType = SirenEntity.getType<ClassCodeTeamRequestsDeserialization>()

/**
 * Class used to deserialize the TeamRequests response from the database
 */

data class ClassCodeTeamRequestsDeserialization(
    @JsonProperty("needApproval") val needApproval: ClassCodeRequestsThatNeedApprovalDeserialization,
    @JsonProperty("requestsHistory") val requestsHistory: ClassCodeRequestsHistoryDeserialization,
)
