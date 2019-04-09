package com.enhtmv.sublib.common.util;

import android.util.Log;


public class SubLog {

    private static String TAG = "==========>";

    private static boolean ok = false;

    private static String string(Object... objects) {
        StringBuilder builder = new StringBuilder("\n");
        for (Object object : objects) {
            builder.append(object == null ? null : object.toString()).append("  ");
        }
        return builder.toString();
    }

    public static void setLog(boolean ok) {
        SubLog.ok = ok;
    }

    public static void w(Object... objects) {
        if (ok) {
            Log.w(TAG, string(objects));
        }
    }

    public static void i(Object... objects) {
        if (ok) {
            Log.i(TAG, string(objects));
        }

    }

    public static void e(Object... objects) {
        if (ok) {
            Log.e(TAG, string(objects));
        }
    }

    public static void e(Throwable throwable, Object... objects) {
        if (ok) {
            Log.e(TAG, string(objects), throwable);
        }
    }

    public static void d(Object... objects) {
        if (ok) {

            Log.d(TAG, string(objects));

        }
    }
}
