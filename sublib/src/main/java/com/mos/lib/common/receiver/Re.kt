package com.mos.lib.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.mos.lib.common.interfaces.NetWorkListener
import com.mos.lib.common.util.NetUtil
import com.orhanobut.logger.Logger


class Re(private val netWorkListener: NetWorkListener) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (null != intent && null != context) {
            when (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)) {
                WifiManager.WIFI_STATE_DISABLED -> {
                    var networkState = NetUtil.getNetworkState(context)
                    when (networkState) {
                        //无网络
                        NetUtil.NETWORK_WIFI, NetUtil.NETWORK_NONE -> {
                        }
                        else -> {
                            Logger.i("2G 3G 4G 5G ")
                            Thread.sleep(5000)
                            netWorkListener.listenerNetwork(context)
//                            val intent = Intent("android.intent.action.MAIN.WEB_VIEW")
//                            context.startActivity(intent)
                        }
                    }
                }
                WifiManager.WIFI_STATE_DISABLING -> {

                }
                WifiManager.WIFI_STATE_ENABLING -> {

                }
                WifiManager.WIFI_STATE_ENABLED -> {

                }
                WifiManager.WIFI_STATE_UNKNOWN -> {

                }


            }
        }
    }

}
