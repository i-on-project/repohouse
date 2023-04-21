import {SirenEntity} from "../../siren/Siren";
import {TeamDomain} from "../Team";
import {Student} from "../User";
import {RepoDomain} from "../Repo";
import {FeedbackDomain} from "../Feedback";

export type TeamDto = SirenEntity<TeamDtoProperties>

export interface TeamDtoProperties{
    team: TeamDomain,
    students: Student[],
    repos: RepoDomain[],
    feedbacks: FeedbackDomain[],
}

export class TeamDtoProperties {
    constructor(
        team: TeamDomain,
        students: Student[],
        repos: RepoDomain[],
        feedbacks: FeedbackDomain[],
    ) {
        this.team = team
        this.students = students
        this.repos = repos
        this.feedbacks = feedbacks
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