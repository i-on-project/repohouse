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
    RequestChangeStatusDtoProperties,
    TeamRequestsDtoProperties
} from "../domain/dto/RequestDtoProperties";
import {FeedbackDtoProperties} from "../domain/dto/FeedbackDtoProperties";


export class TeamServices {

    team = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchGet<TeamDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    leaveTeam = async (body:LeaveTeamBody) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchPost<any>(link.href,body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    teamRequests = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchGet<TeamRequestsDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    changeRequestStatus = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchPost<RequestChangeStatusDtoProperties>(link.href,null)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }

    sendFeedback = async (body:FeedbackBody) => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.COURSE_KEY, Hypermedia.menuServices.menu)
        // TODO: Change this
        const response = await fetchPost<FeedbackDtoProperties>(link.href,body)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
    }
}