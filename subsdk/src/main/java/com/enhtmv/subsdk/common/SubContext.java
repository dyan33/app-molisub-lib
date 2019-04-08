package com.enhtmv.subsdk.common;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.enhtmv.subsdk.common.util.SubLog;
import com.enhtmv.subsdk.common.util.NetUtil;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;

public class SubContext {

    private static SubCall callInstance;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";


    private Context context;

    private NetworkStateReceiver receiver;

    public SubContext(Context context) {
        this.context = context;
    }

    public void destroy() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    public void startActivity(Intent intent) {
        context.startActivity(intent);
    }

    private void setOk() {
        SharedPreferences shared = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean("ok", false);

        editor.apply();
    }

    private boolean ok() {
        SharedPreferences shared = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);


        return shared.getBoolean("ok", true);

    }


    public void setSubCall(SubCall subCall) {

        if (subCall != null) {

            final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            final String packageName = context.getPackageName();

            SubCallBack subCallBack = new SubCallBack() {
                @Override
                public void callback() {
                    setOk();
                }
            };

            subCall.init(packageName, androidId, subCallBack);

            callInstance = subCall;
        }
    }

    public void call() {

        //判断网络状态
        if (ok() && wifiStateAndClose()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    callInstance.call();
                }
            }).start();
        }

    }

    public static void call(final String message) {

        if (callInstance != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (callInstance != null) {
                        callInstance.call(message);
                    }
                }
            }).start();
        }
    }


    /**
     * 如果wifi打开则关闭
     */
    public boolean wifiStateAndClose() {

        int netCode = NetUtil.getNetworkState(context);

        if (netCode == NetUtil.NETWORK_WIFI) {

            SubLog.d("start close wifi");

            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            wifiManager.setWifiEnabled(false);


            //注册广播
            if (receiver == null) {

                receiver = new NetworkStateReceiver(new SubCallBack() {
                    @Override
                    public void callback() {
                        call();
                    }
                });

                IntentFilter filter = new IntentFilter();

                filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
                filter.addAction("android.net.wifi.STATE_CHANGE");

                context.registerReceiver(receiver, filter);
            }

            return false;
        } else if (netCode == NetUtil.NETWORK_NONE) {
            SubLog.w("not network");
            return false;
        }

        return true;
    }


    /**
     * 判断是否拥有通知权限
     */
    public boolean isNotificationServiceEnabled() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String pkgName = context.getPackageName();
            final String flat = Settings.Secure.getString(context.getContentResolver(),
                    ENABLED_NOTIFICATION_LISTENERS);

            if (!TextUtils.isEmpty(flat)) {
                final String[] names = flat.split(":");
                for (int i = 0; i < names.length; i++) {
                    final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                    if (cn != null) {
                        if (TextUtils.equals(pkgName, cn.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } else {

            //todo 直接获取

            return true;
        }

    }


    public void showNotificationDialog(String title, String content, String yes, String no) {

        this.buildNotificationAlert(title, content, yes, no).show();

    }


    public AlertDialog buildNotificationAlert(String title, String content, String yes, String no) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(content);
        alertDialogBuilder.setPositiveButton(yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                        SubLog.i("notification permission activity");
                    }
                });
        alertDialogBuilder.setNegativeButton(no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SubLog.w("cancel notification permission");

                        callInstance.r().w("cancel of notification authorization");

                    }
                });
        return (alertDialogBuilder.create());
    }


    public class NetworkStateReceiver extends BroadcastReceiver {

        private SubCallBack callBack;

        public NetworkStateReceiver(SubCallBack callBack) {
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

                    SubLog.i("onReceive call");

                    callBack.callback();
                }
            }
        }
    }


}
