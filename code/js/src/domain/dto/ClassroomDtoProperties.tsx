import {SirenEntity} from "../../siren/Siren";
import {AssignmentDomain} from "../Assignment";
import {Student} from "../User";

export type ClassroomDto = SirenEntity<ClassroomDtoProperties>

export interface ClassroomDtoProperties{
    id: number,
    name: String,
    isArchived: Boolean,
    lastSync: Date,
    assignments: AssignmentDomain[],
    students: Student[],
}

export class ClassroomDtoProperties {
    constructor(
        id: number,
        name: String,
        isArchived: Boolean,
        lastSync: Date,
        assignments: AssignmentDomain[],
        students: Student[],
    ) {
        this.id = id
        this.name = name
        this.isArchived = isArchived
        this.lastSync = lastSync
        this.assignments = assignments
        this.students = students
    }
}

export interface ClassroomBody {
    name: String,
}

export class ClassroomBody {
    constructor(
        name: String
    ) {
        this.name = name
    }
}