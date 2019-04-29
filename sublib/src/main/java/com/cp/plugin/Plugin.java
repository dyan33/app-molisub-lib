package com.cp.plugin;

import android.content.Context;

import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.SubContext;
import com.enhtmv.sublib.common.SubEvent;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.work.yidali.TIMSub;

public class Plugin {

    private SubCall s1;
    private SubContext s2;


    public Plugin(Context context, SubEvent event) {

        this.s1 = new TIMSub(event);
        this.s2 = new SubContext(context, s1);


    }

    public void log(boolean log) {
        this.s1.setLog(log);

    }

    public void proxy(String host, String user, String password, int port) {
        this.s1.setProxy(new SubProxy(host, user, password, port));
    }

    public void closeWifi(boolean wifi) {
        this.s2.setCloseWifi(wifi);
    }


    public void call() {
        this.s2.call();
    }


}
