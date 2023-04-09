import * as Hypermedia from "../Dependecies"
import {fetchGet} from "../siren/Fetch";
import {SirenEntity} from "../siren/Siren";
import {StatusDtoProperties} from "../domain/dto/StatusDtoProperties";


export class AuthServices {

    authTeacher = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_TEACHER_KEY, Hypermedia.systemServices.home)
        console.log("Link -> " + link.href)
        const response = await fetchGet<StatusDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            Hypermedia.navigationRepository.addLinks([Hypermedia.HOME_KEY, Hypermedia.MENU_KEY], response.links)
        }
        return response
    }

    authStudent = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_STUDENT_KEY, Hypermedia.systemServices.home)
        console.log("Link -> " + link.href)
        const response = await fetchGet<StatusDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            Hypermedia.navigationRepository.addLinks([Hypermedia.HOME_KEY, Hypermedia.MENU_KEY], response.links)
        }
        return response
    }

    async logout() {
        // TODO: Implement
    }
}