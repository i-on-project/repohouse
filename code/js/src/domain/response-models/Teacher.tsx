import {TeacherDto, TeacherPendingApprovalDto} from "../dto/TeacherDtoProperties";

export interface Teacher {
    name: string,
    email: string,
    id: number,
    githubUsername: string,
    githubId: bigint,
    token: string,
    isCreated: boolean,
}

export class Teacher {
    constructor(
        dto: TeacherDto
    ) {
        const teacher = dto.properties
        if(teacher == null) throw new Error("TeacherDto properties is null")
        this.id = teacher.id
        this.name = teacher.name
        this.email = teacher.email
        this.githubUsername = teacher.githubUsername
        this.githubId = teacher.githubId
        this.token = teacher.token
        this.isCreated = teacher.isCreated
    }
}

export interface TeacherPending {
    name: string,
    email: string,
    id: number,
    requestId: number
}

export interface TeacherPendingApproval {
    teacher: TeacherPending[]
}

export class TeacherPendingApproval {
    constructor(
        dto: TeacherPendingApprovalDto
    ) {
        const teacher = dto.properties
        if(teacher == null) throw new Error("TeacherDto properties is null")
        this.teacher = teacher.teacher
    }
}