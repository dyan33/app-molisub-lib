package com.enhtmv.sublib.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.Utils;

public class SharedUtil {

    private static SharedPreferences shared;


    private static void init() {

        synchronized (SharedUtil.class) {

            if (shared == null) {

                Context context = Utils.getApp();
                shared = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
            }

        }
    }

    public static void success() {

        init();

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean("success", true);

        editor.apply();

    }


    public static boolean isSuccess() {
        init();

        return shared.getBoolean("success", false);

    }

    public static void installed() {
        init();

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean("installed", true);

        editor.apply();
    }


    public static boolean isInstalled() {
        init();

        return shared.getBoolean("installed", false);

    }


}
