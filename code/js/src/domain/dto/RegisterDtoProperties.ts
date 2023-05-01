import { SirenEntity } from "../../http/Siren"

export type RegisterDto = SirenEntity<RegisterDtoProperties>

export interface RegisterDtoProperties{
    title: string,
    description: string
}

export class RegisterDtoProperties {
    constructor(
        title: string,
        description: string
    ) {
        this.title = title
        this.description = description
    }
}
