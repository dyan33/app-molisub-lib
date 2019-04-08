package com.enhtmv.subsdk.common;


import java.io.IOException;

public abstract class SubCall extends SubHttp {


    protected String androidId;

    protected String packageName;

    protected String host;

    protected SubCallBack successCall;

    protected SubReport report;


    public SubCall(String host) {
        this.host = host;
    }

    void init(String packageName, String androidId, SubCallBack successCall) {

        this.packageName = packageName;
        this.androidId = androidId;
        this.successCall = successCall;

        this.report = new SubReport(this.host, androidId, packageName);

    }


    protected String meta() throws IOException {
        MyResponse response = get(host + "/app/meta?pname=" + packageName + "&aid=" + androidId);

        return response.body();
    }


    public SubReport r() {
        return this.report;
    }

    public abstract void call();

    public abstract void call(String message);

}
