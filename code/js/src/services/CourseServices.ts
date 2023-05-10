import {navigationRepository, systemServices} from "../react-components"
import * as Hypermedia from "../http/Hypermedia"
import {fetchGet, fetchPost, fetchPut} from "../http/Fetch"
import {
    CourseBody,
    CourseCreatedDtoProperties,
    CourseDtoProperties,
    CourseWithClassroomsDtoProperties
} from "../domain/dto/CourseDtoProperties"
import {GitHubOrgsDtoProperties} from "../domain/dto/GitHubOrgsDtoProperties"
import {parse} from "uri-template"


export class CourseServices {

    course = async (courseId) => {
        const link = await navigationRepository.ensureLink(Hypermedia.COURSE_KEY, systemServices.home)
        const href = parse(link.href).expand(courseId)
        return await fetchGet<CourseWithClassroomsDtoProperties>(href)
    }

    getTeacherOrgs = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.ORGS_KEY, systemServices.home)
        return await fetchGet<GitHubOrgsDtoProperties>(link.href)
    }

    createCourse = async (course: CourseBody) => {
        const link = await navigationRepository.ensureAction(Hypermedia.CREATE_COURSE_KEY, systemServices.home)
        return await fetchPost<CourseCreatedDtoProperties>(link.href, course)
    }

    archiveCourse = async (courseId) => {
        const link = await navigationRepository.ensureAction(Hypermedia.ARCHIVE_COURSE_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId})
        return await fetchPut<CourseDtoProperties>(href)
    }
}
