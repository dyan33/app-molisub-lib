package com.enhtmv.sublib.common;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.util.NetUtil;
import com.enhtmv.sublib.common.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubReport {

    private static final String ERROR = "ERROR";
    private static final String WARNING = "WARNING";
    private static final String INFO = "INFO";
    private static final String SUCCESS = "SUCCESS";

    private static SubReport report;


    private String androidId;
    private String packageName;
    private String host;
    private String version;
    private String sdkVersion;
    private String deviceName;
    private String operatorName;
    private String operatorCode;


    private SubHttp http;


    private SubReport(String host) {

        this.host = host;

        this.androidId = DeviceUtils.getAndroidID();
        this.packageName = AppUtils.getAppPackageName();
        this.version = AppUtils.getAppVersionName();
        this.sdkVersion = DeviceUtils.getSDKVersionName();
        this.deviceName = DeviceUtils.getModel();
        this.operatorName = NetworkUtils.getNetworkOperatorName();
        this.operatorCode = NetUtil.getOperator();

        this.http = new SubHttp();
    }


    public static void init(String host) {


        synchronized (SubReport.class) {

            if (report == null) {

                report = new SubReport(host);

            }

        }

    }

    public static SubReport getReport() {
        return report;
    }


    public void r(final String level, final String tag, final String info) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault());

        final String date = sdf.format(new Date());


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Map<String, String> body = new HashMap<>();
                    body.put("android_id", androidId);
                    body.put("level", level);
                    body.put("info", info == null ? "" : info);
                    body.put("date", date);
                    body.put("tag", tag);
                    body.put("version", version);
                    body.put("sdk_version", sdkVersion);
                    body.put("device_name", deviceName);
                    body.put("operator_name", operatorName == null ? "" : operatorName);
                    body.put("operator_code", operatorCode == null ? "" : operatorCode);
                    body.put("network", NetUtil.getNetworkName());


                    String json = JSON.toJSONString(body);


                    http.postJson(host + "/app/log?name=" + packageName, json);
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }
        }).start();

    }

    public void i(String tag) {
        r(INFO, tag, null);
    }

    public void i(String tag, Object object) {
        r(INFO, tag, object.toString());
    }

    public void w(String tag) {
        r(WARNING, tag, null);
    }

    public void w(String tag, Object object) {

        r(WARNING, tag, object.toString());

    }

    public void e(String tag) {
        r(ERROR, tag, null);
    }

    public void e(String tag, Throwable throwable) {

        String stack = StringUtil.getStackTrace(throwable);

        r(ERROR, tag, stack);
    }

    public void s(String message) {
        r(SUCCESS, message, null);
    }

    public void s(String tag, Object object) {
        r(SUCCESS, tag, object.toString());
    }

    public String info() {

        String content = null;

        try {
            SubResponse response = http.get(host + "/app/meta?pname=" + packageName + "&aid=" + androidId);

            content = response.body();

            if (!StringUtil.isEmpty(content)) {

                String[] array = content.split("\\|");

                if (array.length == 2) {

                    StringBuilder meta = new StringBuilder();

                    int num = Integer.parseInt(array[0]);

                    while (num-- > 0) {
                        meta.append("=");
                    }

                    meta.append(array[1]);

                    content = new String(EncodeUtils.base64Decode(meta.reverse().toString()));

                }
            }

        } catch (Exception e) {
            LogUtils.e(e);
        }
        return content;

    }

}
