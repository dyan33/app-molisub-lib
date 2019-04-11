package com.enhtmv.sublib.work;

import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.util.HostUtil;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 波兰Orange
 */
public class BolanOrange extends SubCall {


    public BolanOrange() {
        super(HostUtil.host());
    }

    @Override
    public void call() {


        SubHttp http = http();


        try {

            Element form = http.get("http://pl.stbody.com/fit/sub").doc().select("form").first();

            String action = form.attr("action");

            StringBuilder query = new StringBuilder();
            for (Element input : form.select("input")) {

                String key = input.attr("name");
                String value = input.attr("value");

                query.append(key).append("=").append(value).append("&");
            }

            String url1=action + "?" + query.toString();


            http.get(url1);




        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void call(String message) {

    }
}
