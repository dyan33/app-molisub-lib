package com.enhtmv.sublib.common.http;

import com.enhtmv.sublib.common.util.SubLog;


import java.io.IOException;
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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

                String host = url.host();

                if (!cookieMap.containsKey(host)) {
                    cookieMap.put(host, new ArrayList<Cookie>());
                }

                cookieMap.get(host).addAll(cookies);

                SubLog.d("saveFromResponse", url, cookies);

            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {


                List<Cookie> cookieList = cookieMap.get(url.host());

                if (cookieList == null)
                    return new ArrayList<>();


                SubLog.d("loadForRequest", url);

                return cookieList;
            }
        });

    }


    public void setLog(boolean log) {
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

        Response response = clientBuilder.build().newCall(request).execute();


        SubResponse m = new SubResponse(response);

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

    public SubResponse form(String url, Map<String, String> body) throws IOException {
        return form(url, null, body);
    }

    public SubResponse form(String url, Map<String, String> header, Map<String, String> body, boolean allowRedirect) throws IOException {


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

        SubResponse myResponse = new SubResponse(response, body);

        if (log)
            SubLog.d("\n", myResponse);

        return myResponse;
    }

    public SubResponse form(String url, Map<String, String> header, Map<String, String> body) throws IOException {
        return form(url, header, body, true);
    }


}
