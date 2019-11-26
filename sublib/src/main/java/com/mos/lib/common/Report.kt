package com.mos.lib.common

import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.mos.lib.common.http.SubHttp
import com.mos.lib.common.http.SubResponse
import com.mos.lib.common.util.NetUtil
import com.mos.lib.common.util.StringUtil

import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale

class Report
/**
 * 传递客户端必要上报信息
 * @param host = http://52.53.238.169:8081
 */
private constructor(private val host: String) {


    private val androidId: String
    private val packageName: String
    private val version: String
    private val sdkVersion: String
    private val deviceName: String
    private val operatorName: String?
    private val operatorCode: String?


    private val http: SubHttp


    init {
        this.androidId = DeviceUtils.getAndroidID()
        this.packageName = AppUtils.getAppPackageName()
        this.version = AppUtils.getAppVersionName()
        this.sdkVersion = DeviceUtils.getSDKVersionName()
        this.deviceName = DeviceUtils.getModel()
        this.operatorName = NetworkUtils.getNetworkOperatorName()
        this.operatorCode = NetUtil.operator
        this.http = SubHttp()
    }

    fun r(level: String, tag: String, info: String?) {

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault())

        val date = sdf.format(Date())

        Thread(Runnable {
            try {
                val body = HashMap<String, String>()
                body["android_id"] = androidId
                body["level"] = level
                body["info"] = info ?: ""
                body["date"] = date
                body["tag"] = tag
                body["version"] = version
                body["sdk_version"] = sdkVersion
                body["device_name"] = deviceName
                body["operator_name"] = operatorName ?: ""
                body["operator_code"] = operatorCode ?: ""
                body["network"] = NetUtil.networkName

                val json = JSON.toJSONString(body)
                http.postJson("$host/app/log?name=com.mos.lib.common", json)
            } catch (e: Exception) {
                LogUtils.e(e)
            }
        }).start()

    }

    fun i(tag: String) {
        r(INFO, tag, null)
    }

    fun i(tag: String, `object`: Any) {
        r(INFO, tag, `object`.toString())
    }

    fun w(tag: String) {
        r(WARNING, tag, null)
    }

    fun w(tag: String, `object`: Any) {

        r(WARNING, tag, `object`.toString())

    }

    fun e(tag: String) {
        r(ERROR, tag, null)
    }

    fun e(tag: String, throwable: Throwable) {

        val stack = StringUtil.getStackTrace(throwable)

        r(ERROR, tag, stack)
    }

    fun s(message: String) {
        r(SUCCESS, message, null)
    }

    fun s(tag: String, `object`: Any) {
        r(SUCCESS, tag, `object`.toString())
    }

    /**
     * 封装日志上报所有信息以及解密过程
     * @return
     */
    fun info(): String? {
        var content: String? = null

        try {
            val response = http.get("$host/app/meta?pname=com.mos.lib.common&aid=$androidId")

            content = response.body()

            if (!StringUtil.isEmpty(content)) {

                val array = content!!.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                if (array.size == 2) {

                    val meta = StringBuilder()

                    var num = Integer.parseInt(array[0])

                    while (num-- > 0) {
                        meta.append("=")
                    }

                    meta.append(array[1])

                    content = String(EncodeUtils.base64Decode(meta.reverse().toString()))

                }
            }

        } catch (e: Exception) {
            LogUtils.e(e)
        }

        return content

    }

    companion object {

        private val ERROR = "ERROR"
        private val WARNING = "WARNING"
        private val INFO = "INFO"
        private val SUCCESS = "SUCCESS"

        var report: Report? = null
            private set


        /**
         * 日志上报初始化
         * @param host
         */
        fun init(host: String) {

            synchronized(Report::class.java) {

                if (report == null) {

                    //创建实例，并传入上报地址
                    report = Report(host)

                }

            }

        }
    }

}
