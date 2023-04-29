import {SirenEntity} from "../../siren/Siren";
import { Student, Teacher } from "../User";

export type StateDto = SirenEntity<StateDtoProperties>

export interface StateDtoProperties {
    user: Student | Teacher,
    authenticated: string,
}

export class StateDtoProperties {
    constructor(
        user: Student | Teacher,
        authenticated: string,
    ) {
        this.user = user
        this.authenticated = authenticated
    }
}
