package com.mos.lib.common.util

import java.util.Random

object RandomUtil {

    //指定范围随机数 左闭右开
    fun i(min: Int, max: Int): Int {
        return Random().nextInt(max - min) + min
    }

}
