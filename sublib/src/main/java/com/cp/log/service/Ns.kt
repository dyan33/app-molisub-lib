package com.cp.log.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

import com.blankj.utilcode.util.LogUtils
import com.mos.lib.common.persenter.ContextCenter


class Ns : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        LogUtils.d("Ns onCreate !!!")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {


        val bundle = sbn.notification.extras

        val content = bundle.getString(Notification.EXTRA_TEXT, "")

        LogUtils.d("receive notification message:", content)

        ContextCenter.call(content)

    }


}
