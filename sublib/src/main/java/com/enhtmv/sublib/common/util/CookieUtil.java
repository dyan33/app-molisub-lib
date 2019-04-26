package com.enhtmv.sublib.common.util;

import com.enhtmv.sublib.common.SubReport;
import com.enhtmv.sublib.common.http.SubResponse;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

public class CookieUtil {


    public static String parse(String str) {


        List<String> cookies = new ArrayList<>();

        for (HttpCookie httpCookie : HttpCookie.parse(str)) {

            String cookie = String.format("%s=%s", httpCookie.getName(), httpCookie.getValue());
            cookies.add(cookie);
        }

        return StringUtil.join(";", cookies);

    }


    public static String parse(SubResponse sub) {

        List<String> cookies = new ArrayList<>();

        for (String str : sub.response().headers("Set-Cookie")) {
            cookies.add(parse(str));
        }

        return StringUtil.join(";", cookies);

    }


}
