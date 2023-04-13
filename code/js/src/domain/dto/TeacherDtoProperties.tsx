import {SirenEntity} from "../../siren/Siren";
import {Teacher, TeacherPending} from "../response-models/Teacher";

export type TeacherDto = SirenEntity<TeacherDtoProperties>
export type TeacherPendingApprovalDto = SirenEntity<TeacherPendingApprovalDtoProperties>

export interface TeacherDtoProperties{
    name: string,
    email: string,
    id: number,
    githubUsername: string,
    githubId: bigint,
    token: string,
    isCreated: boolean,
}

export class TeacherDtoProperties {
    constructor(
        name: string,
        email: string,
        id: number,
        githubUsername: string,
        githubId: bigint,
        token: string,
        isCreated: boolean,
    ) {
        this.id = id
        this.name = name
        this.email = email
        this.githubUsername = githubUsername
        this.githubId = githubId
        this.token = token
        this.isCreated = isCreated
    }
}

export interface TeacherPendingApprovalDtoProperties{
    teacher: TeacherPending[],
}

export class TeacherPendingApprovalDtoProperties {
    constructor(
        teacher: TeacherPending[],
    ) {
        this.teacher = teacher
    }
}