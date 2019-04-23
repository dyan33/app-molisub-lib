package com.enhtmv.molisub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.SubContext;
import com.enhtmv.sublib.common.SubEvent;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.work.putaoya.MeoSub;

public class MainActivity extends AppCompatActivity {

    private SubContext subContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WebView webView = findViewById(R.id.webview);


        webView.setWebChromeClient(new WebChromeClient());


        SubCall subCall = new MeoSub(webView, new SubEvent() {
            @Override
            public void onMessage(String tag, String content) {
                System.out.println(tag);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable);
            }
        });

//        SubCall subCall = new NosSub(new SubEvent() {
//            @Override
//            public void onMessage(String tag, String content) {
//                System.out.println(tag + ": " + content);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        });


        subContext = new SubContext(this, subCall);
        if (BuildConfig.DEBUG) {


            subCall.setProxy(new SubProxy("37.48.98.160","engineer@foxseek.com","0c4263",11310));
//            subCall.setProxy(HostUtil.proxy());
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
