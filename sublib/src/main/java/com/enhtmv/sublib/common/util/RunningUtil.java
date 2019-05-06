package com.enhtmv.sublib.common.util;

import com.blankj.utilcode.util.LogUtils;

import java.util.Date;

public class RunningUtil {


    public static void setDelay(int seconds) {

        long delay = (seconds * 1000) + new Date().getTime();

        SharedUtil.shared().edit().putLong("delay_run", delay).apply();


    }


    public static void delay() {

        long time = new Date().getTime();

        long delay = SharedUtil.shared().getLong("delay_run", 0);

        if (delay > time) {

            try {
                Thread.sleep(time - delay);
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }


    }
}
