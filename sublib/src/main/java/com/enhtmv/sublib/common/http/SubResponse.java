package com.enhtmv.sublib.common.http;

import com.blankj.utilcode.util.LogUtils;
import com.enhtmv.sublib.common.util.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class SubResponse {

    private String body;
    private Response response;

    private long time;


    public SubResponse(Response response, long time) throws IOException {
        this.response = response;

        ResponseBody responseBody = response.body();

        this.body = responseBody != null ? responseBody.string() : "";

        this.time = time;


    }


    public String url() {
        return this.response.request().url().toString();
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

    private String reqeustBody() {
        String body = null;
        try {

            RequestBody requestBody = response.request().body();

            if (requestBody != null) {

                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                body = buffer.readUtf8();
            }

        } catch (Exception e) {
            LogUtils.e(e);
        }

        return body;
    }


    @Override
    public String toString() {


        Request request = response.request();

        String url = request.url().toString();
        String method = request.method();
        Headers headers = request.headers();


        String c1 = String.format("\n%s %s\n%s\n\n%s\n", method, url, headers, reqeustBody());
        String c2 = String.format("%s\n%s\n\n%s\n", response.code(), response.headers(), body);

        String line = "---------------------------------------------------------------------------";

        return String.format("%s\n%s\n%s\n%s\ntime: %ss\n", c1, line, c2, line, time / 1000.0);
    }
}
