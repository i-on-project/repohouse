import {SirenEntity} from "../../siren/Siren";
import {Teacher} from "../response-models/Teacher";

export type CourseDto = SirenEntity<CourseDtoProperties>

export interface CourseDtoProperties{
    id: number,
    orgUrl: string,
    name: string,
    teacher: Teacher[],
}

export class CourseDtoProperties {
    constructor(
        id: number,
        orgUrl: string,
        name: string,
        teacher: Teacher[],
    ) {
        this.id = id
        this.orgUrl = orgUrl
        this.name = name
        this.teacher = teacher
    }
}