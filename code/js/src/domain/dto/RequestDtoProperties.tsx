import {SirenEntity} from "../../siren/Siren";
import {Teacher} from "../response-models/Teacher";
import {JoinTeam, LeaveTeam} from "../Request";

export type RequestDto = SirenEntity<RequestDtoProperties>
export type TeamRequestsDto = SirenEntity<TeamRequestsDtoProperties>
export type RequestCreatedDto = SirenEntity<RequestCreatedDtoProperties>
export type RequestChangeStatusDto = SirenEntity<RequestChangeStatusDtoProperties>

export interface RequestDtoProperties{
    status: number,
    id: number,
    title: string,
}

export class RequestDtoProperties {
    constructor(
        status: number,
        id: number,
        title: string,
    ) {
        this.status = status
        this.id = id
        this.title = title
    }
}

export interface TeamRequestsDtoProperties{
    joinTeam: JoinTeam[],
    leaveTeam: LeaveTeam[],
}

export class TeamRequestsDtoProperties {
    constructor(
        joinTeam: JoinTeam[],
        leaveTeam: LeaveTeam[],
    ) {
        this.joinTeam = joinTeam
        this.leaveTeam = leaveTeam
    }
}

export interface RequestCreatedDtoProperties{
    id: number,
    created: Boolean,
}

export class RequestCreatedDtoProperties {
    constructor(
        id: number,
        created: Boolean,
    ) {
        this.id = id
        this.created = created
    }
}

export interface RequestChangeStatusDtoProperties{
    id: number,
    changed: Boolean,
}

export class RequestChangeStatusDtoProperties {
    constructor(
        id: number,
        changed: Boolean,
    ) {
        this.id = id
        this.changed = changed
    }
}

export interface RequestBody{
    creator: number,
    composite: number | null ,
}

export class LeaveTeamBody implements RequestBody{
    constructor(
        teamId: number,
        creator: number,
        composite: number | null = null,
    ) {
        this.teamId = teamId
        this.creator = creator
        this.composite = composite
    }

    composite: number | null;
    creator: number;
    teamId: number;
}
