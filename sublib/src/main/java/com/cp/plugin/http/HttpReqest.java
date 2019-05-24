package com.cp.plugin.http;

import com.alibaba.fastjson.annotation.JSONField;
import com.blankj.utilcode.util.LogUtils;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpReqest {

    private int id;
    private String url;
    private String method;
    private Map<String, String> header;
    private byte[] body;


    @JSONField(serialize = false)
    SubHttp http;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHttp(SubHttp http) {
        this.http = http;

        this.http.getClientBuilder().followRedirects(false);
        this.http.getClientBuilder().followSslRedirects(false);

    }

    public SubResponse call() throws IOException {

        RequestBody requestBody = null;

        if (body != null && body.length > 0) {
            requestBody = RequestBody.create(MediaType.parse(header.get("Content-Type")), body);
        }


        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(method, requestBody);

        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        SubResponse response = http.execute(builder);

        response.setId(id);


        return response;
    }
}
