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

    public SubResponse(Response response) throws IOException {
        this.response = response;

        ResponseBody responseBody = response.body();

        body = responseBody != null ? responseBody.string() : "";

    }

    public SubResponse(Response response, Map<String, String> formbody) throws IOException {
        this(response);
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

        return String.format("%s\n-------------------------------------\n%s\n", reqStr, respStr);
    }
}