import {fetchGet, fetchPost} from "../siren/Fetch"
import * as Hypermedia from "../Dependecies"
import {MenuDtoProperties} from "../domain/dto/MenuDtoProperties";
import {TeacherPendingApprovalDtoProperties} from "../domain/dto/TeacherDtoProperties";
import {parse} from "uri-template";
import {ClassroomDtoProperties} from "../domain/dto/ClassroomDtoProperties";


export class MenuServices {

    menu = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.MENU_KEY, Hypermedia.systemServices.home)
        return await fetchGet<MenuDtoProperties>(link.href)
    }

    getTeachersPendingApproval = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.TEACHERS_APPROVAL_KEY, Hypermedia.systemServices.home)
        return await fetchGet<TeacherPendingApprovalDtoProperties>(link.href)
    }

    approveTeacher = async (approved:number[],rejected:number[]) => {
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.TEACHERS_APPROVAL_KEY, Hypermedia.systemServices.home)
        const body = {
            approved: approved,
            rejected: rejected
        }
        return await fetchPost<TeacherPendingApprovalDtoProperties>(link.href, body)
    }

    inviteLink = async (inviteCode) => {
        console.log(inviteCode)
        const link = await Hypermedia.navigationRepository.ensureAction(Hypermedia.INVITE_CODE_KEY, Hypermedia.systemServices.home)
        console.log(link.href)
        const href = parse(link.href).expand({inviteLink: inviteCode})
        console.log(href)
        return await fetchPost<ClassroomDtoProperties>(href, null)
    }

}