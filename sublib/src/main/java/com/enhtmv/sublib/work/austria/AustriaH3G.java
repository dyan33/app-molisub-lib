package com.enhtmv.sublib.work.austria;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.enhtmv.sublib.common.sub.SubCall;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;

import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * 奥地利H3G
 */
public class AustriaH3G extends SubCall {

    private final static String baseUrl = "http://nat.allcpx.com/sub/start?affName=DCG&clickId=";

    private SubMeta meta;

    private boolean ok = true;


    @Override
    public void sub(String info) {


        Map<String, String> header = new HashMap<>();

        header.put("User-Agent", userAgent);


        if (this.ok) {

            this.ok = false;

            SubHttp http = http();

            try {


                report(SUB_REQEUST);


                SubResponse response = http.get(baseUrl + androidId, header);


                Element form = response.doc().select("form").first();

                Map<String, String> values = new HashMap<>();

                if (form != null) {

                    String action = form.attr("action");
                    String referer = response.response().request().url().toString();
                    String cookie = response.cookie();

                    for (Element input : form.select("input")) {

                        String key = input.attr("name");
                        String value = input.attr("value");

                        values.put(key, value);
                    }

                    meta = new SubMeta(cookie, action, referer, values);
                }

                if (values.isEmpty()) {

                    r.w("step1_error", response);

                    throw new RetryException(60);

                }

                report("step1", "time: " + response.getTime() / 1000.0);

            } catch (Exception e) {
                this.ok = true;

                if (e instanceof RetryException) {
                    sub(info);
                }
                r.e("step1_exception", e);

            }
        }


    }

    @Override
    public synchronized void onSub(String message) {

        if (meta != null) {

            report(RECEIVE_SMS, message);

            if (!TextUtils.isEmpty(message) && message.length() > 10) {

                String code = message.substring(5, 10);

                if (!TextUtils.isEmpty(code)) {

                    //一定要去掉空格！！！
                    code = code.trim();


                    try {

                        Integer.parseInt(code);

                    } catch (Exception e) {

                        r.w("message_parse_error", code);

                        return;
                    }


                    meta.form.put("pin", code);

                    Map<String, String> header = new HashMap<>();

                    String im1 = "com.silverpop.iMAWebCookie=" + UUID.randomUUID().toString();
                    String im2 = "com.silverpop.iMA.session=" + UUID.randomUUID().toString();
                    String im3 = "com.silverpop.iMA.page_visit=" + "1969604377:";


                    header.put("Cookie", meta.cookie + ";" + im1 + ";" + im2 + ";" + im3);
                    header.put("Referer", meta.referer);


                    if (!meta.form.containsKey("email")) {
                        meta.form.put("email", this.androidId + "@gmail.com");

                    }


                    try {

                        SubResponse response = http().postForm(meta.action, header, meta.form);


                        if (response.response().request().url().toString().startsWith("http://www.gogamehub.com/nm/at")) {

                            success();

                        } else {
                            r.w("step2_error", response);
                        }


                    } catch (Exception e) {
                        r.e("step2_exception", e);
                        LogUtils.e(e);

                    } finally {
                        meta = null;
                        ok = true;
                    }

                }

            }
        }
    }


    public class SubMeta {
        String cookie;
        String action;
        Map<String, String> form;
        String referer;

        private SubMeta(String cookie, String action, String referer, Map<String, String> form) {
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
}
