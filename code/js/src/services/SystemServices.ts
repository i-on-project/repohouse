import { navigationRepository } from "../react-components"
import * as Hypermedia from "../http/Hypermedia"
import { HomeDtoProperties } from "../domain/dto/HomeDtoProperties"
import { fetchGet } from "../http/Fetch"
import { SirenEntity } from "../http/Siren"
import { CreditsDtoProperties } from "../domain/dto/CreditsDtoProperties"


export class SystemServices {

    home = async () => {
        const response = await fetchGet<HomeDtoProperties>("/api/home")
        if (response instanceof SirenEntity) {
            navigationRepository.addLinks(Hypermedia.systemLinkKeys, response.links)
            navigationRepository.addActions(Hypermedia.systemActionKeys, response.actions)
        }
        return response
    }

    credits = async () => {
        const link = await navigationRepository.ensureLink(Hypermedia.CREDITS_KEY, this.home)
        return await fetchGet<CreditsDtoProperties>(link.href)
    }
}
