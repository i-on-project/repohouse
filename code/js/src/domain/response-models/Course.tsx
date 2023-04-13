import {Teacher} from "./Teacher";
import {CourseDto, CourseWithClassroomsDto} from "../dto/CourseDtoProperties";
import {Classroom} from "./Classroom";

export interface Course {
    id: number,
    orgUrl: string,
    name: string,
    teacher: Teacher[],
}

export class Course {
    constructor(
        dto: CourseDto
    ) {
        const course = dto.properties
        if(course == null) throw new Error("CourseDto properties is null")
        this.id = course.id
        this.orgUrl = course.orgUrl
        this.name = course.name
        this.teacher = course.teacher
    }
}

export interface CourseWithClassrooms {
    id: number,
    orgUrl: string,
    name: string,
    teacher: Teacher[],
    isArchived: boolean,
    classrooms: Classroom[]
}

export class CourseWithClassrooms {
    constructor(
        dto: CourseWithClassroomsDto
    ) {
        const course = dto.properties
        if(course == null) throw new Error("CourseDto properties is null")
        this.id = course.id
        this.orgUrl = course.orgUrl
        this.name = course.name
        this.teacher = course.teacher
        this.isArchived = course.isArchived
        this.classrooms = course.classrooms
    }
}