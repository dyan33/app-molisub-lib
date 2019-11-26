package com.mos.lib.common.util

import android.content.Context
import android.content.SharedPreferences

import com.blankj.utilcode.util.Utils


object SharedUtil {

    val isSuccess: Boolean
        get() = shared().getBoolean("success", false)

    val isInstalled: Boolean
        get() = shared().getBoolean("installed", false)


    fun shared(): SharedPreferences {
        val context = Utils.getApp()
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }


    fun success() {
        val editor = shared().edit()

        editor.putBoolean("success", true)

        editor.apply()

    }

    fun installed() {
        val editor = shared().edit()

        editor.putBoolean("installed", true)

        editor.apply()
    }

}
