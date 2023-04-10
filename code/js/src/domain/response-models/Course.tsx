import {Teacher} from "./Teacher";
import {CourseDto} from "../dto/CourseDtoProperties";

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