package isel.ps.classcode.http

import isel.ps.classcode.http.hypermedia.SirenAction
import isel.ps.classcode.http.hypermedia.SirenLink

class NavigationRepository {
    private val linkStorage = mutableMapOf<String, SirenLink?>()
    private val actionsStorage = mutableMapOf<String, SirenAction?>()

    private fun addLink(key: String, link: SirenLink?) = linkStorage.put(key, link)
    private fun addAction(key: String, action: SirenAction?) = actionsStorage.put(key, action)
    private fun getLink(key: String): SirenLink? = linkStorage[key]
    private fun getAction(key: String): SirenAction? = actionsStorage[key]

    fun addLinks(keys: List<String>, links: List<SirenLink>?) {
        if (links == null) return
        keys.forEach { key -> addLink(key = key, link = links.find { it.rel.contains(key) }) }
    }

    fun addActions(keys: List<String>, actions: List<SirenAction>?) {
        if (actions == null) return
        keys.forEach { key -> addAction(key = key, action = actions.find { it.title == key }) }
    }

    suspend fun ensureLink(key: String, fetchLink: suspend () -> Any): SirenLink? {
        val link = getLink(key)
        if (link == null) {
            fetchLink()
        }
        return link
    }

    suspend fun ensureAction(key: String, fetchAction: suspend () -> Any): SirenAction? {
        if (getAction(key) == null) {
            fetchAction()
        }
        return getAction(key)
    }
}
