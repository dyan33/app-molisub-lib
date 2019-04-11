package com.enhtmv.sublib.common.util;


public class SubLog {

    private static String TAG = "------------------------------------------------------------\n";

    private static boolean ok;

    private static String string(Object... objects) {
        StringBuilder builder = new StringBuilder();
        for (Object object : objects) {
            builder.append(object == null ? null : object.toString()).append("  ");
        }
        return builder.append("\n").toString();
    }

    public static void setLog(boolean ok) {
        SubLog.ok = ok;
    }

    public static void w(Object... objects) {
        if (ok) {
            System.out.println(TAG + string(objects));
        }
    }

    public static void i(Object... objects) {
        if (ok) {
            System.out.println(TAG + string(objects));
        }

    }

    public static void e(Object... objects) {
        if (ok) {
            System.out.println(TAG + string(objects));
        }
    }

    public static void e(Throwable throwable, Object... objects) {
        if (ok) {
            System.out.println(TAG + string(objects) + "\n" + StringUtil.getStackTrace(throwable));
        }
    }

    public static void d(Object... objects) {
        if (ok) {

            System.out.println(TAG + string(objects));

        }
    }
}
