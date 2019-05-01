package com.enhtmv.sublib.common.sub;


import com.blankj.utilcode.util.DeviceUtils;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.SubReport;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.util.SharedUtil;
import com.enhtmv.sublib.common.util.SubLog;


public abstract class SubCall implements Sub {

    protected String androidId;

    protected SubReport r;

    private SubProxy proxy;

    protected SubEvent event;

    protected String userAgent;

    private boolean log;

    public void init(String userAgent, SubEvent event) {

        this.userAgent = userAgent;
        this.event = event;

        this.r = SubReport.getReport();
        this.androidId = DeviceUtils.getAndroidID();

    }

    public void setLog(boolean log) {
        this.log = log;
        SubLog.setLog(log);
    }

    public void setProxy(SubProxy proxy) {
        this.proxy = proxy;
    }

    protected SubHttp http() {

        SubHttp http = new SubHttp();

        http.setLog(this.log);

        if (this.proxy != null) {
            http.setProxy(proxy);
        }


        return http;
    }


    public void report(String tag) {
        report(tag, "");
    }

    public void report(String tag, String info) {

        r.i(tag, info);

        event.onMessage(tag, info);

    }

    public void report(String message, Throwable throwable) {
        SubLog.e(throwable);
        r.e(message, throwable);
        event.onError(throwable);
    }


    protected void success() {

        SubLog.i("success !!!");

        SharedUtil.success();
        event.onMessage(SUB_SUCCESS, null);
        r.s(SUB_SUCCESS);

    }


    public abstract void sub(String info);

    public abstract void onSub(String message);


}
