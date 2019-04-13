package com.enhtmv.molisub;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.SubCallBack;
import com.enhtmv.sublib.common.SubContext;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.work.AodiliH3g;

public class MainActivity extends AppCompatActivity {

    private SubContext subContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WebView.setWebContentsDebuggingEnabled(true);


        WebView webView = new WebView(this);


        webView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {




                return super.shouldOverrideUrlLoading(view, request);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {






                return super.shouldInterceptRequest(view, request);


            }
        });

        addContentView(webView, new WindowManager.LayoutParams());


        webView.loadUrl("http://www.baidu.com");


//        SubCall subCall = new AodiliH3g();
//
//        if (BuildConfig.DEBUG) {
//
//            subCall.setLog(true);
//
//            subCall.setProxy(new SubProxy("91.220.77.154", "mauritius", "Ux5vW5qw", 8090));
//
//        }
//
//        subContext = new SubContext(this, subCall);
//
//        subContext.state(new SubCallBack<String>() {
//            @Override
//            public void callback(final String meta) {
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (!StringUtil.isEmpty(meta)) {
//                            if (!subContext.isNotificationServiceEnabled()) {
//
//                                subContext.showNotificationDialog("获取权限", "通知权限获取!", "是", "否");
//                            } else {
//                                subContext.call();
//                            }
//                        }
//
//
//                    }
//                });
//            }
//        });


    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (subContext.isNotificationServiceEnabled()) {
//            subContext.call();
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        subContext.destroy();
    }
}
