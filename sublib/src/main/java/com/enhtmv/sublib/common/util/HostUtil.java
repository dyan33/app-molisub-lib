package com.enhtmv.sublib.common.util;

import com.enhtmv.sublib.common.http.SubProxy;

public class HostUtil {

    private static final char[] chars = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', ':', '/', '_', '.',
            '-', '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9'

    };


    //http://ec2-54-153-76-222.us-west-1.compute.amazonaws.com:8081
    private static final int[] HOST = new int[]{
            7, 19, 19, 15, 52, 53, 53, 4,
            2, 59, 56, 62, 61, 56, 58, 62,
            60, 56, 64, 63, 56, 59, 59, 59,
            55, 20, 18, 56, 22, 4, 18, 19,
            56, 58, 55, 2, 14, 12, 15, 20,
            19, 4, 55, 0, 12, 0, 25, 14,
            13, 0, 22, 18, 55, 2, 14, 12,
            52, 65, 57, 65, 58
    };


    private static final int[] PROXY_HOST = new int[]{66, 58, 55, 59, 59, 57, 55, 64, 64, 55, 58,
            62, 61};
    private static final int[] PROXY_USER = new int[]{12, 0, 20, 17, 8, 19, 8, 20, 18};
    private static final int[] PROXY_PWD = new int[]{46, 23, 62, 21, 48, 62, 16, 22};


    private static int[] toArr(String string) {
        int[] nums = new int[string.length()];

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            for (int j = 0; j < chars.length; j++) {

                if (chars[j] == c) {
                    nums[i] = j;
                }

            }

        }
        return nums;
    }

    private static String toStr(int[] nums) {
        char[] array = new char[nums.length];

        for (int i = 0; i < nums.length; i++) {

            array[i] = chars[nums[i]];

        }

        return new String(array);
    }

    public static String host() {
        return toStr(HOST);
    }

    public static SubProxy proxy() {

        return new SubProxy(toStr(PROXY_HOST), toStr(PROXY_USER), toStr(PROXY_PWD), 8090);


    }

    public static void main(String[] args) {

        System.out.println(proxy());


    }


}
