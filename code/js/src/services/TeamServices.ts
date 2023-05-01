import { navigationRepository, systemServices } from "../react-components"
import * as Hypermedia from "../http/Hypermedia"
import { fetchGet, fetchPost } from "../http/Fetch"
import { SirenEntity } from "../http/Siren"
import { FeedbackBody, TeamDtoProperties } from "../domain/dto/TeamDtoProperties"
import { LeaveTeamBody, RequestChangeStatusDtoProperties, RequestCreatedDtoProperties, TeamRequestsDtoProperties } from "../domain/dto/RequestDtoProperties"
import {FeedbackDtoProperties} from "../domain/dto/FeedbackDtoProperties"
import { parse } from "uri-template"


export class TeamServices {

    team = async (courseId,classroomId,assignmentId,teamId) => {
        const link = await navigationRepository.ensureLink(Hypermedia.TEAM_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,teamId:teamId})
        return await fetchGet<TeamDtoProperties>(href)
    }

    leaveTeam = async (body:LeaveTeamBody,courseId,classroomId,assignmentId,teamId) => {
        const link = await navigationRepository.ensureAction(Hypermedia.EXIT_TEAM_KEY,systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,teamId:teamId})
        return await fetchPost<RequestCreatedDtoProperties>(href,body)
    }

    teamRequests = async (courseId,classroomId,assignmentId,teamId) => {
        const link = await navigationRepository.ensureLink(Hypermedia.REQUESTS_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,teamId:teamId})
        return await fetchGet<TeamRequestsDtoProperties>(href)
    }

    changeRequestStatus = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.COURSE_KEY, systemServices.home)
        // TODO: Change this
        const response = await fetchPost<RequestChangeStatusDtoProperties>(link.href,null)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    sendFeedback = async (body:FeedbackBody) => {
        const link = await navigationRepository.ensureLink(Hypermedia.COURSE_KEY, systemServices.home)
        // TODO: Change this
        const response = await fetchPost<FeedbackDtoProperties>(link.href,body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }
}