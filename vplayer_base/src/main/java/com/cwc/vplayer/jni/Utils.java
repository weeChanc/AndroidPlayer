package com.cwc.vplayer.jni;

public class Utils {

    static {
        System.loadLibrary("native-lib");
    }
    public static native String hello();
}
