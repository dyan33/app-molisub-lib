package com.cp.plugin.service;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.blankj.utilcode.util.LogUtils;
import com.enhtmv.sublib.common.sub.SubContext;


public class SubNotificationService extends NotificationListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("SubNotificationService onCreate !!!");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {


        Bundle bundle = sbn.getNotification().extras;

        String content = bundle.getString(Notification.EXTRA_TEXT, "");

        LogUtils.d("receive notification message:", content);

        SubContext.call(content);

    }


}
