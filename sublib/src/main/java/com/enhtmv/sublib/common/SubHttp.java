package com.enhtmv.sublib.common;

import com.enhtmv.sublib.common.util.SubLog;
import com.enhtmv.sublib.common.util.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Authenticator;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;

public class SubHttp {

    private OkHttpClient.Builder clientBuilder;


    private boolean log;


    public SubHttp() {

        clientBuilder = new OkHttpClient.Builder();


        clientBuilder.cookieJar(new CookieJar() {


            private Map<String, List<Cookie>> cookieMap = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

                SubLog.d("saveFromResponse", url, cookies);

                String host = url.host();

                if (!cookieMap.containsKey(host)) {
                    cookieMap.put(host, new ArrayList<Cookie>());
                }

                cookieMap.get(host).addAll(cookies);

            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {


                SubLog.d("loadForRequest", url);

                List<Cookie> cookieList = cookieMap.get(url.host());


                return cookieList != null ? cookieList : new ArrayList<Cookie>();
            }
        });

    }


    public void setHttpLog(boolean log) {
        this.log = log;
    }

    public void setProxy(String hostname, final String username, final String password, int port) {

        SubLog.d("set proxy !", hostname, username, password, port);

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port));

        clientBuilder.proxy(proxy)
                .proxyAuthenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) {
                        String credential = Credentials.basic(username, password);
                        return response.request().newBuilder()
                                .header("Proxy-Authorization", credential)
                                .build();
                    }
                });

    }


    protected MyResponse get(String url, Map<String, String> header, boolean allowRedirect) throws IOException {

        Request.Builder builder = new Request.Builder().url(url);

        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        clientBuilder.followRedirects(allowRedirect);
        clientBuilder.followRedirects(allowRedirect);


        Request request = builder.build();

        Response response = clientBuilder.build().newCall(request).execute();


        MyResponse m = new MyResponse(response);

        if (log)
            SubLog.d("\n", m);

        return m;
    }

    protected MyResponse get(String url, Map<String, String> header) throws IOException {

        return get(url, header, true);
    }

    protected MyResponse get(String url, boolean allowRedirect) throws IOException {

        return get(url, null, allowRedirect);
    }

    protected MyResponse get(String url) throws IOException {
        return get(url, null, true);
    }

    protected MyResponse form(String url, Map<String, String> body) throws IOException {
        return form(url, null, body);
    }

    protected MyResponse form(String url, Map<String, String> header, Map<String, String> body, boolean allowRedirect) throws IOException {


        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, String> entry : body.entrySet()) {
            formBody.add(entry.getKey(), entry.getValue());
        }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(formBody.build());


        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        clientBuilder.followRedirects(allowRedirect);
        clientBuilder.followSslRedirects(allowRedirect);


        Response response = clientBuilder.build().newCall(builder.build()).execute();

        MyResponse myResponse = new MyResponse(response, body);

        if (log)
            SubLog.d("\n", myResponse);

        return myResponse;
    }

    protected MyResponse form(String url, Map<String, String> header, Map<String, String> body) throws IOException {
        return form(url, header, body, true);
    }

    public class MyResponse {

        private String body;
        private Response response;
        private Map<String, String> formbody;

        private MyResponse(Response response) throws IOException {
            this.response = response;

            ResponseBody responseBody = response.body();

            body = responseBody != null ? responseBody.string() : "";

        }

        private MyResponse(Response response, Map<String, String> formbody) throws IOException {
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

}
