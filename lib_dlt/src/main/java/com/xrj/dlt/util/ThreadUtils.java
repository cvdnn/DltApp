package com.xrj.dlt.util;

/**
 * Created by handy on 17-3-15.
 */

public class ThreadUtils {

    public static void sleepThread(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            // do nothing
        }
    }
}
