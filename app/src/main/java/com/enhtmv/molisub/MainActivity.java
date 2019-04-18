package com.enhtmv.molisub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.SubCallBack;
import com.enhtmv.sublib.common.SubContext;
import com.enhtmv.sublib.common.SubEvent;
import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.webview.PutaoyaMEOSub;

public class MainActivity extends AppCompatActivity {

    private SubContext subContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WebView webView = findViewById(R.id.webview);


        webView.setWebChromeClient(new WebChromeClient());


        PutaoyaMEOSub subCall = new PutaoyaMEOSub(webView, new SubEvent() {
            @Override
            public void onMessage(String tag, String content) {
                System.out.println(tag);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable);
            }
        });

        subContext = new SubContext(this, subCall);
        if (BuildConfig.DEBUG) {

            subCall.setLog(true);
            subContext.setCloseWifi(false);
        }

        subContext.call();


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
