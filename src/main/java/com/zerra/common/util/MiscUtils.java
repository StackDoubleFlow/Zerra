package com.zerra.common.util;

public class MiscUtils {

    public static String secondsSinceTime(long millis) {
        return (float) (System.currentTimeMillis() - millis) / 1000F + " seconds.";
    }
}
