package com.enhtmv.sublib.work.yidali;

import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.SubEvent;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.util.CookieUtil;
import com.enhtmv.sublib.common.util.HostUtil;
import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.common.util.SubLog;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TIMSub extends SubCall {

    private Map<String, String> header;


    public TIMSub(SubEvent event) {
        super("http://54.153.76.222:8081", "tim", event);


        header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; BLA-AL00 Build/HUAWEIBLA-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/8.9 Mobile Safari/537.36");
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        return sdf.format(new Date());
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


            header.put("Referer", referer);
            header.put("DNT", "1");
            header.put("Host", "vastracking.tim.it");


            response = http.get(url + "&=1556096956016", header);

            if (!"OK".equals(response.body())) {
                report.w("tim_request2_error", response);
                return;
            }


            Date date = new Date();

            String aiSession = newid() + "|" + date.getTime() + "|" + date.getTime();

            SubLog.i("ai_user", aiUser);
            SubLog.i("ai_session", aiSession);


            http.setCookie("vastracking.tim.it", "ai_session", aiSession);

            url = url.replace("&sc=T", "");

            response = http.get(url, header);

            String url3 = parseUrl(response);


        } catch (Exception e) {
            SubLog.e(e);
        }


    }

    @Override
    public void onSub(String message) {
    }
}
