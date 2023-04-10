import {RegisterDto} from "../dto/RegisterDtoProperties";


export interface Register{
    title: string,
    description: string
}

export class Register {
    constructor(
        dto: RegisterDto
    ) {
        const register = dto.properties
        if (register == null) throw new Error("RegisterDto properties is null")
        this.title = register.title
        this.description = register.description
    }
}