import { navigationRepository, systemServices } from "../react-components"
import * as Hypermedia from "../http/Hypermedia"
import { fetchGet, fetchPost } from "../http/Fetch"
import { FeedbackBody, TeamClosedDtoProperties, TeamDtoProperties } from "../domain/dto/TeamDtoProperties"
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

    changeRequestStatus = async (courseId,classroomId,assignmentId,teamId,requestId) => {
        const link = await navigationRepository.ensureAction(Hypermedia.CHANGE_REQUEST_STATUS_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,teamId:teamId,requestId:requestId})
        return await fetchPost<RequestChangeStatusDtoProperties>(href)
    }

    sendFeedback = async (courseId,classroomId,assignmentId,teamId,body:FeedbackBody) => {
        const link = await navigationRepository.ensureAction(Hypermedia.POST_FEEDBACK_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,teamId:teamId})
        return await fetchPost<FeedbackDtoProperties>(href,body)
    }

    closeTeam = async (courseId,classroomId,assignmentId,teamId) => {
        const link = await navigationRepository.ensureAction(Hypermedia.CLOSE_TEAM_KEY, systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,teamId:teamId})
        return await fetchPost<TeamClosedDtoProperties>(href)
    }
}