package com.cp.plugin;

import android.content.Context;

import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.sub.SubContext;
import com.enhtmv.sublib.common.http.SubProxy;

public class Plugin {


    public static void init(Context context, SubEvent event) {

        SubContext.init(context, event);

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
