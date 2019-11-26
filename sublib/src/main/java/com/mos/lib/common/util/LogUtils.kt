package com.mos.lib.common.util

import com.mos.moli.subproject.BuildConfig
import com.orhanobut.logger.Logger

/**
 * Date: 2019/1/28
 * author: liuhao
 */
object LogUtils {
    fun i(info: String) {
        if (BuildConfig.DEBUG) {
            Logger.i(info)
        }
    }

    fun d(info: String) {
        if (BuildConfig.DEBUG) {
            Logger.d(info)
        }
    }
}
