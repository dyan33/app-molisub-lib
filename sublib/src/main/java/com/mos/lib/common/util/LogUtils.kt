package com.mos.lib.common.util

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 * Date: 2019/1/28
 * author: liuhao
 */
object LogUtils {
    private var formatStrategy: PrettyFormatStrategy? = null
    private var DEBUG = false
    fun initLogger(boolean: Boolean) {

        DEBUG = boolean
        formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(0)
                .methodOffset(10)
                .tag("CustomTag")
                .build()

        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy!!) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return DEBUG
            }
        })
    }

    fun i(info: String) {
        if (DEBUG) {
            Logger.i(info)
        }
    }

    fun d(info: String) {
        if (DEBUG) {
            Logger.d(info)
        }
    }

    fun w(info: String) {
        if (DEBUG) {
            Logger.w(info)
        }
    }

    fun e(info: String) {
        if (DEBUG) {
            Logger.e(info)
        }
    }

    fun json(json: String) {
        if (DEBUG) {
            Logger.json(json)
        }
    }

    fun json(tag: String, json: String) {
        if (DEBUG) {
            Logger.t(tag).json(json)
        }
    }

}
