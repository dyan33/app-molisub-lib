package com.enhtmv.sublib.work.yidali;

import com.blankj.utilcode.util.LogUtils;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.sub.SubCall;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.util.RandomUtil;
import com.enhtmv.sublib.common.util.StringUtil;

import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TIMSub extends SubCall {


    private int times;

    private String x;
    private String y;
    private Map<String, String> header;
    private String host;


    @Override
    public void init(String userAgent, SubEvent event) {
        super.init(userAgent, event);


        host = "vastracking.tim.it";

        y = RandomUtil.i(750, 1300) + "";
        x = RandomUtil.i(400, 700) + "";

        header = new HashMap<>();
        header.put("DNT", "1");


        String ua = "Mozilla/5.0 (Linux; Android 9; Android SDK built for x86 Build/PSR1.180720.075; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/73.0.3683.90 Mobile Safari/537.36";

//        ua = "Mozilla/5.0 (Linux; Android 8.0.0; FIG-LX1 Build/HUAWEIFIG-LX1; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/73.0.3683.90 Mobile Safari/537.36";

        header.put("User-Agent", ua);
        times = 0;
    }

    @Override
    public void sub(String info) {

        sub(0);

    }

    @Override
    public void onSub(String message) {

    }


    private void sub(int delay) {

        try {

            if (delay > 0) {
                Thread.sleep(delay);
            }

            if (++times > 2) {
                LogUtils.w("retry already max times !!!", times);
                return;
            }

            SubHttp http = http();

            report(SUB_REQEUST, "times: " + times);


            SubResponse response = http.get("http://offer.allcpx.com/offer/track?offer=219&pubId={pub_id}&clickId=" + androidId, header);
//            SubResponse response = http.get("http://lit.gbbgame.com/sub/req", header);


            //40秒休息
            long time = response.getTime();
            if (time < 30000) {
                Thread.sleep(30000 - time);
            }


            report("step1.1", "time: " + response.getTime() / 1000.0);

            //=====================================页面解析跳转=======================================

            String nextUrl = StringUtil.findByReg("<meta http-equiv=\"refresh\" content=\"0;URL='(.*)'\" />", response.body());
            if (!StringUtil.isEmpty(nextUrl)) {

                nextUrl = nextUrl.replace("amp;", "");

                response = http.get(nextUrl, header);

            }


            header.put("Host", "vastracking.tim.it");

            String referer = response.response().request().url().toString();


            String url = parseUrl(response);

            if (StringUtil.isEmpty(url)) {

                r.w("tim_request1_error", response);

                throw new RetryException(response);
            }
            report("step1.2", "time: " + response.getTime() / 1000.0);
            sleep();


            //=====================================请求第二个页面=====================================

            String aiUser = newid() + "|" + isoDate();

            http.setCookie(host, "CLIENT_BINFO", "B-N");
            http.setCookie(host, "CLIENTX", x);
            http.setCookie(host, "CLIENTY", y);
            http.setCookie(host, "ai_user", aiUser);


            header.put("Referer", referer);

            //点击 第一次ajax
            response = http.get(url, header);

            if (!"OK".equals(response.body())) {
                r.w("tim_request2_error", response);
                throw new RetryException(response);
            }

            report("step2", "time: " + response.getTime() / 1000.0);
            sleep();


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
                throw new RetryException(response);
            }


            report("step3", "time: " + response.getTime() / 1000.0);
            sleep();


            //------------------------OPTIONS---------------
//            {
//                Map<String, String> oHeader = new HashMap<>();
//
//                oHeader.put("Host", "dc.services.visualstudio.com");
//                oHeader.put("Referer", "http://vastracking.tim.it/");
//                oHeader.put("Origin", "http://vastracking.tim.it");
//                oHeader.put("DNT", "1");
//                oHeader.put("User-Agent", this.userAgent);
//
//
//                http.options("https://dc.services.visualstudio.com/v2/track", oHeader);
//
//            }


            //=====================================订阅请求==========================================

//            String subUrl = element.attr("href");
            String subUrl = parseUrl(response);

            http.setCookie(host, "CLIENT_BINFO", "B-N");
            http.setCookie(host, "CLIENTX", x);
            http.setCookie(host, "CLIENTY", y);


            sleep();

            header.put("Referer", url);

            response = http.get(subUrl, header);

            if (!"OK".equals(response.body()) || subUrl == null) {

                r.w("tim_request4_error", response);

                throw new RetryException(response);

            }


            report("step4", "time: " + response.getTime() / 1000.0);
            response = http.get(subUrl.replace("&sc=T", ""), header);


            String successUrl = response.response().request().url().toString();


            if (successUrl.startsWith("http://offer.globaltraffictracking.com/sub_track")) {

                success();
                return;
            }

            r.w("tim_request5_error", response);

            throw new RetryException(response);


        } catch (Exception e) {

            if (e instanceof RetryException) {
                //10秒后重试
//                sub(60 * 1000);
            } else {
                report("tim_error", e);
                LogUtils.e(e);
            }

        }

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


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
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
        private RetryException(SubResponse response) {
            super(response.toString());
        }
    }

}
