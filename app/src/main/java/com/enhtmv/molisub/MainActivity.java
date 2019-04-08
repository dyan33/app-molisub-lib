package com.enhtmv.molisub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.enhtmv.subsdk.common.SubContext;
import com.enhtmv.subsdk.common.util.SubLog;
import com.enhtmv.subsdk.work.H3GSubCall;

public class MainActivity extends AppCompatActivity {


    private SubContext subContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        H3GSubCall subCall = new H3GSubCall();

        if (BuildConfig.DEBUG) {

            SubLog.setLog(true);

            subCall.setHttpLog(true);

            subCall.setProxy("91.220.77.154", "mauritius", "Ux5vW5qw", 8090);

        }

        subContext = new SubContext(this);
        subContext.setSubCall(subCall);


        //判断通知权限
        if (!subContext.isNotificationServiceEnabled()) {
            subContext.showNotificationDialog("通知权限获取", "获取通知权限!!!", "是", "否");
        } else {
            subContext.call();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        subContext.call();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subContext.destroy();
    }
}
