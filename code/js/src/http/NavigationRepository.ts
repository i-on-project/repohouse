import { SirenAction, SirenLink } from "./Siren"


export class NavigationRepository implements NavigationRepository {

    linkStorage = new Map<string, SirenLink | null>()
    actionStorage = new Map<string, SirenAction | null>()


    getLink(key: string): SirenLink | null {
        return this.linkStorage.get(key)
    }

    getAction(key: string): SirenAction | null {
        return this.actionStorage.get(key)
    }

    addLinks(keys: string[], links: SirenLink[] | null) {
        if (links == null) return
        keys.forEach(key => {
            this.linkStorage.set(key, links?.find(link => link.rel.includes(key)))
        })
    }

    addActions(keys: string[], actions: SirenAction[] | null) {
        if (actions == null) return
        keys.forEach(key => {
            this.actionStorage.set(key, actions?.find(action => action.title == key))
        })
    }

    async ensureLink(key: string, fetchLink: () => any): Promise<SirenLink> {
        if (this.getLink(key) == null) {
            await fetchLink()
        }
        return this.getLink(key)
    }

    async ensureAction(key: string, fetchAction:() => any): Promise<SirenAction> {
        if (this.getAction(key) == null) {
            await fetchAction()
        }
        return this.getAction(key)
    }
}