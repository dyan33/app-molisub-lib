package com.cp.log.event

import com.alibaba.fastjson.annotation.JSONField
import com.blankj.utilcode.util.LogUtils
import com.mos.lib.common.http.SubHttp
import com.mos.lib.common.http.SubResponse

import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody

class HttpReqest {

    private var id: Long = 0
    private var url: String? = null
    private var method: String? = null
    private var header: Map<String, String>? = null
    private var body: ByteArray? = null


    internal lateinit var http: SubHttp

    fun setUrl(url: String) {
        this.url = url
    }

    fun setMethod(method: String) {
        this.method = method
    }

    fun setHeader(header: Map<String, String>) {
        this.header = header
    }

    fun setBody(body: ByteArray) {
        this.body = body
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun setHttp(http: SubHttp) {
        this.http = http

        this.http.clientBuilder.followRedirects(false)
        this.http.clientBuilder.followSslRedirects(false)

    }

    fun call(): String {

        try {

            var requestBody: RequestBody? = null

            if (body != null && body!!.isNotEmpty()) {
                requestBody = RequestBody.create(MediaType.parse(header!!["Content-Type"] ?: error("")), body!!)
            } else if ("POST" == method) {
                requestBody = RequestBody.create(null, "")
            }


            val builder = Request.Builder()
                    .url(url!!)
                    .method(method!!, requestBody)

            //header中能获取服务端所有的浏览器环境
            for ((key, value) in header!!) {
                builder.addHeader(key, value)
            }


            val response = http.execute(builder)
            response.id = id
            return response.json()

        } catch (e: Exception) {

            LogUtils.e(e)

            return SubResponse(id, url!!, e).json()
        }

    }
}
