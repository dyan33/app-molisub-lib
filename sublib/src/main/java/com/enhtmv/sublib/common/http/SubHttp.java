package com.enhtmv.sublib.common.http;

import com.blankj.utilcode.util.LogUtils;
import com.enhtmv.sublib.common.util.StringUtil;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
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
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class SubHttp {

    private OkHttpClient.Builder clientBuilder;

    private Map<String, Map<String, Cookie>> cookieMap = new HashMap<>();

    private List<String> urls = new ArrayList<>();

    private boolean log;

    private int timeout = 30;


    public SubHttp() {

        //创建OkHttpclient实例
        clientBuilder = new OkHttpClient.Builder();
        //连接时长
        clientBuilder.connectTimeout(timeout, TimeUnit.SECONDS);
        //读取超时
        clientBuilder.readTimeout(timeout, TimeUnit.SECONDS);
        //写入超时
        clientBuilder.writeTimeout(timeout, TimeUnit.SECONDS);
        //cookie监听
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


                //LogUtils.i("load cookie", url, cookies);

                return cookies;
            }
        });

        //添加网络拦截器用于保存地址
        clientBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                //保存URL
                urls.add(request.url().toString());

                return chain.proceed(request);
            }
        });

    }

    public OkHttpClient.Builder getClientBuilder() {
        return clientBuilder;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setCookie(String host, String name, String vaule) {

        if (!cookieMap.containsKey(host)) {
            cookieMap.put(host, new HashMap<String, Cookie>());
        }

        cookieMap.get(host).put(name, new Cookie.Builder().name(name).value(vaule).domain(host).build());

    }

    public boolean containsCookie(String host, String name) {

        Map<String, Cookie> map = cookieMap.get(host);

        return map != null && map.containsKey(name);
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public void setProxy(final SubProxy xy) {

        LogUtils.d("set proxy !", xy);

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(xy.getHost(), xy.getPort()));

        clientBuilder.proxy(proxy);

        final String username = xy.getUsername();
        final String password = xy.getPassword();

        if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(password)) {
            clientBuilder.proxyAuthenticator(new Authenticator() {
                @Override
                public Request authenticate(Route route, Response response) {
                    String credential = Credentials.basic(username, password);
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                }
            });
        }

    }


    public SubResponse execute(Request.Builder builder) throws IOException {
        return execute(builder, null);
    }

    public SubResponse execute(Request.Builder builder, Map<String, String> header) throws IOException {
        urls.clear();

        //设置请求头
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }


        long t1 = System.currentTimeMillis();

        Response response = clientBuilder.build().newCall(builder.build()).execute();

        long t2 = System.currentTimeMillis();

        SubResponse subResponse = new SubResponse(response, urls, t2 - t1);

        if (log) {
            LogUtils.d(subResponse);
        }


        return subResponse;
    }

    public SubResponse get(String url, Map<String, String> header, boolean allowRedirect) throws IOException {

        Request.Builder builder = new Request.Builder().url(url);


        clientBuilder.followRedirects(allowRedirect);
        clientBuilder.followRedirects(allowRedirect);


        return execute(builder, header);
    }

    public SubResponse get(String url, Map<String, String> header) throws IOException {

        return get(url, header, true);
    }

    public SubResponse get(String url) throws IOException {
        return get(url, null, true);
    }

    public SubResponse postForm(String url, Map<String, String> form) throws IOException {
        return postForm(url, null, form);
    }

    public SubResponse postForm(String url, Map<String, String> header, Map<String, String> form, boolean allowRedirect) throws IOException {


        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, String> entry : form.entrySet()) {
            formBody.add(entry.getKey(), entry.getValue());
        }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(formBody.build());


        clientBuilder.followRedirects(allowRedirect);
        clientBuilder.followSslRedirects(allowRedirect);


        return execute(builder, header);
    }

    public SubResponse postForm(String url, Map<String, String> header, Map<String, String> form) throws IOException {
        return postForm(url, header, form, true);
    }

    public SubResponse options(String url, Map<String, String> header) throws IOException {

        Request.Builder builder = new Request.Builder()
                .url(url)
                .method("OPTIONS", null);


        return execute(builder, header);


    }

    public SubResponse postJson(String url, String json) throws IOException {
        return postJson(url, null, json);
    }

    public SubResponse postJson(String url, Map<String, String> header, String json) throws IOException {

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json));


        return execute(builder, header);


    }

    public List<String> getUrls() {
        return urls;
    }
}
