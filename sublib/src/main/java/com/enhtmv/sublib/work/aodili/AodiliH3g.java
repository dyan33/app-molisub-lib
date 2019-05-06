package com.enhtmv.sublib.work.aodili;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.enhtmv.sublib.common.sub.SubCall;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.util.StringUtil;

import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * 奥地利H3G
 */
public class AodiliH3g extends SubCall {


    private Info info;

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


    @Override
    public void sub(String metaText) {


        if (this.ok) {

            this.ok = false;

            SubHttp http = http();

            try {


                report(SUB_REQEUST);


                SubResponse response = http.get("http://www.gogamehub.com/at/lp?aff=zcj&dvid=" + androidId);

                String ptxid = response.doc().select("#ptxid").first().text();

                if (StringUtil.isEmpty(ptxid)) {

                    r.w("h3g_step1_err", response);

                    throw new RetryException(120);

                }

                report("step1", "time: " + response.getTime() / 1000.0);


                response = http.get("http://cpx5.allcpx.com:8088/subscript/request/" + ptxid);


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

                    info = new Info(cookie, action, referer, values);
                }

                if (values.isEmpty()) {

                    r.w("h3g_step2_err", response);

                    throw new RetryException(120);

                }

                report("step2", "time: " + response.getTime() / 1000.0);

            } catch (Exception e) {
                this.ok = true;

                if (e instanceof RetryException) {
                    sub(metaText);
                }
                r.e("h3g_err", e);
                event.onError(e);

            }
        }


    }

    @Override
    public synchronized void onSub(String message) {

        if (info != null) {

            report(RECEIVE_SMS, message);

            if (!TextUtils.isEmpty(message) && message.length() > 10) {

                String code = message.substring(5, 10);

                if (!TextUtils.isEmpty(code)) {

                    //一定要去掉空格！！！
                    code = code.trim();


                    try {

                        Integer.parseInt(code);

                    } catch (NumberFormatException e) {

                        r.w("h3g_message_parse_err", e);

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

                        SubResponse response = http().postForm(info.action, header, info.form);


                        report("step3", "time: " + response.getTime() / 1000.0);


                        if (response.response().request().url().toString().startsWith("http://www.gogamehub.com/nm/at")) {

                            success();

                        } else {
                            r.w("h3g_step3_err", response);
                        }


                    } catch (Exception e) {
                        r.e("sub_requst_message_error", e);
                        event.onError(e);
                        LogUtils.e(e);

                    }
//                    finally {
//                        info = null;
//                        ok = true;
//                    }

                }

            }
        }
    }


}
