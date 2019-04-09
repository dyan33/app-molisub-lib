package com.enhtmv.sublib.common.service;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.enhtmv.sublib.common.SubContext;
import com.enhtmv.sublib.common.util.SubLog;


public class SubNotificationService extends NotificationListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
        SubLog.d("SubNotificationService onCreate !!!");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {


        if ("com.google.android.apps.messaging".equals(sbn.getPackageName())) {
            Bundle bundle = sbn.getNotification().extras;


//            String title = bundle.getString(Notification.EXTRA_TITLE, "");
            String content = bundle.getString(Notification.EXTRA_TEXT, "");

            SubLog.d("receive notification message:", content);

            SubContext.call(content);
        }

    }


}
