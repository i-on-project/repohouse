import { HomeDtoProperties } from "../domain/dto/HomeDtoProperties"
import { fetchGet } from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {CreditsDtoProperties} from "../domain/dto/CreditsDtoProperties";


export class SystemServices {

    home = async () => {
        const response = await fetchGet<HomeDtoProperties>("api/home")
        if (response instanceof SirenEntity) {
            Hypermedia.navigationRepository.addLinks([Hypermedia.CREDITS_KEY, Hypermedia.MENU_KEY,Hypermedia.AUTH_TEACHER_KEY, Hypermedia.AUTH_STUDENT_KEY, Hypermedia.AUTH_REGISTER_INFO, Hypermedia.AUTH_STATUS_KEY, Hypermedia.ORGS_KEY, Hypermedia.COURSE_KEY,Hypermedia.CLASSROOM_KEY,Hypermedia.ASSIGNMENT_KEY], response.links)
            Hypermedia.navigationRepository.addActions([Hypermedia.LOGOUT_KEY, Hypermedia.AUTH_REGISTER_STUDENT, Hypermedia.AUTH_REGISTER_TEACHER, Hypermedia.AUTH_VERIFY_STUDENT_KEY, Hypermedia.CREATE_COURSE_KEY], response.actions)
        }
        return response
    }

    credits = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.CREDITS_KEY, this.home)
        const response = await fetchGet<CreditsDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            Hypermedia.navigationRepository.addLinks([Hypermedia.HOME_KEY, Hypermedia.MENU_KEY], response.links)
        }
        return response
    }

}