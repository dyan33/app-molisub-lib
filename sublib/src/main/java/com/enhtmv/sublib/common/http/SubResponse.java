package com.enhtmv.sublib.common.http;


import com.alibaba.fastjson.annotation.JSONField;
import com.blankj.utilcode.util.LogUtils;
import com.enhtmv.sublib.common.util.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class SubResponse {

    private int id;

    private int code;

    private Map<String, List<String>> headers;

    @JSONField(name = "body")
    private byte[] bodyRaw;

    @JSONField(serialize = false)
    private String body;

    @JSONField(serialize = false)
    private Response response;

    @JSONField(serialize = false)
    private List<String> urls;

    @JSONField(serialize = false)
    private long time;


    public SubResponse(int id, String url, Throwable throwable) {


        this.id = id;
        this.code = 555;
        this.headers = new HashMap<>();
        String content = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\"" +
                "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                "<title>ERROR</title>" +
                "</head>" +
                "<body>" +
                "<div>" + url + "</div>" +
                "<div>" + throwable.getMessage() + "</div>" +
                "</body>" +
                "</html>";
        this.bodyRaw = content.getBytes();

    }

    public SubResponse(Response response, List<String> urls, long time) throws IOException {

        this.response = response;

        this.time = time;

        this.urls = urls;

        this.code = response.code();
        this.headers = response.headers().toMultimap();


        ResponseBody responseBody = response.body();

        if (responseBody != null) {

            this.bodyRaw = responseBody.bytes();

            List<String> headers = this.headers.get("Content-Type");

            if (headers != null && headers.size() > 0) {
                if (headers.get(0).contains("text/html")) {
                    this.body = new String(this.bodyRaw, "UTF-8");
                }
            }

        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public byte[] getBodyRaw() {
        return bodyRaw;
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
        String c2 = String.format("%s\n%s\n\n%s\n", code, response.headers(), body);


        String line2 = "---------------- urls ----------------";


        return String.format("%s\n%s\n%s\n%s\n%s\n%s\ntime: %ss\n", c1, line2, flowUrls(), line, c2, line, time / 1000.0);
    }
}
