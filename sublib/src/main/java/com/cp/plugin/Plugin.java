package com.cp.plugin;

import android.content.Context;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.sub.SubContext;
import com.enhtmv.sublib.common.http.SubProxy;

public class Plugin {


    private static boolean hiden = true;

    public static void init(Context context, SubEvent event, ViewGroup viewGroup) {

        SubContext.init(context, event, viewGroup);


    }


    public static void init(Context context, SubEvent event) {
        init(context, event, null);
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
