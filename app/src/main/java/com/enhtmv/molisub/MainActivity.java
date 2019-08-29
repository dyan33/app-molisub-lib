package com.enhtmv.molisub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.cp.plugin.Plugin;
import com.cp.plugin.event.SubEvent;
import com.enhtmv.sublib.common.sub.Sub;

public class MainActivity extends AppCompatActivity implements SubEvent {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
//            SubProxy proxy = HostUtil.proxy();
//            this.setProxy(new SubProxy("37.48.98.160", "engineer@foxseek.com", "0c4263", 11285));
//            Plugin.proxy("91.220.77.154", "mauritius","precpx123",8090);
//            Plugin.proxy(proxy.getHost(), proxy.getUsername(), proxy.getPassword(), proxy.getPort());
//            Plugin.proxy("37.48.98.160", "engineer@foxseek.com", "0c4263", 11303);
//            Plugin.log(true);
//            Plugin.closeWifi(false);
//            Plugin.setHiden(false);


        }
        Plugin.operator(Sub.PT_MEO);

        Plugin.init(this, this);

        Plugin.call();

//        buildNotificationAlert("通知设置", "a", "yes", "no").show();
    }

    @Override
    protected void onResume() {
        super.onResume();


        Plugin.call();

    }

    @Override
    public void onMessage(String tag, String content) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

}

//adb shell settings put global http_proxy 192.168.31.112:8090
