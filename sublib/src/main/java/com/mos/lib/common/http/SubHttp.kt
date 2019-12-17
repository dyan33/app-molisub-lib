package com.mos.lib.common.http

import com.blankj.utilcode.util.LogUtils
import com.mos.lib.common.util.StringUtil


import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.TimeUnit

import okhttp3.Authenticator
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.Route

object SubHttp {

    val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

    private val cookieMap = HashMap<String, Map<String, Cookie>>()

    private val urls = ArrayList<String>()

    private var log: Boolean = false

    private var timeout = 30

    init {

        //创建OkHttpclient实例
        //连接时长
        clientBuilder.connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
        //读取超时
        clientBuilder.readTimeout(timeout.toLong(), TimeUnit.SECONDS)
        //写入超时
        clientBuilder.writeTimeout(timeout.toLong(), TimeUnit.SECONDS)
        //cookie监听
        clientBuilder.cookieJar(object : CookieJar {


            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {

                val host = url.host()

                if (!cookieMap.containsKey(host)) {
                    cookieMap[host] = HashMap()
                }


                var map = cookieMap[host]

                for (cookie in cookies) {
                    map = mapOf(cookie.name() to cookie)
                }


            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {

                val host = url.host()

                val cookies = ArrayList<Cookie>()

                val map = cookieMap[host]

                if (map != null && map.isNotEmpty()) {
                    cookies.addAll(map.values)
                }


                //LogUtils.i("load cookie", url, cookies);

                return cookies
            }
        })

        //添加网络拦截器用于保存地址
        clientBuilder.addNetworkInterceptor { chain ->
            val request = chain.request()

            //保存URL
            urls.add(request.url().toString())

            chain.proceed(request)
        }

    }

    fun setTimeout(timeout: Int) {
        this.timeout = timeout
    }

    fun setCookie(host: String, name: String, value: String) {

        if (!cookieMap.containsKey(host)) {
            cookieMap[host] = HashMap()

        }
        cookieMap[host]?.mapValues { name to Cookie.Builder().name(name).value(value).domain(host).build() }

    }

    fun containsCookie(host: String, name: String): Boolean {

        val map = cookieMap[host]

        return map != null && map.containsKey(name)
    }

    fun setLog(log: Boolean) {
        this.log = log
    }

    fun setProxy(xy: SubProxy) {

        LogUtils.d("set proxy !", xy)

        val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(xy.host, xy.port))

        clientBuilder.proxy(proxy)

        val username = xy.username
        val password = xy.password

        if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(password)) {
            clientBuilder.proxyAuthenticator { _, response ->
                val credential = Credentials.basic(username, password)
                response.request().newBuilder().header("Proxy-Authorization", credential).build()
            }
        }

    }

    @Throws(IOException::class)
    @JvmOverloads
    fun execute(builder: Request.Builder, header: Map<String, String>? = null): SubResponse {
        urls.clear()

        //设置请求头
        if (header != null) {
            for ((key, value) in header) {
                builder.addHeader(key, value)
            }
        }


        val t1 = System.currentTimeMillis()

        val response = clientBuilder.build().newCall(builder.build()).execute()

        val t2 = System.currentTimeMillis()

        val subResponse = SubResponse(response, urls, t2 - t1)

        if (log) {
            LogUtils.d(subResponse)
        }


        return subResponse
    }

    @Throws(IOException::class)
    @JvmOverloads
    operator fun get(url: String, header: Map<String, String>? = null, allowRedirect: Boolean = true): SubResponse {

        val builder = Request.Builder().url(url)


        clientBuilder.followRedirects(allowRedirect)
        clientBuilder.followRedirects(allowRedirect)


        return execute(builder, header)
    }

    @Throws(IOException::class)
    fun postForm(url: String, form: Map<String, String>): SubResponse {
        return postForm(url, null, form)
    }

    @Throws(IOException::class)
    @JvmOverloads
    fun postForm(url: String, header: Map<String, String>?, form: Map<String, String>, allowRedirect: Boolean = true): SubResponse {


        val formBody = FormBody.Builder()
        for ((key, value) in form) {
            formBody.add(key, value)
        }

        val builder = Request.Builder()
                .url(url)
                .post(formBody.build())


        clientBuilder.followRedirects(allowRedirect)
        clientBuilder.followSslRedirects(allowRedirect)


        return execute(builder, header)
    }

    @Throws(IOException::class)
    fun options(url: String, header: Map<String, String>): SubResponse {

        val builder = Request.Builder()
                .url(url)
                .method("OPTIONS", null)


        return execute(builder, header)


    }

    @Throws(IOException::class)
    fun postJson(url: String, json: String): SubResponse {
        return postJson(url, null, json)
    }

    @Throws(IOException::class)
    fun postJson(url: String, header: Map<String, String>?, json: String): SubResponse {

        val builder = Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))


        return execute(builder, header)


    }

    fun getUrls(): List<String> {
        return urls
    }
}
