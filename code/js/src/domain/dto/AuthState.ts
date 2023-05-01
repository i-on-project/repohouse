import { SirenEntity } from "../../http/Siren"

export type StateDto = SirenEntity<StateDtoProperties>

export interface StateDtoProperties {
    user: string,
    authenticated: boolean,
}

export class StateDtoProperties {
    constructor(
        user: string,
        authenticated: boolean,
    ) {
        this.user = user
        this.authenticated = authenticated
    }
}
