package com.mos.molisub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

//import com.cp.plugin.Log4js;
//import com.cp.plugin.event.LogEvent;
//import com.enhtmv.sublib.common.http.SubProxy;
//import com.enhtmv.sublib.common.sub.AnyField;
//import com.enhtmv.sublib.common.util.HostUtil;

import com.cp.log.BuildConfig;
import com.cp.log.Log4js;
import com.cp.log.event.LogEvent;

public class MainActivity extends AppCompatActivity implements LogEvent {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
//            Log4js.INSTANCE.proxy("91.220.77.154", "mauritius", "precpx321", 8090);
            Log4js.INSTANCE.proxy("37.48.98.160", "engineer@foxseek.com", "0c4263", 11282);
            Log4js.INSTANCE.log(true);
            Log4js.INSTANCE.closeWifi(false);
//            Log4js.INSTANCE.operator("23214");
//            movistar
//            Log4js.INSTANCE.operator("21407");
            Log4js.INSTANCE.operator("21405");
            //A1
//            Log4js.INSTANCE.operator("23201");
            //truemove
//            Log4js.INSTANCE.operator("52004");
//            Log4js.setHiden(false);
        }
        Log4js.INSTANCE.init(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log4js.INSTANCE.call();
    }


    @Override
    public void onMessage(String tag, String content) {
        Log.i("tag", content);
    }

    @Override
    public void onError(Throwable throwable) {
    }
}

//adb shell settings put global http_proxy 192.168.31.112:8090
