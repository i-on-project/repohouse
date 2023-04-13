import {SirenEntity} from "../../siren/Siren";
import {Course} from "../response-models/Course";

export type MenuDto = SirenEntity<MenuDtoProperties>

export interface MenuDtoProperties {
    name: string,
    email: string,
    courses: Course[]
}

export class MenuStudentDtoProperties implements MenuDtoProperties{
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

    schoolNumber: number;
    courses: Course[];
    email: string;
    name: string;
}

export class MenuTeacherDtoProperties implements MenuDtoProperties{
    constructor(
        name: string,
        email: string,
        courses: Course[]
    ) {
        this.name = name
        this.email = email
        this.courses = courses
    }

    courses: Course[];
    email: string;
    name: string;
}
