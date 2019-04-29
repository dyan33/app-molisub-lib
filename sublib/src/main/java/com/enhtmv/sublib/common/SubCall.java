package com.enhtmv.sublib.common;


import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.util.SubLog;


public abstract class SubCall {


    protected String androidId;

    protected String packageName;

    protected String host;

    protected SubCallBack<String> successCall;

    protected SubReport report;

    private SubProxy proxy;

    protected SubEvent event;

    protected String operatorName;

    private boolean log;

    public SubCall(String host, String operatorName, SubEvent subEvent) {
        this.host = host;
        this.operatorName = operatorName;
        this.event = subEvent;
    }

    void init(String packageName, String androidId, SubCallBack<String> successCall) {

        this.packageName = packageName;
        this.androidId = androidId;
        this.successCall = successCall;

        this.report = SubReport.getReport();

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
        report(tag, operatorName);
    }

    public void report(String tag, String info) {


        report.s(tag, info);

        event.onMessage(tag, info);


    }

    public abstract void sub(String meta);

    public abstract void onSub(String message);


}
