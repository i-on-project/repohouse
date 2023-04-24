import {fetchDelete, fetchGet, fetchPost} from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {
    AssignmentBody,
    AssignmentDeletedDtoProperties,
    AssignmentDtoProperties
} from "../domain/dto/AssignmentDtoProperties";
import {parse} from "uri-template";


export class AssignmentServices {

    assignment = async (courseId,classroomId,assignmentId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.ASSIGNMENT_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchGet<AssignmentDtoProperties>(href)
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