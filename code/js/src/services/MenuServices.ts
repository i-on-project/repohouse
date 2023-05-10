import { navigationRepository, systemServices } from "../react-components"
import * as Hypermedia from "../http/Hypermedia"
import { fetchGet, fetchPost } from "../http/Fetch"
import { MenuDtoProperties}  from "../domain/dto/MenuDtoProperties"
import { TeacherPendingApprovalDtoProperties } from "../domain/dto/TeacherDtoProperties"
import { parse } from "uri-template"
import {ClassroomDtoProperties, ClassroomInviteDtoProperties} from "../domain/dto/ClassroomDtoProperties"


export class MenuServices {

    menu = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.MENU_KEY, systemServices.home)
        return await fetchGet<MenuDtoProperties>(link.href)
    }

    getTeachersPendingApproval = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.TEACHERS_APPROVAL_KEY, systemServices.home)
        return await fetchGet<TeacherPendingApprovalDtoProperties>(link.href)
    }

    approveTeacher = async (approved:number[],rejected:number[]) => {
        const link = await navigationRepository.ensureAction(Hypermedia.APPROVE_TEACHERS_KEY, systemServices.home)
        const body = {
            approved: approved,
            rejected: rejected
        }
        return await fetchPost<TeacherPendingApprovalDtoProperties>(link.href, body)
    }

    inviteLink = async (inviteCode) => {
        const link = await navigationRepository.ensureAction(Hypermedia.INVITE_CODE_KEY, systemServices.home)
        const href = parse(link.href).expand({inviteLink: inviteCode})
        return await fetchPost<ClassroomInviteDtoProperties>(href)
    }
}
