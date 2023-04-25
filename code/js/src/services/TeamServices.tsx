import {fetchGet, fetchPost} from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {MenuDtoProperties} from "../domain/dto/MenuDtoProperties";
import {CourseBody, CourseDtoProperties, CourseWithClassroomsDtoProperties} from "../domain/dto/CourseDtoProperties";
import {GitHubOrgsDtoProperties} from "../domain/dto/GitHubOrgsDtoProperties";
import {FeedbackBody, TeamDtoProperties} from "../domain/dto/TeamDtoProperties";
import {
    LeaveTeamBody,
    RequestChangeStatusDto,
    RequestChangeStatusDtoProperties, RequestCreatedDtoProperties,
    TeamRequestsDtoProperties
} from "../domain/dto/RequestDtoProperties";
import {FeedbackDtoProperties} from "../domain/dto/FeedbackDtoProperties";
import {parse} from "uri-template";


export class TeamServices {

    team = async (courseId,classroomId,assignmentId,teamId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.TEAM_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,teamId:teamId})
        return await fetchGet<TeamDtoProperties>(href)
    }

    leaveTeam = async (body:LeaveTeamBody,courseId,classroomId,assignmentId,teamId) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.EXIT_TEAM_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,teamId:teamId})
        return await fetchPost<RequestCreatedDtoProperties>(href,body)
    }

    teamRequests = async (courseId,classroomId,assignmentId,teamId) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.REQUESTS_KEY, Hypermedia.systemServices.home)
        const href = parse(link.href).expand({courseId: courseId,classroomId:classroomId,assignmentId:assignmentId,teamId:teamId})
        return await fetchGet<TeamRequestsDtoProperties>(href)
    }

    changeRequestStatus = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.systemServices.home)
        // TODO: Change this
        const response = await fetchPost<RequestChangeStatusDtoProperties>(link.href,null)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    sendFeedback = async (body:FeedbackBody) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.systemServices.home)
        // TODO: Change this
        const response = await fetchPost<FeedbackDtoProperties>(link.href,body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }
}