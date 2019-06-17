package com.cp.plugin;

import android.content.Context;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.sub.SubContext;
import com.enhtmv.sublib.common.http.SubProxy;

public class Plugin {


    private static boolean hiden = true;


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

    public static void init(Context context, SubEvent event) {
//        init(context, event, null);
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


}
