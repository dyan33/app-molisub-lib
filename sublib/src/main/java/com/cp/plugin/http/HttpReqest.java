package com.cp.plugin.http;

import com.alibaba.fastjson.annotation.JSONField;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpReqest {


    private String url;
    private String method;
    private Map<String, String> headers;
    private String body;


    @JSONField(serialize = false)
    SubHttp http;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHttp(SubHttp http) {
        this.http = http;
    }

    public SubResponse call() throws IOException {

        RequestBody requestBody = RequestBody.create(MediaType.parse(headers.get("Content-Type")), body);


        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(method, requestBody);

        for (Map.Entry<String, String> entry : headers.entrySet()) {

            if ("Content-Type".equals(entry.getKey())) {
                continue;
            }

            builder.addHeader(entry.getKey(), entry.getValue());

        }

        return http.execute(builder);
    }
}
