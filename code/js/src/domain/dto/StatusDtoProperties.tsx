import {SirenEntity} from "../../siren/Siren";

export type StatusDto = SirenEntity<StatusDtoProperties>

export interface StatusDtoProperties{
    statusInfo: string,
    message: string
}

export class StatusDtoProperties {
    constructor(
        statusInfo: string,
        message: string
    ) {
        this.statusInfo = statusInfo;
        this.message = message;
    }
}
