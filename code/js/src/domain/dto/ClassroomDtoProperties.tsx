import {SirenEntity} from "../../siren/Siren";
import {Teacher} from "../response-models/Teacher";
import {AssigmentDomain} from "../Assigment";
import {Student} from "../User";

export type ClassroomDto = SirenEntity<ClassroomDtoProperties>

export interface ClassroomDtoProperties{
    id: number,
    name: String,
    isArchived: Boolean,
    lastSync: Date,
    assigments: AssigmentDomain[],
    students: Student[],
}

export class ClassroomDtoProperties {
    constructor(
        id: number,
        name: String,
        isArchived: Boolean,
        lastSync: Date,
        assigments: AssigmentDomain[],
        students: Student[],
    ) {
        this.id = id
        this.name = name
        this.isArchived = isArchived
        this.lastSync = lastSync
        this.assigments = assigments
        this.students = students
    }
}