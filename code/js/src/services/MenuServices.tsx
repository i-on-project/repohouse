import { HomeDtoProperties } from "../domain/dto/HomeDtoProperties"
import { fetchGet } from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {CreditsDtoProperties} from "../domain/dto/CreditsDtoProperties";


export class MenuServices {

    menu = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.MENU_KEY, Hypermedia.systemServices.home)
        const response = await fetchGet<HomeDtoProperties>(link.href)
        if (response instanceof SirenEntity) {
            Hypermedia.navigationRepository.addLinks([Hypermedia.CREDITS_KEY, Hypermedia.TEACHERS_APPROVAL_KEY,Hypermedia.CREATE_COURSE_KEY,Hypermedia.COURSE_KEY], response.links)
            Hypermedia.navigationRepository.addActions([Hypermedia.LOGOUT_KEY], response.actions)
        }
        return response
    }

}