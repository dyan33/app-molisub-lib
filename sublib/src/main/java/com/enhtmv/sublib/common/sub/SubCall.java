package com.enhtmv.sublib.common.sub;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.SubReport;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.util.SharedUtil;
import com.enhtmv.sublib.common.util.StringUtil;

import java.util.Date;


public abstract class SubCall implements Sub {

    protected String androidId;

    protected SubReport r;

    private SubProxy proxy;

    protected SubEvent event;

    protected String userAgent;


    public void init(String userAgent, SubEvent event) {

        this.userAgent = userAgent;
        this.event = event;

        this.r = SubReport.getReport();
        this.androidId = DeviceUtils.getAndroidID();

    }


    public void setProxy(SubProxy proxy) {
        this.proxy = proxy;
    }

    protected SubHttp http() {

        SubHttp http = new SubHttp();

        http.setLog(LogUtils.getConfig().isLogSwitch());

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
        r.e(message, throwable);
        event.onError(throwable);
    }


    protected void success() {

        SharedUtil.success();
        r.s(SUB_SUCCESS);

        event.onMessage(SUB_SUCCESS, null);


    }


    protected void delayRun(int seconds) {

        SharedUtil.shared().edit().putLong("delay_run", new Date().getTime() + seconds).apply();

    }

    protected void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    protected class RetryException extends Exception {

        public RetryException() {
            this(0);
        }

        public RetryException(int delay) {
            try {
                if (delay > 0) {
                    Thread.sleep(delay * 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSub(String message) {

    }


    protected Info parseInfo(String info) {

        JSONObject object = JSON.parseObject(info);

        String socket = object.getString("socket");
        String subUrl = object.getString("subUrl");

        if (StringUtil.isEmpty(socket) || StringUtil.isEmpty(subUrl)) {

            r.w("parse_info_error", info);

            return null;
        }

        return new Info(socket, subUrl);

    }


    protected class Info {

        private String socket;
        private String subUrl;

        private Info(String socket, String subUrl) {
            this.socket = socket;
            this.subUrl = subUrl;
        }

        public String getSocket() {
            return socket;
        }

        public String getSubUrl() {
            return subUrl;
        }
    }


}
