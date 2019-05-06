package com.enhtmv.sublib.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.blankj.utilcode.util.Utils;

import java.util.Date;


public class SharedUtil {


    public static SharedPreferences shared() {
        Context context = Utils.getApp();
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }


    public static void success() {


        SharedPreferences.Editor editor = shared().edit();

        editor.putBoolean("success", true);

        editor.apply();

    }

    public static boolean isSuccess() {

        return shared().getBoolean("success", false);

    }

    public static void installed() {

        SharedPreferences.Editor editor = shared().edit();

        editor.putBoolean("installed", true);

        editor.apply();
    }

    public static boolean isInstalled() {

        return shared().getBoolean("installed", false);

    }

}
