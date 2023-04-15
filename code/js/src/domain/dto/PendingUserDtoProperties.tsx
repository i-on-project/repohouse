import {SirenEntity} from "../../siren/Siren";

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

