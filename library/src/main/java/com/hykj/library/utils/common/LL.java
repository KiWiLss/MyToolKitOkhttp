package com.hykj.library.utils.common;

import android.util.Log;

/**
 * Author: KiWi刘少帅 on 2017/5/21   16:33
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:日志辅助工具
 */

public class LL {

    public static final String TAG="MMM";//过滤
    private static boolean isPrint=true;//是否打印日志
    public static void i(String msg){
        if (isPrint){
            Log.i(TAG, msg);
        }
    }
    public static void ii(String tag,String msg){
        if (isPrint){
            Log.i(tag, msg);
        }
    }
    public static void d(String msg){
        if (isPrint){
            Log.d(TAG, msg);
        }
    }
    public static void w(String msg){
        if (isPrint){
            Log.w(TAG, msg);
        }
    }
    public static void e(String msg){
        if (isPrint){
            Log.e(TAG, msg);
        }
    }
    public static void ee(String tag,String msg){
        if (isPrint){
            Log.e(tag, msg);
        }
    }

}
