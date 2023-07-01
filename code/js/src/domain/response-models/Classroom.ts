import {AssignmentDomain} from "../Assignment";
import {Student} from "../User";
import {ClassroomDto} from "../dto/ClassroomDtoProperties";

export interface Classroom {
    id: number,
    name: String,
    isArchived: Boolean,
    lastSync: Date,
    inviteCode: String,
    assignments: AssignmentDomain[],
    students: Student[],
}

export class Classroom {
    constructor(
        dto: ClassroomDto
    ) {
        const classroom = dto.properties
        if(classroom == null) throw new Error("ClassroomDto properties is null")
        this.id = classroom.id
        this.name = classroom.name
        this.isArchived = classroom.isArchived
        this.lastSync = classroom.lastSync
        this.inviteCode = classroom.inviteCode
        this.assignments = classroom.assignments
        this.students = classroom.students
    }
}