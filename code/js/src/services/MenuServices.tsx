import {fetchGet, fetchPost} from "../siren/Fetch"
import {SirenEntity} from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {MenuDtoProperties} from "../domain/dto/MenuDtoProperties";
import {TeacherPendingApprovalDtoProperties} from "../domain/dto/TeacherDtoProperties";


export class MenuServices {

    menu = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.MENU_KEY, Hypermedia.systemServices.home)
        const response = await fetchGet<MenuDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            Hypermedia.navigationRepository.addLinks([Hypermedia.CREDITS_KEY, Hypermedia.TEACHERS_APPROVAL_KEY,Hypermedia.CREATE_COURSE_KEY,Hypermedia.COURSE_KEY], response.links)
            Hypermedia.navigationRepository.addActions([Hypermedia.LOGOUT_KEY], response.actions)
        }
        return response
    }

    getTeachersPendingApproval = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.TEACHERS_APPROVAL_KEY, Hypermedia.systemServices.home)
        const response = await fetchGet<TeacherPendingApprovalDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            // TODO
        }
        return response
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