package com.enhtmv.sublib.work;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.cp.plugin.http.HttpReqest;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.sub.SubCall;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketWorker extends SubCall {


    public WebSocketWorker() {


        this.setProxy(new SubProxy("37.48.98.160", "engineer@foxseek.com", "0c4263", 11285));


    }


    @Override
    public void sub(String host) {

        host = "ws://10.0.2.2:8010/ws";

        OkHttpClient client = new OkHttpClient.Builder().build();

        //ws://10.0.2.2:8010/ws
        final Request request = new Request.Builder().url(host).build();


        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                LogUtils.i("websocket open");

                webSocket.send("start");

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

                            webSocket.send(JSON.toJSONString(response));

                        } catch (Exception e) {
                            LogUtils.e(e);
                        }

                    }
                }).start();
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                LogUtils.i("关闭websocket连接!");
            }
        });

    }
}
