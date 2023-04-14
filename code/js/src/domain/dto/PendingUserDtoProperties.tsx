import {SirenEntity} from "../../siren/Siren";
import {Teacher, TeacherPending} from "../response-models/Teacher";

export type PendingUserDto = SirenEntity<PendingUserDtoProperties>

export interface PendingUserDtoProperties{
    name: String,
    email: String,
    GitHubUsername: String,
}

export class PendingUserDtoProperties {
    constructor(
        name: String,
        email: String,
        GitHubUsername: String,
    ) {
        this.name = name
        this.email = email
        this.GitHubUsername = GitHubUsername
    }
}

