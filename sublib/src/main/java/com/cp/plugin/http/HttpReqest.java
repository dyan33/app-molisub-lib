package com.cp.plugin.http;

import com.alibaba.fastjson.annotation.JSONField;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;

import java.io.IOException;
import java.util.Map;

public class HttpReqest {

    String method;
    String url;
    Map<String, String> headers;
    Map<String, String> form;

    @JSONField(serialize = false)
    SubHttp http;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getForm() {
        return form;
    }

    public void setForm(Map<String, String> form) {
        this.form = form;
    }

    public void setHttp(SubHttp http) {
        this.http = http;
    }

    public SubResponse call() throws IOException {

        if ("GET".equals(method)) {

            return http.get(url, this.headers);

        } else if ("POST".equals(method)) {

            return http.postForm(url, this.headers, this.form);
        }

        return null;
    }
}
