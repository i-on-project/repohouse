import {fetchGet, fetchPost} from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {MenuDtoProperties} from "../domain/dto/MenuDtoProperties";
import {CourseBody, CourseDtoProperties, CourseWithClassroomsDtoProperties} from "../domain/dto/CourseDtoProperties";
import {GitHubOrgsDtoProperties} from "../domain/dto/GitHubOrgsDtoProperties";
import {ClassroomBody, ClassroomDtoProperties} from "../domain/dto/ClassroomDtoProperties";


export class ClassroomServices {

    classroom = async (courseId,classroomId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchGet<ClassroomDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    createClassroom = async (body:ClassroomBody) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchPost<ClassroomDtoProperties>(link.href,body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    archiveClassroom = async (courseId,classroomId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchGet<ClassroomDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }


}