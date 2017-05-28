package com.hykj.library.utils.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by kiwi on 2017/5/24.
 * Email:2763015920@qq.com
 */

//sd相关工具类
public class ExternalUtils {
    //	  1.判断设置是否挂载
//      2.获得sd的文件目录
//      3.获得总空间的大小
//      4.获得可用空间的大小
//      5.存储byte类型数据到sd卡
//      6.获取byte类型数据
//      7.存储String类型的数据
//      8.获取String类型的数据
//      9.存储Bitmap类型的数据
//      10.获取Bitmap类型的数据
    private static ExternalUtils instance;
    public static ExternalUtils getInstance(){
        if (instance==null){
            instance=new ExternalUtils();
        }
        return instance;
    }
    /**
     *  1.判断设置是否挂载,是否可用
     * @return
     */
    public boolean isMounted(){
        //获得当前的挂载状态
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }

    /**
     * 2.获得sd的文件目录
     * @return
     */
    public  String getSDRootPath(){
        if(isMounted()){
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator;
        }
        return null;
    }

    /**
     *获得总空间大小    单位是MB;
     * @param path
     * @return
     */
    public int getTotalSize(String path){
        if(!isMounted()){
            return -1;
        }
        StatFs statFs = new StatFs(path);
        //获得该空间总共有多少个块区
        int blockCount = statFs.getBlockCount();
        //获得每一块的大小
        int blockSize = statFs.getBlockSize();
        return blockCount*blockSize/1024/1024;
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public String getSDCardPath()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }
    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public long getFreeBytes(String filePath)
    {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath()))
        {
            filePath = getSDCardPath();
        } else
        {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }
    /**
     * 获得有用空间大小    单位是MB;
     * @param path
     * @return
     */
    public int getAvailableSize(String path){
        if(!isMounted()){
            return -1;
        }

        StatFs statFs = new StatFs(path);
        //获得有用空间的块数
        int availableBlocks = statFs.getAvailableBlocks();
        //获得每一块的大小
        int blockSize = statFs.getBlockSize();
//		计算大小
        return availableBlocks*blockSize/1024/1024;
    }



    /**
     * 存储字节数组到sd卡
     * @param byteData
     * @param fileName
     */
    public void saveByteArrayToSd(byte[] byteData,String fileName){
        //判断挂载
        if(!isMounted()){
            return;
        }
        File file = new File(getCachePath());
        //判断文件是否存在
        if(!file.exists()){
            //创建目录
            file.mkdirs();
        }

        try {
            FileOutputStream fos = new FileOutputStream(new File(file,fileName));
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(byteData, 0, byteData.length);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * 读取sd卡中的数据
     * @param fileName
     * @return
     */
    public byte[] readByteArrayFromSd(String fileName){
        //判断挂载
        if(!isMounted()){
            return null;
        }

        File file = new File(getCachePath(),fileName);
        if(!file.exists()){
            return null;
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length = 0;
            while(-1 != (length = fis.read(buffer))){
                baos.write(buffer, 0, length);
                baos.flush();
            }
            baos.close();
            fis.close();
            return baos.toByteArray();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return null;
    }


    /**
     * 保存String类型的数据到指定文件中
     * @param stringData
     * @param fileName
     */
    public void  saveStringToSd(String stringData,String fileName){
        //转换成字节数组
        byte[] bytes = stringData.getBytes();
        saveByteArrayToSd(bytes, fileName);
    }

    /**
     * 读取sd卡中的String数据
     * @param fileName
     * @return
     */
    public String readStringFromSd(String fileName){
        byte[] readBytearray = readByteArrayFromSd(fileName);
        return new String(readBytearray);
    }



    /**
     * 根据图片的网络地址将bitmap存储到对应的文件中
     * @param bitmap
     * @param imageNetPath
     */
    public void saveBitmapToSd(Bitmap bitmap, String imageNetPath){
        //截图图片的路径 ,通常网址的最后一段字符串
        String imageFileName = getImageFileNameFromNetpath(imageNetPath);
        //判断挂载
        if(!isMounted()){
            return;
        }
        File file = new File(getCachePath());
        //判断文件是否存在
        if(!file.exists()){
            //创建目录
            file.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(new File(file,imageFileName));
            //将图片压缩到一个流中
            //CompressFormat format,  表示图片格式
            //int quality, 表示压缩质量,  100表示原质量压缩
            //OutputStream stream  表示bitmap需要压缩到那个输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    /**
     * 获得bitmap从sd的文件中
     * @param imageNetPath
     * @return
     */
    public Bitmap readBitmapFromSd(String imageNetPath){
        //判断挂载
        if(!isMounted()){
            return null;
        }

        File file = new File(getCachePath(),getImageFileNameFromNetpath(imageNetPath));
        if(!file.exists()){
            return null;
        }

        return BitmapFactory.decodeFile(file.getAbsolutePath());

    }


    /**
     * 获得程序的文件存储目录(自定义)
     * @return
     */
    public String getCachePathTwo(String path){
        //return getSDRootPath() + "/youku/yfile";
        return getSDRootPath() + path;
    }
    /**
     * 获得程序的文件存储目录(自定义)
     * @return
     */
    public String getCachePath(){
        return getSDRootPath() + "/youku/yfile";
        //return getSDRootPath() + path;
    }


    /**
     * 根据网络地址截取图片地址
     * @param imageNetPath
     * @return
     */
    public String getImageFileNameFromNetpath(String imageNetPath){
        return imageNetPath.substring(imageNetPath.lastIndexOf("/") + 1);
    }
}
