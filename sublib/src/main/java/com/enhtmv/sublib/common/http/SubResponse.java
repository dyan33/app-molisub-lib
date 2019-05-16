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
    private List<String> urls;

    private long time;


    public SubResponse(Response response, List<String> urls, long time) throws IOException {

        this.response = response;
        this.time = time;
        this.urls = urls;

        ResponseBody responseBody = response.body();

        this.body = responseBody != null ? responseBody.string() : "";


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

    public String flowUrls() {
        StringBuilder flow = new StringBuilder();
        for (int i = 0; i < urls.size(); i++) {
            flow.append(i + 1).append(". ").append(urls.get(i)).append("\n");
        }

        return flow.toString();
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


        String line = "---------------------------------------------------------------------------";


        String c1 = String.format("\n%s %s\n%s\n\n%s\n", method, url, headers, reqeustBody());
        String c2 = String.format("%s\n%s\n\n%s\n", response.code(), response.headers(), body);


        String line2 = "---------------- urls ----------------";


        return String.format("%s\n%s\n%s\n%s\n%s\n%s\ntime: %ss\n", c1, line2, flowUrls(), line, c2, line, time / 1000.0);
    }
}
