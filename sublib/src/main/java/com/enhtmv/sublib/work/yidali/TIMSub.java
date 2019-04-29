package com.enhtmv.sublib.work.yidali;

import com.enhtmv.sublib.common.SubCall;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
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

    private Map<String, String> header;


    public TIMSub(SubEvent event) {
        super("http://54.153.76.222:8081", "tim", event);


        header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (Linux; Android 7.0; PLUS Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.98 Mobile Safari/537.36");
        header.put("DNT", "1");
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


    @Override
    public void sub(String meta) {


        SubHttp http = http();

        try {

            String host = "vastracking.tim.it";


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
                report.w("tim_request1_error", response);
                return;
            }


            String aiUser = newid() + "|" + isoDate();

            http.setCookie(host, "CLIENT_BINFO", "B-N");
            http.setCookie(host, "CLIENTX", "725");
            http.setCookie(host, "CLIENTY", "400");
            http.setCookie(host, "ai_user", aiUser);

            Date date = new Date();

            String aiSession = newid() + "|" + date.getTime() + "|" + date.getTime();

            http.setCookie("vastracking.tim.it", "ai_session", aiSession);

            header.put("Referer", referer);

            //点击 第一次ajax
            response = http.get(url + "&=1556096956016", header);

            if (!"OK".equals(response.body())) {
                report.w("tim_request2_error", response);
                return;
            }


            SubLog.i("ai_user", aiUser);
            SubLog.i("ai_session", aiSession);


            url = url.replace("&sc=T", "");

            //点击 第二次跳转
            response = http.get(url, header);

            Element element = response.doc().select("a[target=_parent]").first();

            if (element == null) {
                report.w("tim_request3_error", response);
                return;
            }

//            String subUrl = element.attr("href");
            String subUrl = parseUrl(response);

            http.setCookie(host, "CLIENT_BINFO", "B-N");
            http.setCookie(host, "CLIENTX", "725");
            http.setCookie(host, "CLIENTY", "400");


            header.put("Referer", url);

            response = http.get(subUrl, header);

            if (!"OK".equals(response.body())) {

                report.w("tim_request4_error", response);

                return;

            }


            response = http.get(subUrl.replace("&sc=T", ""), header);


        } catch (Exception e) {
            SubLog.e(e);
        }


    }

    @Override
    public void onSub(String message) {
    }
}
