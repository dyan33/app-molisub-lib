package com.enhtmv.sublib.common.util;

import java.util.Random;

public class RandomUtil {

    //指定范围随机数 左闭右开
    public static int i(int min, int max) {

        return new Random().nextInt(max - min) + min;

    }

}
