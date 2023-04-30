import {fetchGet, fetchPost, fetchPut} from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {
    CourseBody,
    CourseCreatedDtoProperties,
    CourseDtoProperties,
    CourseWithClassroomsDtoProperties
} from "../domain/dto/CourseDtoProperties";
import {GitHubOrgsDtoProperties} from "../domain/dto/GitHubOrgsDtoProperties";
import {parse} from "uri-template";


export class CourseServices {

    course = async (courseId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand(courseId)
        return await fetchGet<CourseWithClassroomsDtoProperties>(href)
    }

    getTeacherOrgs = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.ORGS_KEY, Hypermedia.systemServices.home)
        return await fetchGet<GitHubOrgsDtoProperties>(link.href)
    }

    createCourse = async (course: CourseBody) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.CREATE_COURSE_KEY, Hypermedia.systemServices.home)
        return await fetchPost<CourseCreatedDtoProperties>(link.href, course)
    }

    archiveCourse = async (courseId) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.ARCHIVE_COURSE_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId})
        return await fetchPut<CourseDtoProperties>(href)
    }

}