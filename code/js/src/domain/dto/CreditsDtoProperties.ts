import { SirenEntity } from "../../http/Siren"

export type CreditsDto = SirenEntity<CreditsDtoProperties>

export interface CreditsDtoProperties{
    teacher: CreditsTeacher,
    students: CreditsStudent[]
}

export class CreditsDtoProperties {
    constructor(
        teacher: CreditsTeacher,
        students: CreditsStudent[]
    ) {
        this.teacher = teacher
        this.students = students
    }
}

export interface CreditsTeacher {
    name: string,
    email: string,
}

export class CreditsTeacher {
    constructor(
        name: string,
        email: string,
    ) {
        this.name = name
        this.email = email
    }
}

export interface CreditsStudent {
    name: string,
    schoolNumber: number,
    email: string
}

export class CreditsStudent {
    constructor(
        name: string,
        schoolNumber: number,
        email: string,
    ) {
        this.name = name
        this.schoolNumber = schoolNumber
        this.email = email
    }
}
