package com.hykj.library.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import com.hykj.library.MyApp;


/**
 * Author: KiWi刘少帅 on 2017/5/21   16:31
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:
 */

public class Utils {
    //判断字符串的长度
    public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength = valueLength + 2;
            } else {
                valueLength++;
            }
        }
        return valueLength;
    }

    public static boolean checkPermission(String permissionName) {
        String permission = permissionName; //你要判断的权限名字
        int res = MyApp.getInstance().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * 当SD卡存在或者SD卡不可被移除的时候，就调用getExternalCacheDir()方法来获取缓存路径，
     * 否则就调用getCacheDir()方法来获取缓存路径。前者获取到的就是 /sdcard/Android/data/<application package>/cache 这个路径，
     * 而后者获取到的是 /data/data/<application package>/cache 这个路径
     *
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
    //判断相机是否可用
    public static boolean cameraIsCanUse(Context context){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            return false;
        }
        return true;
    }
    /**
     * 调用系统打电话
     * @param phoneNum
     * @param context
     */
    public static void callPhone(String phoneNum, Context context){
        Uri number = Uri.parse("tel:"+phoneNum);
        context.startActivity(new Intent(Intent.ACTION_DIAL,number));
    }

}
