package com.mos.lib.common.http


import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.LogUtils
import com.mos.lib.common.util.StringUtil

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.io.IOException
import java.net.HttpCookie
import java.util.ArrayList
import java.util.HashMap

import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer

class SubResponse {

    var id: Long = 0

    var code: Int = 0
        private set

    private var headers: Map<String, List<String>>? = null

    private var bodyRaw: ByteArray? = null

    private var body: String? = null

    private val response: Response

    private val urls: List<String>

    val time: Long


    constructor(id: Long, url: String, throwable: Throwable) {
        this.id = id
        this.code = 520
        this.headers = HashMap()
        val content = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\"" +
                "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                "<title>ERROR</title>" +
                "</head>" +
                "<body>" +
                "<div>" + url + "</div>" +
                "<div>" + throwable.message + "</div>" +
                "</body>" +
                "</html>"
        this.bodyRaw = content.toByteArray()

    }

    @Throws(IOException::class)
    constructor(response: Response, urls: List<String>, time: Long) {

        this.response = response

        this.time = time

        this.urls = urls

        this.code = response.code()
        this.headers = response.headers().toMultimap()

        val responseBody = response.body()

        if (responseBody != null) {

            this.bodyRaw = responseBody.bytes()

            val headers = this.headers!!["Content-Type"]

            if (headers != null && headers.size > 0) {

                val content = headers[0]

                if (content.contains("text/html") || content.contains("text/plain")) {
                    this.body = String(this.bodyRaw, "UTF-8")
                }
            }

        }
    }

    fun url(): String {
        return this.response.request().url().toString()
    }

    fun body(): String? {
        return body
    }

    fun response(): Response {
        return response
    }

    fun doc(): Document {
        return Jsoup.parse(body!!)
    }

    fun cookie(): String {

        val cookies = response.headers("Set-Cookie")

        val stringList = ArrayList<String>()

        if (cookies != null) {
            for (stirng in cookies) {
                for (cookie in HttpCookie.parse(stirng)) {
                    stringList.add(cookie.name + "=" + cookie.value)
                }
            }
        }
        return StringUtil.join(";", stringList)
    }

    fun flowUrls(): String {
        val flow = StringBuilder()
        for (i in urls.indices) {
            flow.append(i + 1).append(". ").append(urls[i]).append("\n")
        }

        return flow.toString()
    }

    private fun reqeustBody(): String? {
        var body: String? = null
        try {

            val requestBody = response.request().body()

            if (requestBody != null) {

                val buffer = Buffer()
                requestBody.writeTo(buffer)

                body = buffer.readUtf8()
            }

        } catch (e: Exception) {
            LogUtils.e(e)
        }

        return body
    }

    fun json(): String {

        val map = HashMap<String, Any>()
        map["id"] = this.id
        map["code"] = this.code
        map["headers"] = this.headers!!
        map["body"] = this.bodyRaw!!


        return JSON.toJSONString(map)

    }


    override fun toString(): String {


        val request = response.request()

        val url = request.url().toString()
        val method = request.method()
        val headers = request.headers()


        val line = "---------------------------------------------------------------------------"


        val c1 = String.format("\n%s %s\n%s\n\n%s\n", method, url, headers, reqeustBody())
        val c2 = String.format("%s\n%s\n\n%s\n", code, response.headers(), body)


        val line2 = "---------------- urls ----------------"


        return String.format("%s\n%s\n%s\n%s\n%s\n%s\ntime: %ss\n", c1, line2, flowUrls(), line, c2, line, time / 1000.0)
    }
}
