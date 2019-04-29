package com.enhtmv.molisub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.cp.plugin.Plugin;
import com.enhtmv.sublib.common.SubEvent;
import com.enhtmv.sublib.common.http.SubProxy;
import com.enhtmv.sublib.common.util.HostUtil;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WebView webView = findViewById(R.id.webview);


        webView.setWebChromeClient(new WebChromeClient());

        SubEvent event = new TestSubEvent();


        Plugin plugin = new Plugin(this, event);


        if (BuildConfig.DEBUG) {

//            subCall.setProxy(new SubProxy("37.48.98.160","engineer@foxseek.com","0c4263",11310));

            SubProxy proxy = HostUtil.proxy();

            plugin.proxy(proxy.getHost(), proxy.getUsername(), proxy.getPassword(), proxy.getPort());
            plugin.log(true);
            plugin.closeWifi(false);
        }

        plugin.call();


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class TestSubEvent implements SubEvent {
        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onMessage(String tag, String content) {

        }
    }
}
