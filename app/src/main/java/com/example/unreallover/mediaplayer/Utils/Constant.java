package com.example.unreallover.mediaplayer.Utils;

import android.Manifest;

/**
 * Created by Unreal Lover on 2017/10/31.
 */

public class Constant {
    // 当前的状态，0x11代表没有播放；0x12代表正在播放；0x13代表暂停
    public static final int M_NONE = 0x11;
    public static final int M_PLAYING = 0x12;
    public static final int M_PAUSE = 0x13;
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public  final static String SER_KEY = "com.example.unreallover.mediaplayer.ser";


}
