package com.enhtmv.molisub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.enhtmv.sublib.common.SubCall;
import com.enhtmv.sublib.common.SubCallBack;
import com.enhtmv.sublib.common.SubContext;
import com.enhtmv.sublib.common.util.SubLog;
import com.enhtmv.sublib.work.H3GSubCall;

public class MainActivity extends AppCompatActivity {


    private SubContext subContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final SubCall subCall = new H3GSubCall();

        if (BuildConfig.DEBUG) {

            SubLog.setLog(true);

            subCall.setLog(true);

//            subCall.setProxy(HostUtil.proxy());

        }

        subContext = new SubContext(this, subCall);

        subContext.state(new SubCallBack<String>() {
            @Override
            public void callback(final String string) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (string != null && !subContext.isNotificationServiceEnabled()) {

                            subContext.showNotificationDialog("a", "a", "y", "n");
                        }

                    }
                });
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (subContext.isNotificationServiceEnabled()) {
            subContext.call();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subContext.destroy();
    }
}
