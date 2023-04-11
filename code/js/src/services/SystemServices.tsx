import { HomeDtoProperties } from "../domain/dto/HomeDtoProperties"
import { fetchGet } from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {CreditsDtoProperties} from "../domain/dto/CreditsDtoProperties";


export class SystemServices {

    home = async () => {
        const response = await fetchGet<HomeDtoProperties>("api/home")
        if (response instanceof SirenEntity) {
            console.log("Returned from home: " + response.properties.title)

            Hypermedia.navigationRepository.addLinks([Hypermedia.CREDITS_KEY, Hypermedia.MENU_KEY,Hypermedia.AUTH_TEACHER_KEY, Hypermedia.AUTH_STUDENT_KEY], response.links)
            Hypermedia.navigationRepository.addActions([Hypermedia.LOGOUT_KEY], response.actions)
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