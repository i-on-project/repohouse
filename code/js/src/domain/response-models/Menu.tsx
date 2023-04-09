import {MenuDto} from "../dto/MenuDtoProperties";
import {Course} from "./Course";

export interface Menu{
    name: string,
    schoolNumber: number,
    email: string,
    courses: Course[]
}

export class Menu {
    constructor(
        dto: MenuDto
    ) {
        const menu = dto.properties
        if(menu == null) throw new Error("MenuDto properties is null")
        this.name = menu.name
        this.schoolNumber = menu.schoolNumber
        this.email = menu.email
        this.courses = menu.courses
    }
}