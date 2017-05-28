package com.hykj.library.utils.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Author: KiWi刘少帅 on 2017/5/21   16:44
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:
 */

public class TypeConversionUtils {
    //object类转成string
    public static String getStringFromObject(Object object){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos=null;
        try {
            oos=new ObjectOutputStream(baos);
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String string = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return string;
    }
    //把string转成object
    public static Object getObjectFromString(String msg){
        byte[] bytes = Base64.decode(msg, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    //bitmap转成string
    public static String getStringFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    //string 还原为bitmap
    public static Bitmap getBitmapFormString(String msg){
        Bitmap bp=null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(msg, Base64.DEFAULT);
            bp = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bp;
    }
    //把bitmap转成btye数组
    //把btye[]转成bitmap


}
