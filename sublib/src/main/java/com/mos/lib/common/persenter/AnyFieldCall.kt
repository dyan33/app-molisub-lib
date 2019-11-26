package com.mos.lib.common.persenter


import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.cp.log.event.LogEvent
import com.mos.lib.common.Report
import com.mos.lib.common.http.SubProxy
import com.mos.lib.common.http.SubHttp
import com.mos.lib.common.util.SharedUtil
import com.mos.lib.common.util.StringUtil

import java.util.Date


abstract class AnyFieldCall : AnyField {

    private lateinit var androidId: String

    lateinit var r: Report

    private var proxy: SubProxy? = null

    private lateinit var event: LogEvent

    private lateinit var userAgent: String

    protected lateinit var operator: String


    fun init(userAgent: String, operator: String, event: LogEvent) {

        this.userAgent = userAgent
        this.event = event

        this.r = Report.report!!
        this.androidId = DeviceUtils.getAndroidID()
        this.operator = operator

    }


    fun setProxy(proxy: SubProxy) {
        this.proxy = proxy
    }

    protected fun http(): SubHttp {

        val http = SubHttp()

        http.setLog(LogUtils.getConfig().isLogSwitch)

        //这里proxy 在SubContext 的initSubCall方法中赋值，仅调试设置
        if (this.proxy != null) {

            http.setProxy(proxy)
        }

        return http
    }

    @JvmOverloads
    fun report(tag: String, info: String = "") {

        r.i(tag, info)

        event.onMessage(tag, info)

    }

    fun report(message: String, throwable: Throwable) {
        r.e(message, throwable)
        event.onError(throwable)
    }


    protected fun success() {

        SharedUtil.success()
        r.s(AnyField.Companion.SUB_SUCCESS)

        event.onMessage(AnyField.Companion.SUB_SUCCESS, null!!)


    }


    protected fun delayRun(seconds: Int) {

        SharedUtil.shared().edit().putLong("delay_run", Date().time + seconds).apply()

    }

    protected fun sleep(seconds: Int) {
        try {
            Thread.sleep((seconds * 1000).toLong())
        } catch (e: Exception) {
            LogUtils.e(e)
        }

    }

    protected inner class RetryException @JvmOverloads constructor(delay: Int = 0) : Exception() {

        init {
            try {
                if (delay > 0) {
                    Thread.sleep((delay * 1000).toLong())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onSub(message: String) {

    }


    protected fun parseInfo(info: String): Info? {

        val `object` = JSON.parseObject(info)

        val socket = `object`.getString("socket")
        val subUrl = `object`.getString("subUrl")

        if (StringUtil.isEmpty(socket) || StringUtil.isEmpty(subUrl)) {

            r.w("parse_info_error", info)

            return null
        }

        return Info(socket, subUrl)

    }


    protected inner class Info(val socket: String, val subUrl: String)


}
