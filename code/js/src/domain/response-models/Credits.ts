import {CreditsDto,CreditsStudent, CreditsTeacher} from "../dto/CreditsDtoProperties";

export interface Credits{
    teacher: CreditsTeacher,
    students: CreditsStudent[]
}

export class Credits {
    constructor(
        dto: CreditsDto
    ) {
        const credits = dto.properties
        if(credits == null) throw new Error("CreditsDto properties is null")
        this.teacher = credits.teacher
        this.students = credits.students
    }
}