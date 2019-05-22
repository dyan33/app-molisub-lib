package com.enhtmv.sublib.work;

import com.enhtmv.sublib.common.sub.SubCall;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketWorker extends SubCall {


    @Override
    public void sub(String host) {


        OkHttpClient client = new OkHttpClient.Builder().build();

        //ws://10.0.2.2:8010/ws
        final Request request = new Request.Builder().url(host).build();


        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);


            }

        });

    }
}
