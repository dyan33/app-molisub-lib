package com.enhtmv.sublib.common;


import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;

import java.io.IOException;

public abstract class SubCall {


    protected String androidId;

    protected String packageName;

    protected String host;

    protected SubCallBack successCall;

    protected SubReport report;

    private Proxy proxy;

    private boolean log;

    public SubCall(String host) {
        this.host = host;
    }

    void init(String packageName, String androidId, SubCallBack successCall) {

        this.packageName = packageName;
        this.androidId = androidId;
        this.successCall = successCall;

        this.report = new SubReport(this.host, androidId, packageName);

    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public void setProxy(String host, String user, String password, int port) {
        this.proxy = new Proxy(host, user, password, port);
    }

    protected String meta() throws IOException {
        SubResponse response = http().get(host + "/app/meta?pname=" + packageName + "&aid=" + androidId);

        return response.body();
    }

    protected SubHttp http() {

        SubHttp http = new SubHttp();

        http.setLog(this.log);

        if (this.proxy != null) {
            http.setProxy(proxy.host, proxy.user, proxy.passwd, proxy.port);
        }


        return http;
    }

    public SubReport r() {
        return this.report;
    }

    public abstract void call();

    public abstract void call(String message);

    private class Proxy {
        String host, user, passwd;
        int port;

        public Proxy(String host, String user, String passwd, int port) {
            this.host = host;
            this.user = user;
            this.passwd = passwd;
            this.port = port;
        }
    }

}
