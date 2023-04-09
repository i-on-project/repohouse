import {RequestChangeStatusDto, RequestCreatedDto, RequestDto, TeamRequestsDto} from "../dto/RequestDtoProperties";
import {JoinTeam, LeaveTeam} from "../Request";


export interface Request{
    status: number,
    id: number,
    title: string,
}

export class Request {
    constructor(
        dto: RequestDto
    ) {
        const request = dto.properties
        if (request == null) throw new Error("RequestDto properties is null")
        this.id = request.id
        this.status = request.status
        this.title = request.title
    }
}

export interface TeamRequests{
    joinTeam: JoinTeam[],
    leaveTeam: LeaveTeam[],
}

export class TeamRequests {
    constructor(
        dto: TeamRequestsDto
    ) {
        const teamRequests = dto.properties
        if (teamRequests == null) throw new Error("TeamRequestsDto properties is null")
        this.joinTeam = teamRequests.joinTeam
        this.leaveTeam = teamRequests.leaveTeam
    }
}

export interface RequestCreated{
    id: number,
    created: Boolean,
}

export class RequestCreated {
    constructor(
        dto: RequestCreatedDto
    ) {
        const requestCreated = dto.properties
        if (requestCreated == null) throw new Error("RequestCreatedDto properties is null")
        this.id = requestCreated.id
        this.created = requestCreated.created
    }
}

export interface RequestChangeStatus{
    id: number,
    changed: Boolean,
}

export class RequestChangeStatus {
    constructor(
        dto: RequestChangeStatusDto
    ) {
        const requestChangeStatus = dto.properties
        if (requestChangeStatus == null) throw new Error("RequestChangeStatusDto properties is null")
        this.id = requestChangeStatus.id
        this.changed = requestChangeStatus.changed
    }
}