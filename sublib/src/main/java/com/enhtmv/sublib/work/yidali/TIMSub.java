package com.enhtmv.sublib.work.yidali;

import com.enhtmv.sublib.common.sub.SubCall;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.util.RandomUtil;
import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.common.util.SubLog;

import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TIMSub extends SubCall {


    @Override
    public synchronized void sub(String meta) {


        SubHttp http = http();

        String host = "vastracking.tim.it";

        String y = RandomUtil.i(750, 1300) + "";
        String x = RandomUtil.i(400, 700) + "";

        Map<String, String> header = new HashMap<>();
        header.put("DNT", "1");
        header.put("User-Agent", this.userAgent);


        try {

            report("start_sub", meta);


            SubResponse response = http.get("http://offer.allcpx.com/offer/track?offer=219&pubId={pub_id}&clickId=" + androidId, header);
//            SubResponse response = http.get("http://lit.gbbgame.com/sub/req", header);


            String nextUrl = StringUtil.findByReg("<meta http-equiv=\"refresh\" content=\"0;URL='(.*)'\" />", response.body());

            header.put("Host", "vastracking.tim.it");
            if (!StringUtil.isEmpty(nextUrl)) {

                nextUrl = nextUrl.replace("amp;", "");

                response = http.get(nextUrl, header);


            }


            String referer = response.response().request().url().toString();


            String url = parseUrl(response);

            if (StringUtil.isEmpty(url)) {
                r.w("tim_request1_error", response);
                return;
            }
            report("step1");
            sleep();


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
                return;
            }

            report("step2");
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
                return;
            }
            report("step3");
            sleep();

//            String subUrl = element.attr("href");
            String subUrl = parseUrl(response);

            http.setCookie(host, "CLIENT_BINFO", "B-N");
            http.setCookie(host, "CLIENTX", x);
            http.setCookie(host, "CLIENTY", y);


            header.put("Referer", url);

            response = http.get(subUrl, header);

            if (!"OK".equals(response.body()) || subUrl == null) {

                r.w("tim_request4_error", response);

                return;

            }
            report("step4");
            response = http.get(subUrl.replace("&sc=T", ""), header);


            String successUrl = response.response().request().url().toString();


            if (successUrl.startsWith("http://offer.globaltraffictracking.com/sub_track")) {

                success();
                return;
            }

            r.w("tim_request5_error", response);


        } catch (Exception e) {
            report("tim_error", e);
            SubLog.e(e);
        }


    }

    @Override
    public void onSub(String message) {
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


        int num = RandomUtil.i(1000, 2500);

        Thread.sleep(num);

    }
}
