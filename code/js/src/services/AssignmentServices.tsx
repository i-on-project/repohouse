import {fetchDelete, fetchGet, fetchPost} from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {
    AssignmentBody,
    AssignmentDeletedDtoProperties,
    AssignmentDtoProperties
} from "../domain/dto/AssignmentDtoProperties";
import {parse} from "uri-template";
import {CreateTeamBody, JoinTeamBody, RequestCreatedDtoProperties} from "../domain/dto/RequestDtoProperties";
import {TeamDomain} from "../domain/Team";
import {TeamDtoProperties, TeamsDtoProperties} from "../domain/dto/TeamDtoProperties";


export class AssignmentServices {

    assignment = async (courseId,classroomId,assignmentId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.ASSIGNMENT_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchGet<AssignmentDtoProperties>(href)
    }

    createAssignment = async (courseId,classroomId,body: AssignmentBody) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.CREATE_ASSIGNMENT_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId:courseId,classroomId:classroomId})
        return await fetchPost<AssignmentDtoProperties>(href, body)
    }

    deleteAssignment = async (courseId,classroomId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.DELETE_ASSIGNMENT_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId})
        return await fetchDelete<AssignmentDeletedDtoProperties>(href)
    }

    editAssignment = async (body: AssignmentBody) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.systemServices.home)
        //TODO: Change this
        const response = await fetchPost<AssignmentDtoProperties>(link.href, body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    joinTeam = async (body:JoinTeamBody,courseId,classroomId,assignmentId) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.JOIN_TEAM_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchPost<RequestCreatedDtoProperties>(href,body)
    }

    createTeam = async (body: CreateTeamBody, courseId: number, classroomId: number, assignmentId: number) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.CREATE_TEAM_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchPost<RequestCreatedDtoProperties>(href,body)
    }

    teams = async (courseId,classroomId,assignmentId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.TEAMS_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchGet<TeamsDtoProperties>(href)
    }
}