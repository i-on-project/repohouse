import {SirenEntity} from "../../siren/Siren";
import {Teacher} from "../response-models/Teacher";
import {Classroom} from "../response-models/Classroom";

export type CourseDto = SirenEntity<CourseDtoProperties>
export type CourseWithClassroomsDto = SirenEntity<CourseWithClassroomsDtoProperties>

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

export interface CourseWithClassroomsDtoProperties{
    id: number,
    orgUrl: string,
    name: string,
    teacher: Teacher[],
    isArchived: boolean,
    classrooms: Classroom[]
}

export class CourseWithClassroomsDtoProperties {
    constructor(
        id: number,
        orgUrl: string,
        name: string,
        teacher: Teacher[],
        isArchived: boolean,
        classrooms: Classroom[]
    ) {
        this.id = id
        this.orgUrl = orgUrl
        this.name = name
        this.teacher = teacher
        this.isArchived = isArchived
        this.classrooms = classrooms
    }
}

export class CourseBody{
    constructor(
        name: string,
        orgUrl: string
    ) {
        this.name = name
        this.orgUrl = orgUrl
    }
    name: string
    orgUrl: string
}