package com.enhtmv.molisub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.cp.plugin.Plugin;
import com.cp.plugin.event.SubEvent;

public class MainActivity extends AppCompatActivity implements SubEvent {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Plugin.init(this, this);

        Plugin.buildNotificationAlert("标题", "该应用需要授权读取通知权限", "确定", "取消");
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

