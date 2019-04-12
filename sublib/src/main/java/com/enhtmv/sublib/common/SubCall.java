package com.enhtmv.sublib.common;


import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.util.SubLog;

import java.io.IOException;

public abstract class SubCall {


    protected String androidId;

    protected String packageName;

    protected String host;

    protected SubCallBack<String> successCall;

    protected SubReport report;

    private SubProxy proxy;

    private boolean log;

    public SubCall(String host) {
        this.host = host;
    }

    void init(String packageName, String androidId, SubCallBack<String> successCall) {

        this.packageName = packageName;
        this.androidId = androidId;
        this.successCall = successCall;

        this.report = new SubReport(this.host, androidId, packageName);

    }

    public void setLog(boolean log) {
        this.log = log;
        SubLog.setLog(log);
    }

    public void setProxy(SubProxy proxy) {
        this.proxy = proxy;
    }

    public String meta() throws IOException {
        SubResponse response = http().get(host + "/app/meta?pname=" + packageName + "&aid=" + androidId);

        return response.body();
    }

    protected SubHttp http() {

        SubHttp http = new SubHttp();

        http.setLog(this.log);

        if (this.proxy != null) {
            http.setProxy(proxy);
        }


        return http;
    }

    public SubReport r() {
        return this.report;
    }

    public abstract void call();

    public abstract void call(String message);


}
