import {StatusDto} from "../dto/StatusDtoProperties";

export interface Status{
    statusInfo: string,
    message: string
}

export class Status {
    constructor(
        dto: StatusDto
    ) {
        const status = dto.properties
        if(status == null) throw new Error("StatusDto properties is null")
        this.statusInfo = status.statusInfo
        this.message = status.message
    }
}