import {fetchDelete, fetchGet, fetchPost} from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {MenuDtoProperties} from "../domain/dto/MenuDtoProperties";
import {CourseBody, CourseDtoProperties, CourseWithClassroomsDtoProperties} from "../domain/dto/CourseDtoProperties";
import {GitHubOrgsDtoProperties} from "../domain/dto/GitHubOrgsDtoProperties";
import {ClassroomDtoProperties} from "../domain/dto/ClassroomDtoProperties";
import {
    AssignmentBody,
    AssignmentDeletedDtoProperties,
    AssignmentDtoProperties
} from "../domain/dto/AssignmentDtoProperties";


export class AssignmentServices {

    assignment = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchGet<AssignmentDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    createAssignment = async (body: AssignmentBody) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        //TODO: Change this
        const response = await fetchPost<AssignmentDtoProperties>(link.href, body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    deleteAssignment = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        //TODO: Change this
        const response = await fetchDelete<AssignmentDeletedDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    editAssignment = async (body: AssignmentBody) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        //TODO: Change this
        const response = await fetchPost<AssignmentDtoProperties>(link.href, body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }


}