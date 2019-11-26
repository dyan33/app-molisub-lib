package com.mos.lib.common.util

import com.blankj.utilcode.util.LogUtils

import java.util.Date

object RunningUtil {


    fun setDelay(seconds: Int) {

        val delay = seconds * 1000 + Date().time

        SharedUtil.shared().edit().putLong("delay_run", delay).apply()


    }


    fun delay() {

        val time = Date().time

        val delay = SharedUtil.shared().getLong("delay_run", 0)

        if (delay > time) {

            try {

                LogUtils.i("delay running:", time - delay)

                Thread.sleep(time - delay)
            } catch (e: Exception) {
                LogUtils.e(e)
            }

        }


    }
}
