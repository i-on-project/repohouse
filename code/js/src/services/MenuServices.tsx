import {fetchGet, fetchPost} from "../siren/Fetch"
import {SirenEntity} from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {MenuDtoProperties} from "../domain/dto/MenuDtoProperties";
import {TeacherPendingApprovalDtoProperties} from "../domain/dto/TeacherDtoProperties";


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
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.TEACHERS_APPROVAL_KEY, Hypermedia.systemServices.home)
        const body = {
            approved: approved,
            rejected: rejected
        }
        return await fetchPost<TeacherPendingApprovalDtoProperties>(link.href, body)
    }

}