import { SirenEntity } from "../../http/Siren"
import { AssignmentDomain } from "../Assignment"
import { Student } from "../User"

export type ClassroomDto = SirenEntity<ClassroomDtoProperties>

export interface ClassroomDtoProperties{
    id: number,
    name: String,
    isArchived: Boolean,
    lastSync: Date,
    inviteCode: String,
    assignments: AssignmentDomain[],
    students: Student[],
}

export class ClassroomDtoProperties {
    constructor(
        id: number,
        name: String,
        isArchived: Boolean,
        lastSync: Date,
        inviteCode: String,
        assignments: AssignmentDomain[],
        students: Student[],
    ) {
        this.id = id
        this.name = name
        this.isArchived = isArchived
        this.lastSync = lastSync
        this.inviteCode = inviteCode
        this.assignments = assignments
        this.students = students
    }
}

export interface ClassroomInviteDtoProperties{
    courseId: number,
    classroom: ClassroomDtoProperties
}

export class ClassroomInviteDtoProperties {
    constructor(
        courseId: number,
        classroom: ClassroomDtoProperties
    ) {
        this.courseId = courseId
        this.classroom = classroom
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

export interface ClassroomArchievedOrDeletedDtoProperties{
    id: number
    archived: Boolean
    deleted: Boolean
}

export class ClassroomArchievedOrDeletedDtoProperties {
    constructor(
        id: number,
        archived: Boolean,
        deleted: Boolean
    ) {
        this.id = id
        this.archived = archived
        this.deleted = deleted
    }
}
