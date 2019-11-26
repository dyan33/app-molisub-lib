package com.cp.log;

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.widget.RelativeLayout

import com.cp.log.event.LogEvent
import com.mos.lib.common.http.SubProxy
import com.mos.lib.common.persenter.ContextCenter

@SuppressLint("StaticFieldLeak")
object Log4js {

    var isHiden = true

    var isPermission = false

    private var mContext: Context? = null

    private var alertDialogBuilder: AlertDialog.Builder? = null

    fun init(context: Context) {
        init(context, object : LogEvent {
            override fun onMessage(tag: String, content: String) {
            }

            override fun onError(throwable: Throwable) {
            }

        })
    }

    /**
     * 调用初始化init方法
     *
     * @param context
     * @param event
     */
    fun init(context: Context, event: LogEvent) {
        mContext = context
        ContextCenter.init(context, event)
    }

    fun init(context: Context, event: LogEvent, layout: RelativeLayout) {
        ContextCenter.init(context, event, layout)
    }


    fun log(log: Boolean) {
        ContextCenter.log(log)

    }

    fun operator(code: String) {
        ContextCenter.setOperator(code)
    }

    fun proxy(host: String, user: String, password: String, port: Int) {
        ContextCenter.proxy(SubProxy(host, user, password, port))
    }

    fun closeWifi(wifi: Boolean) {
        ContextCenter.closeWifi(wifi)
    }


    fun call() {
        ContextCenter.call()
    }

    fun showDialog() {
        if (alertDialogBuilder != null) {
            alertDialogBuilder!!.show()
        }
    }
}
