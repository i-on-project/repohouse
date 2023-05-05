import { SirenEntity } from "../../http/Siren"

export type StateDto = SirenEntity<StateDtoProperties>

export interface StateDtoProperties {
    user: string,
    authenticated: boolean,
    githubId:BigInt,
    userId:number
}

export class StateDtoProperties {
    constructor(
        user: string,
        authenticated: boolean,
        githubId:BigInt,
        userId:number
    ) {
        this.user = user
        this.authenticated = authenticated,
        this.githubId = githubId,
        this.userId = userId
    }
}
