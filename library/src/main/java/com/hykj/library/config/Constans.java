package com.hykj.library.config;



import android.Manifest;

import okhttp3.MediaType;

/**
 * Author: KiWi刘少帅 on 2017/5/21   17:04
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:
 */

public class Constans {
    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType NORM_TYPE = MediaType.parse("text/plain; charset=utf-8");
    //常用权限
    public static String CAMARA= Manifest.permission.CAMERA;
    public static String WRITE_EXTERNAL_STORAGE= Manifest.permission.WRITE_EXTERNAL_STORAGE;
}
