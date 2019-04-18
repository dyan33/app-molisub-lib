package com.enhtmv.molisub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.enhtmv.sublib.common.SubContext;
import com.enhtmv.sublib.common.SubEvent;
import com.enhtmv.sublib.webview.putaoya.MeoSub;
import com.enhtmv.sublib.webview.putaoya.NosSub;

public class MainActivity extends AppCompatActivity {

    private SubContext subContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WebView webView = findViewById(R.id.webview);


        webView.setWebChromeClient(new WebChromeClient());


//        NosSub subCall = new NosSub(webView, new SubEvent() {
//            @Override
//            public void onMessage(String tag, String content) {
//                System.out.println(tag);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                System.out.println(throwable);
//            }
//        });


        MeoSub subCall = new MeoSub(webView, new SubEvent() {
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
