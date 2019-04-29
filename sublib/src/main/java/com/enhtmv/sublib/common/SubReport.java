package com.enhtmv.sublib.common;

import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.common.util.SubLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SubReport {


    private static final String spearator = "\n------\n";

    private static final String ERROR = "ERROR";
    private static final String WARNING = "WARNING";
    private static final String INFO = "INFO";
    private static final String SUCCESS = "SUCCESS";

    private static SubReport report;

    private String androidId;
    private String packageName;
    private String host;
    private String version;

    private SubHttp http;


    public static void init(String host, String androidId, String packageName, String version) {

        if (report == null) {

            report = new SubReport();

            report.androidId = androidId;
            report.packageName = packageName;
            report.host = host;
            report.version = version;
            report.http = new SubHttp();
        }

    }

    public static SubReport getReport() {
        return report;
    }


    public void r(final String level, final String info) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final String date = sdf.format(new Date());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    Map<String, String> body = new HashMap<>();
                    body.put("android_id", androidId);
                    body.put("package_name", packageName);
                    body.put("level", level);
                    body.put("info", info);
                    body.put("date", date);
                    body.put("version", version);

                    http.post(host + "/app/log", body);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void i(String message) {
        r(INFO, message);
    }

    public void i(String message, Object object) {
        r(INFO, message + spearator + object);
    }

    public void w(String message) {
        r(WARNING, message);
    }

    public void w(String message, Object object) {

        r(WARNING, message + spearator + object);

    }

    public void e(String message) {
        r(ERROR, message);
    }

    public void e(String message, Throwable throwable) {


        String stack = StringUtil.getStackTrace(throwable);

        r(ERROR, message + spearator + stack);
    }

    public void s(String message) {
        r(SUCCESS, message);
    }

    public void s(String message, Object object) {
        r(SUCCESS, message + spearator + object);
    }


    public String info() {

        String content = null;

        try {
            SubResponse response = http.get(host + "/app/meta?pname=" + packageName + "&aid=" + androidId);

            content = response.body();

        } catch (Exception e) {
            SubLog.e(e);
        }
        return content;

    }

}
