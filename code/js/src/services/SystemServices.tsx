import { HomeDtoProperties } from "../domain/dto/HomeDtoProperties"
import { fetchGet } from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"
import * as Hypermedia from "../Dependecies"
import {CreditsDtoProperties} from "../domain/dto/CreditsDtoProperties";


export class SystemServices {

    home = async () => {
        const response = await fetchGet<HomeDtoProperties>("api/home")
        if (response instanceof SirenEntity) {
            Hypermedia.navigationRepository.addLinks(Hypermedia.systemLinkKeys, response.links)
            Hypermedia.navigationRepository.addActions(Hypermedia.systemActionKeys, response.actions)
        }
        return response
    }

    credits = async () => {
        const link = await Hypermedia.navigationRepository.ensureLink(Hypermedia.CREDITS_KEY, this.home)
        return await fetchGet<CreditsDtoProperties>(link.href)
    }



}