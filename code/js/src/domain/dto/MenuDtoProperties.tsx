import {SirenEntity} from "../../siren/Siren";
import {Course} from "../response-models/Course";

export type MenuDto = SirenEntity<MenuDtoProperties>

export interface MenuDtoProperties {
    name: string,
    schoolNumber: number,
    email: string,
    courses: Course[]
}

export class MenuDtoProperties {
    constructor(
        name: string,
        schoolNumber: number,
        email: string,
        courses: Course[]
    ) {
        this.name = name
        this.schoolNumber = schoolNumber
        this.email = email
        this.courses = courses
    }
}
