package com.enhtmv.sublib.work.spain;

import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;

import java.io.IOException;
import java.util.Map;

public class RequestObj {

    String method;
    String url;
    Map<String, String> headers;
    Map<String, String> form;

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


    public SubResponse call(SubHttp http) throws IOException {

        if ("GET".equals(method)) {

            return http.get(url, this.headers);

        } else if ("POST".equals(method)) {

            return http.postForm(url, this.headers, this.form);
        }

        return null;
    }

    @Override
    public String toString() {
        return "RequestObj{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", headers=" + headers +
                ", form=" + form +
                '}';
    }
}
