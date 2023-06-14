package isel.ps.classcode.domain

import isel.ps.classcode.domain.deserialization.ClassCodeCreateTeamCompositeDeserialization

/**
 * Represents a ClassCode Create Team Composite
 */

data class CreateTeamComposite(
    val compositeState: String,
    val createTeam: CreateTeam,
    val joinTeam: JoinTeam,
    val createRepo: CreateRepo,
) {
    constructor(deserialization: ClassCodeCreateTeamCompositeDeserialization) : this(
        compositeState = deserialization.compositeState,
        createTeam = CreateTeam(deserialization = deserialization.createTeam),
        joinTeam = JoinTeam(deserialization = deserialization.joinTeam),
        createRepo = CreateRepo(deserialization = deserialization.createRepo),
    )
}
