package com.enhtmv.sublib.common.http;

import com.enhtmv.sublib.common.util.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SubResponse {

    private String body;
    private Response response;
    private Map<String, String> formbody;

    private long time;


    public SubResponse(Response response, long time) throws IOException {
        this.response = response;

        ResponseBody responseBody = response.body();

        this.body = responseBody != null ? responseBody.string() : "";

        this.time = time;


    }

    public SubResponse(Response response, Map<String, String> formbody, long time) throws IOException {
        this(response, time);
        this.formbody = formbody;
    }


    public String body() {
        return body;
    }

    public Response response() {
        return response;
    }

    public Document doc() {
        return Jsoup.parse(body);
    }

    public long getTime() {
        return time;
    }

    public String cookie() {

        List<String> cookies = response.headers("Set-Cookie");

        List<String> stringList = new ArrayList<>();

        if (cookies != null) {
            for (String stirng : cookies) {
                for (HttpCookie cookie : HttpCookie.parse(stirng)) {
                    stringList.add(cookie.getName() + "=" + cookie.getValue());
                }
            }
        }
        return StringUtil.join(";", stringList);
    }

    @Override
    public String toString() {


        Request request = response.request();

        String url = request.url().toString();
        String method = request.method();
        Headers reqHeaders = request.headers();

        String reqStr = String.format("\n%s %s\n%s\n\n%s\n", method, url, reqHeaders, formbody);
        String respStr = String.format("%s\n%s\n\n%s\n", response.code(), response.headers(), body);

        String line = "-------------------------------------";

        return String.format("%s\n%s\n%s\n%s\ntime: %s", reqStr, line, respStr, line, time / 1000.0);
    }
}
