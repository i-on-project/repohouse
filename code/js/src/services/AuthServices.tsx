import * as Hypermedia from "../Dependecies"
import {fetchGet} from "../siren/Fetch";
import {SirenEntity} from "../siren/Siren";
import {StatusDtoProperties} from "../domain/dto/StatusDtoProperties";
import {AuthRedirectDto, AuthRedirectDtoProperties} from "../domain/dto/AuthRedirectDtoProperties";


export class AuthServices {

    authTeacher = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_TEACHER_KEY, Hypermedia.systemServices.home)
        const response = await fetchGet<AuthRedirectDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            Hypermedia.navigationRepository.addLinks([Hypermedia.HOME_KEY, Hypermedia.AUTH_CALLBACK_KEY], response.links)
        }
        return response
    }

    authStudent = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_STUDENT_KEY, Hypermedia.systemServices.home)
        const response = await fetchGet<AuthRedirectDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            console.log("Returned from authStudent: " + response.properties.url)
            Hypermedia.navigationRepository.addLinks([Hypermedia.AUTH_CALLBACK_KEY], response.links)
        }
        return response
    }

    authCallback = async () => {
        //const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.AUTH_CALLBACK_KEY,Hypermedia.authServices.authStudent)
        const link = 'api/auth/callback' + window.location.search
        console.log("authCallback: " + link)
        const response = await fetchGet<StatusDtoProperties>(link)
        if (response instanceof SirenEntity) {
            Hypermedia.navigationRepository.addLinks([Hypermedia.HOME_KEY, Hypermedia.MENU_KEY], response.links)
        }
        return response
    }

    async logout() {
        // TODO: Implement
    }
}