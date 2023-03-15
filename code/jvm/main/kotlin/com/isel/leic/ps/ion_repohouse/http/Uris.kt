package com.isel.leic.ps.ion_repohouse.http

import java.net.URI

object Uris {

    private const val API ="/api"

    private const val WEB = "$API/web"
    private const val MOBILE = "$API/mobile"


    /** Common Uris **/

    const val HOME = "$API/"
    const val AUTH_PATH = "$API/auth"
    const val CALLBACK_PATH = "$AUTH_PATH/callback"


    /** Web Uris **/


    /** Mobile Uris **/



    /** Functions Uris **/

    fun homeUri():URI = URI(HOME)
    fun authUri():URI = URI(AUTH_PATH)
    fun callbackUri():URI = URI(CALLBACK_PATH)
}
