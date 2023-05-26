import { SirenEntity } from "../../http/Siren"
import { TeamDomain } from "../Team"
import { Student } from "../User"
import { AssignmentDomain } from "../Assignment"
import { RepoDomain } from "../Repo"
import { FeedbackDomain } from "../Feedback"

export type TeamDto = SirenEntity<TeamDtoProperties>
export type TeamsDto = SirenEntity<TeamsDtoProperties>
export type TeamClosedDto = SirenEntity<TeamClosedDtoProperties>

export interface TeamDtoProperties{
    team: TeamDomain,
    students: Student[],
    repo: RepoDomain,
    feedbacks: FeedbackDomain[],
    assignment:AssignmentDomain,
}

export interface TeamsDtoProperties{
    teams: TeamDtoProperties[],
}

export class TeamDtoProperties {
    constructor(
        team: TeamDomain,
        students: Student[],
        repo: RepoDomain,
        feedbacks: FeedbackDomain[],
        assignment:AssignmentDomain,
    ) {
        this.team = team
        this.students = students
        this.repo = repo
        this.feedbacks = feedbacks
        this.assignment = assignment
    }
}

export class TeamsDtoProperties {
    constructor(
        teams: TeamDtoProperties[],
    ) {
        this.teams = teams
    }
}

export interface TeamClosedDtoProperties{
    closed: boolean
}

export class TeamClosedDtoProperties {
    constructor(
        closed: boolean
    ) {
        this.closed = closed
    }
}

export interface FeedbackBody {
    label: string,
    description: string,
    teamId: number,
}

export class FeedbackBody {
    constructor(
        label: string,
        description: string,
        teamId: number,
    ) {
        this.label = label
        this.description = description
        this.teamId = teamId
    }
}
