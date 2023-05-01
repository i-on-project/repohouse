import { navigationRepository, systemServices } from "../react-components"
import * as Hypermedia from "../http/Hypermedia"
import { fetchDelete, fetchGet, fetchPost } from "../http/Fetch"
import { AssignmentBody, AssignmentDeletedDtoProperties, AssignmentDtoProperties } from "../domain/dto/AssignmentDtoProperties"
import { parse } from "uri-template"
import { CreateTeamBody, JoinTeamBody, RequestCreatedDtoProperties } from "../domain/dto/RequestDtoProperties"
import { TeamsDtoProperties } from "../domain/dto/TeamDtoProperties"


export class AssignmentServices {

    assignment = async (courseId,classroomId,assignmentId) => {
        const link = await navigationRepository.ensureLink(Hypermedia.ASSIGNMENT_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchGet<AssignmentDtoProperties>(href)
    }

    createAssignment = async (courseId,classroomId,body: AssignmentBody) => {
        const link = await navigationRepository.ensureAction(Hypermedia.CREATE_ASSIGNMENT_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId:courseId,classroomId:classroomId})
        return await fetchPost<AssignmentDtoProperties>(href, body)
    }

    deleteAssignment = async (courseId,classroomId,assignmentId) => {
        const link = await navigationRepository.ensureAction(Hypermedia.DELETE_ASSIGNMENT_KEY,systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchDelete<AssignmentDeletedDtoProperties>(href)
    }

    editAssignment = async (body: AssignmentBody) => {
        const link = await navigationRepository.ensureLink(Hypermedia.COURSE_KEY, systemServices.home)
        //TODO
        return await fetchPost<AssignmentDtoProperties>(link.href, body)
    }

    joinTeam = async (body:JoinTeamBody,courseId,classroomId,assignmentId) => {
        const link = await navigationRepository.ensureAction(Hypermedia.JOIN_TEAM_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchPost<RequestCreatedDtoProperties>(href,body)
    }

    createTeam = async (body: CreateTeamBody, courseId: number, classroomId: number, assignmentId: number) => {
        const link = await navigationRepository.ensureAction(Hypermedia.CREATE_TEAM_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchPost<RequestCreatedDtoProperties>(href,body)
    }

    teams = async (courseId,classroomId,assignmentId) => {
        const link = await navigationRepository.ensureLink(Hypermedia.TEAMS_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId})
        return await fetchGet<TeamsDtoProperties>(href)
    }
}
