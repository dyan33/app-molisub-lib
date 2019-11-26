package com.mos.lib.common.util

import com.mos.lib.common.http.SubResponse

import java.net.HttpCookie
import java.util.ArrayList

object CookieUtil {


    fun parse(str: String): String {


        val cookies = ArrayList<String>()

        for (httpCookie in HttpCookie.parse(str)) {

            val cookie = String.format("%s=%s", httpCookie.name, httpCookie.value)
            cookies.add(cookie)
        }

        return StringUtil.join(";", cookies)

    }


    fun parse(sub: SubResponse): String {

        val cookies = ArrayList<String>()

        for (str in sub.response().headers("Set-Cookie")) {
            cookies.add(parse(str))
        }

        return StringUtil.join(";", cookies)

    }


}
