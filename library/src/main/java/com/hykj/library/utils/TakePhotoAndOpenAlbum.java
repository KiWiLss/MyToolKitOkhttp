package com.hykj.library.utils;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.widget.ImageView;

import com.hykj.library.config.Constans;
import com.hykj.library.utils.common.TT;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;

/**
 * Author: KiWi刘少帅 on 2017/1/25   13:08
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:
 */

public class TakePhotoAndOpenAlbum {

    public static final int OPEN_ALBUM=2;//打开相册是标识
    public String mImgPath; //拍照或是相册选中图片的地址
    private Activity activity;
    ImageView imgPic;


    public TakePhotoAndOpenAlbum(Activity activity, ImageView imgPic) {
        this.activity = activity;
        this.imgPic = imgPic;
    }

    public static final int TAKE_PHOTO = 1;//拍照的标记


    //打开相机拍照的方法
    public Disposable takePhoto() {

        //创建用于存放拍摄照片的文件
        File outputImage = new File(activity.getExternalCacheDir(), "output_image.jpg");
        //如果文件存在,就删除重新创建
        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
            mImgPath = outputImage.toString();//记录存储拍照图片的路径
        } catch (IOException e) {
            e.printStackTrace();
        }

      /*  imgUri = Uri.fromFile(outputImage);
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        if (intent.resolveActivity(activity.getPackageManager())==null){
            TT.show("存储卡不可用");
        }else {
            boolean isHas = PermissionUtils.isCameraPermission(activity, TAKE_PHOTO);
            if (isHas){
                activity.startActivityForResult(intent, TAKE_PHOTO);
            }
        }*/
        Uri imgUri;
     /*   if (Build.VERSION.SDK_INT>=24){//设备大于等于7.0,单独一种处理方法,主要是展示图片获取方式不同
             imgUri = FileProvider.getUriForFile(activity, "com.example.cameraalbumtest.fileprovider", outputImage);
        }else{*/
        imgUri = Uri.fromFile(outputImage);

        //启动相机程序
        final Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        //判断相机是否可用
        boolean isCanUse = com.hykj.library.utils.Utils.cameraIsCanUse(activity);
        if (!isCanUse){
            TT.show("存储卡不可用");
        }else {
            //判断权限是否充足
            Disposable disposable = RxPermissions.getInstance(activity).request(Constans.CAMARA)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(@NonNull Boolean aBoolean) throws Exception {
                            if (aBoolean) {//6.0以前会默认获取权限,获取权限
                                //打开相机
                                activity.startActivityForResult(intent, TAKE_PHOTO);
                            } else {
                                TT.show("您没有授权该权限，请在设置中打开授权");
                            }
                        }
                    });
            return disposable;
        }
        return null;
    }
    //打开相册的方法
    public Disposable openAlbum(){
        //跳转到相册可以用这种方法
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        activity.startActivityForResult(intent, OPEN_ALBUM);
        //也可以用这种方法
        final Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");

        //判断是否有权限
        Disposable disposable = RxPermissions.getInstance(activity)
                .request(Constans.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            activity.startActivityForResult(intent, OPEN_ALBUM);
                        } else {
                            TT.show("您没有授权该权限，请在设置中打开授权");
                        }
                    }
                });
        return disposable;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case TAKE_PHOTO:
                        //获取图片,展示
                        if (!TextUtils.isEmpty(mImgPath)){
                            return true;
                        }
                        return false;
                    case OPEN_ALBUM:
                        //判断手机系统的版本信息
                        if (Build.VERSION.SDK_INT>=19) {
                            handleImageOnKitKat(data);
                        }else{
                            handleImageBeforeKitKat(data);
                        }
                        //获取图片,展示
                        if (!TextUtils.isEmpty(mImgPath)){
                            return true;
                        }
                        return false;
                }
            }

        return false;
    }

    private void handleImageBeforeKitKat(Intent data) {//5.0以下的系统
        Uri uri = data.getData();
        String imagePath = getimagePath(uri, null);
        if (!TextUtils.isEmpty(imagePath)){
            mImgPath=imagePath;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {//5.0以上的系统
        String imgPath=null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(activity,uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imgPath=getimagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imgPath=getimagePath(contentUri,null);

            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imgPath=getimagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imgPath=uri.getPath();
        }
        if (!TextUtils.isEmpty(imgPath)){
            mImgPath=imgPath;
        }
        //displayImage(imgPath);



    }
    private String getimagePath(Uri uri, String seletion) {//获取图片路径的方法
        String path=null;
        Cursor cursor = activity.getContentResolver().query(uri, null, seletion, null, null);
        if (cursor!=null) {
            if (cursor.moveToFirst()) {
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            }
            cursor.close();
        }
        return path;
    }

   /* public boolean onResum() {//true,表示打开图片成功
        LogUtils.i("onresum-->"+mImgPath);
        if (!TextUtils.isEmpty(mImgPath)) {
            Bitmap bitmap = CompressPic.compressPicSize(mImgPath, 500, 500);
            if (bitmap==null){
                LogUtils.i("bitmap==null");
                return false;//
            }
            if (imgPic!=null){
                imgPic.setImageBitmap(bitmap);
                return true;//成功展示
            }
            mImgPath=null;//刷新后释放,防止手机休眠后自动添加
        }
        LogUtils.i("mimgpath==null");
        return false;
    }*/

    public  void saveBpImageToGallery(Context context, Bitmap bmp) {//保存bitmap图片
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,	Uri.fromFile(new File(file.getPath()))));
    }

    public  void saveFileImageToGallery(Context context, String imgFile) {//保存file图片
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Bitmap bmp = BitmapFactory.decodeFile(imgFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,	Uri.fromFile(new File(file.getPath()))));
    }

    //获取拍照的图片路径和打开相册展示的图片的路径
    public String getImgPath(){
        if (!TextUtils.isEmpty(mImgPath)){
            return mImgPath;
        }
        return null;
    }
}
