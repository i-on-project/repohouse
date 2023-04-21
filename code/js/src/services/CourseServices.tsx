import {fetchGet, fetchPost} from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {CourseBody, CourseDtoProperties, CourseWithClassroomsDtoProperties} from "../domain/dto/CourseDtoProperties";
import {GitHubOrgsDtoProperties} from "../domain/dto/GitHubOrgsDtoProperties";


export class CourseServices {

    course = async (courseId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.systemServices.home)
        // TODO: CourseId
        const response = await fetchGet<CourseWithClassroomsDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    getTeacherOrgs = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.ORGS_KEY, Hypermedia.systemServices.home)
        const response = await fetchGet<GitHubOrgsDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    createCourse = async (course: CourseBody) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.CREATE_COURSE_KEY, Hypermedia.systemServices.home)
        const response = await fetchPost<CourseDtoProperties>(link.href, course)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

}