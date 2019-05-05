package com.enhtmv.sublib.work.yidali;

import com.blankj.utilcode.util.LogUtils;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.sub.SubCall;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.util.RandomUtil;
import com.enhtmv.sublib.common.util.SharedUtil;
import com.enhtmv.sublib.common.util.StringUtil;

import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TIMSub extends SubCall {


    private String x;
    private String y;
    private Map<String, String> header;
    private String host;
    private int times = 0;


    @Override
    public void init(String userAgent, SubEvent event) {
        super.init(userAgent, event);


        host = "vastracking.tim.it";

        y = RandomUtil.i(750, 1300) + "";
        x = RandomUtil.i(400, 700) + "";

    }

    @Override
    public void onSub(String message) {

    }

    @Override
    public void sub(String info) {

        SubHttp http = http();

        header = new HashMap<>();
        header.put("DNT", "1");
        header.put("User-Agent", userAgent);

        try {

            report(SUB_REQEUST, ++times + "");


            SubResponse response = http.get("http://offer.allcpx.com/offer/track?offer=219&pubId={pub_id}&clickId=" + androidId, header);
//            SubResponse response = http.get("http://lit.gbbgame.com/sub/req", header);

            report("step1.1", "time: " + response.getTime() / 1000.0);

            //=====================================页面解析跳转=======================================

            String nextUrl = StringUtil.findByReg("<meta http-equiv=\"refresh\" content=\"0;URL='(.*)'\" />", response.body());
            if (!StringUtil.isEmpty(nextUrl)) {

                nextUrl = nextUrl.replace("amp;", "");

                response = http.get(nextUrl, header);
                report("step1.2", "time: " + response.getTime() / 1000.0);

            }


            String url = parseUrl(response);

            if (StringUtil.isEmpty(url)) {

                r.w("tim_request1_error", response);
                throw new RetryException(response.response().code() == 404 ? 0 : 60);
            }


            sleep();
            sleep();
            sleep();


            //=====================================请求第二个页面=====================================

            //----------------------------------- ok -----------------------------------

            String aiUser = newid() + "|" + isoDate();

            http.setCookie(host, "CLIENT_BINFO", "B-N");
            http.setCookie(host, "CLIENTX", x);
            http.setCookie(host, "CLIENTY", y);
            http.setCookie(host, "ai_user", aiUser);


            String referer = response.response().request().url().toString();

            header.put("Host", "vastracking.tim.it");
            header.put("Referer", referer);

            //点击 第一次ajax
            response = http.get(url, header);

            if (!"OK".equals(response.body())) {
                r.w("tim_request2_error", response);
                throw new RetryException(120);
            }

            report("step2", "time: " + response.getTime() / 1000.0);
            sleep();


            //----------------------------------- click -----------------------------------


            url = url.replace("&sc=T", "");

            //ai_session
            Date date = new Date();

            String aiSession = newid() + "|" + date.getTime() + "|" + date.getTime();

            http.setCookie("vastracking.tim.it", "ai_session", aiSession);


            //点击 第二次跳转
            response = http.get(url, header);

            Element element = response.doc().select("a[target=_parent]").first();

            if (element == null) {
                r.w("tim_request3_error", response);
                throw new RetryException(120);
            }


            report("step3", "time: " + response.getTime() / 1000.0);
            sleep();
            sleep();


            //=====================================订阅请求==========================================

            //----------------------------------- ok -----------------------------------

//            String subUrl = element.attr("href");
            String subUrl = parseUrl(response);

            http.setCookie(host, "CLIENT_BINFO", "B-N");
            http.setCookie(host, "CLIENTX", x);
            http.setCookie(host, "CLIENTY", y);


            header.put("Referer", url);

            //ok
            response = http.get(subUrl, header);

            if (!"OK".equals(response.body()) || subUrl == null) {

                r.w("tim_request4_error", response);

                throw new RetryException(120);

            }

            report("step4", "time: " + response.getTime() / 1000.0);
            sleep();

            //----------------------------------- click -----------------------------------

            response = http.get(subUrl.replace("&sc=T", ""), header);

            String successUrl = response.response().request().url().toString();

            if (subOk(response)) {
                return;

            } else if (successUrl.startsWith("http://api.servicelayer.mobi/pro/timokfrm.ashx")) {

                for (int i = 0; i < 5; i++) {
                    sleep();

                    response = http.get(successUrl);

                    r.i("step5." + i, response);

                    if (subOk(response)) {
                        return;
                    }
                }
            }

            r.w("tim_request5_error", response);

            throw new RetryException(120);


        } catch (Exception e) {

            http.clearCookie("offer.allcpx.com");

            if (e instanceof RetryException) {

                sub(info);

            } else {
                report("tim_error", e);
                LogUtils.e(e);
            }
        }

    }


    private boolean subOk(SubResponse response) throws Exception {

        String url = response.response().request().url().toString();

        if (url.startsWith("http://offer.globaltraffictracking.com/sub_track") || url.startsWith("http://lit.gbbgame.com")) {

            sleep();

            success();
            return true;

        } else if (url.startsWith("https://li.lpaosub.com/already_sub218")) {

            sleep();

            r.s(ALREADY_SUB, url);
            SharedUtil.success();

            event.onMessage(ALREADY_SUB, "");

            return true;
        }


        return false;


    }


    private String newid() {
        double t = 1073741824 * Math.random();

        String string = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";


        StringBuilder sb = new StringBuilder();

        while (t > 0.0) {

            sb.append(string.charAt((int) t % 64));

            t = Math.floor(t / 64);


        }
        return sb.toString();
    }


    private String isoDate() {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));


        return sdf.format(new Date()) + "Z";
    }


    private String parseUrl(SubResponse response) {


        Pattern pattern = Pattern.compile("document\\.getElementById\\('(.*)'\\)\\.value");

        Matcher matcher = pattern.matcher(response.body());

        if (matcher.find()) {

            String text = matcher.group(1);

            String[] array = text.split("'\\).value\\+document\\.getElementById\\('");

            StringBuilder urlBuilder = new StringBuilder();

            for (String str : array) {

                urlBuilder.append(response.doc().select("#" + str).attr("value"));

            }
            urlBuilder.append("&bInfo=B-N").append("&sc=T");

            return urlBuilder.toString();

        }
        return null;
    }

    private void sleep() throws Exception {

        int num = RandomUtil.i(2000, 2500);

        Thread.sleep(num);

    }

    private class RetryException extends Exception {

        private RetryException(int delay) {
            try {
                if (delay > 0) {
                    Thread.sleep(delay * 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
