package com.enhtmv.sublib.common.http;

import com.enhtmv.sublib.common.util.SubLog;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class SubHttp {

    private OkHttpClient.Builder clientBuilder;

    private Map<String, Map<String, Cookie>> cookieMap = new HashMap<>();

    private boolean log;


    public SubHttp() {

        clientBuilder = new OkHttpClient.Builder();

        clientBuilder.connectTimeout(60, TimeUnit.SECONDS);
        clientBuilder.readTimeout(60, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(60, TimeUnit.SECONDS);

        clientBuilder.cookieJar(new CookieJar() {


            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

                String host = url.host();

                if (!cookieMap.containsKey(host)) {
                    cookieMap.put(host, new HashMap<String, Cookie>());
                }


                Map<String, Cookie> map = cookieMap.get(host);

                for (Cookie cookie : cookies) {

                    map.put(cookie.name(), cookie);

                }


            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {


                String host = url.host();


                List<Cookie> cookies = new ArrayList<>();


                Map<String, Cookie> map = cookieMap.get(host);

                if (map != null && map.size() > 0) {
                    cookies.addAll(map.values());
                }


                SubLog.i("loading cookie!", url, cookies);

                return cookies;
            }
        });

    }


    public void setCookie(String host, String name, String vaule) {

        if (!cookieMap.containsKey(host)) {
            cookieMap.put(host, new HashMap<String, Cookie>());
        }


        cookieMap.get(host).put(name, new Cookie.Builder().name(name).value(vaule).domain(host).build());


    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public void setProxy(final SubProxy xy) {

        SubLog.d("set proxy !", xy);

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(xy.getHost(), xy.getPort()));

        clientBuilder.proxy(proxy)
                .proxyAuthenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) {
                        String credential = Credentials.basic(xy.getUsername(), xy.getPassword());
                        return response.request().newBuilder()
                                .header("Proxy-Authorization", credential)
                                .build();
                    }
                });

    }


    private Response execute(Request request) throws IOException {


        return clientBuilder.build().newCall(request).execute();
    }


    public SubResponse get(String url, Map<String, String> header, boolean allowRedirect) throws IOException {

        Request.Builder builder = new Request.Builder().url(url);

        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        clientBuilder.followRedirects(allowRedirect);
        clientBuilder.followRedirects(allowRedirect);


        Request request = builder.build();

        long start = System.currentTimeMillis();

        Response response = execute(request);

        long end = System.currentTimeMillis();


        SubResponse m = new SubResponse(response, end - start);

        if (log)
            SubLog.d("\n", m);

        return m;
    }

    public SubResponse get(String url, Map<String, String> header) throws IOException {

        return get(url, header, true);
    }

    public SubResponse get(String url, boolean allowRedirect) throws IOException {

        return get(url, null, allowRedirect);
    }

    public SubResponse get(String url) throws IOException {
        return get(url, null, true);
    }

    public SubResponse post(String url, Map<String, String> body) throws IOException {
        return post(url, null, body);
    }

    public SubResponse post(String url, Map<String, String> header, Map<String, String> body, boolean allowRedirect) throws IOException {


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


        long start = System.currentTimeMillis();

        Response response = execute(builder.build());

        long end = System.currentTimeMillis();

        SubResponse myResponse = new SubResponse(response, body, end - start);

        if (log)
            SubLog.d("\n", myResponse);

        return myResponse;
    }

    public SubResponse post(String url, Map<String, String> header, Map<String, String> body) throws IOException {
        return post(url, header, body, true);
    }


}
