import { HomeDtoProperties } from "../domain/dto/HomeDtoProperties"
import { fetchGet } from "../siren/Fetch"
import { SirenEntity } from "../siren/Siren"


export class SystemServices {

    home = async () => {
        const response = await fetchGet<HomeDtoProperties>("api/home")
        if (response instanceof SirenEntity) {
            //Hypermedia.navigationRepo.addLinks([Hypermedia.WINS_LEADERBOARD_KEY, Hypermedia.CREDITS_KEY, Hypermedia.MENU_KEY, Hypermedia.GAMEMODE_RANKED_LIST_KEY, Hypermedia.GAMEMODE_NORMAL_LIST_KEY], response.links)
            //Hypermedia.navigationRepo.addActions([Hypermedia.LOGIN_KEY, Hypermedia.REGISTER_KEY], response.actions)
        }
        return response
    }

}