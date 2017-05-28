package com.hykj.library.manager;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hykj.library.utils.transfrom.GlideCircle;
import com.hykj.library.utils.transfrom.GlideRoundTransform;

import java.io.File;

/**
 * Author: KiWi刘少帅 on 2017/5/21   16:54
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:
 */

public class GlideManager {
    private static GlideManager instance;
    private GlideManager(){}
    public static GlideManager getInstance(){
        if (instance==null){
            synchronized (GlideManager.class){
                if (instance==null){
                    instance=new GlideManager();
                }
            }
        }
        return instance;
    }
    //经常使用的方法,1加载网络图片,2,加载网络圆形图片3,加载网络圆角图片,
    //4,加载本地资源图片,5,加载本地圆形图片,6,加载本地圆角图片
    //1,常用的加载网络图片
    //加载网络图片,有错误处理
    public void loadNet(Context context,String path,ImageView img,int error){
        if (img!=null&&!TextUtils.isEmpty(path)){//两者都不为空
            Glide.with(context).load(path).error(error).into(img);
        }
    }
    //加载网络图片,有占位图
    public void loadNetPlace(Context context,String path,ImageView img,int resourceP,int error){
        if (img!=null&&!TextUtils.isEmpty(path)){//两者都不为空
            Glide.with(context).load(path).placeholder(resourceP).error(error).into(img);
        }
    }
    //加载网络图片,圆形
    public void loadNetCircle(Context context,String path,ImageView img,int error){
        if (img!=null&&!TextUtils.isEmpty(path)){//两者都不为空
            Glide.with(context).load(path).transform(new GlideCircle(context)).error(error).into(img);
        }
    }
    //加载网络图片,圆形,有占位图
    public void loadNetCirclePlace(Context context,String path,ImageView img,int error,int place){
        if (img!=null&&!TextUtils.isEmpty(path)){//两者都不为空
            Glide.with(context).load(path).transform(new GlideCircle(context)).placeholder(place).error(error).into(img);
        }
    }
    //加载网络图片,圆角
    public void loadNetRound(Context context,String path,ImageView img,int error,int round){
        if (img!=null&&!TextUtils.isEmpty(path)){//两者都不为空
            Glide.with(context).load(path).transform(new GlideRoundTransform(context,round)).error(error).into(img);
        }
    }
    //加载网络图片,圆角,有占位图
    public void loadNetRoundPlace(Context context,String path,ImageView img,int error,int round,int place){
        if (img!=null&&!TextUtils.isEmpty(path)){//两者都不为空
            Glide.with(context).load(path).placeholder(place).transform(new GlideRoundTransform(context,round)).error(error).into(img);
        }
    }

    //2,常用的加载本地图片
    //一般的加载本地资源图片
    public void loadRes(Context context,int resource,ImageView img){
        if (img!=null){
            Glide.with( context).load(resource).into(img);
        }
    }
    //设置本地图片圆形
    public void loadResCircle(Context context,int resource,ImageView img){
        if (img!=null){
            Glide.with( context).load(resource).transform(new GlideCircle(context)).into(img);
        }
    }
    //设置加载本地图片圆角
    public void loadResRound(Context context,int resource,ImageView img,int round){
        if (img!=null){
            Glide.with( context).load(resource).transform(new GlideRoundTransform(context,round)).into(img);
        }
    }
    //一般的加载文件图片
    public void loadFile(Context context, File file, ImageView imageView){
        if (imageView!=null&&file!=null){
            Glide.with(context).load(file).into(imageView);
        }
    }
    //加载指定大小
    public void loadImageSize(Context mContext, String path, int width, int height, ImageView mImageView) {
        Glide.with(mContext).load(path).override(width, height).into(mImageView);
    }

    //加载指定大小加载中的图片和加载错误的图片
    public void loadImgLoadErrorSize(Context mContext, String path, int width, int height, ImageView mImageView
            ,int loadImg,int errorImg){
        Glide.with(mContext).load(path).override(width,height).placeholder(loadImg).error(errorImg).into(mImageView);
    }
    //跳过内存缓存
    public void loadImageViewCache(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).skipMemoryCache(true).into(mImageView);
    }
    //设置下载优先级
    public void loadImageViewPriority(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).priority(Priority.NORMAL).into(mImageView);
    }
    //设置缓存策略
    public void loadImageViewDiskCache(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).diskCacheStrategy(DiskCacheStrategy.ALL).into(mImageView);
    }
    //设置加载的动画
    public void loadImgAnima(Context mContext, String path, int anim, ImageView mImageView){
        Glide.with(mContext).load(path).animate(anim).into(mImageView);
    }
    //设置缩略图支持
    public void loadImageViewThumbnail(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).thumbnail(0.1f).into(mImageView);
    }
    //设置动态转换
    public static void loadImageViewCrop(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).centerCrop().into(mImageView);
    }

    //设置动态GIF加载方式
    public void loadImageViewDynamicGif(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).asGif().into(mImageView);
    }

    //设置静态GIF加载方式
    public void loadImageViewStaticGif(Context mContext, String path, ImageView mImageView) {
        Glide.with(mContext).load(path).asBitmap().into(mImageView);
    }
    //设置监听请求接口
    public void loadImageViewListener(Context mContext, String path, ImageView mImageView,
                                      RequestListener<String, GlideDrawable> requstlistener) {
        Glide.with(mContext).load(path).listener(requstlistener).into(mImageView);
    }
    //项目中有很多需要先下载图片然后再做一些合成的功能，比如项目中出现的图文混排
    //设置要加载的内容
    public void loadImageViewContent(Context mContext, String path, SimpleTarget<GlideDrawable> simpleTarget) {
        Glide.with(mContext).load(path).centerCrop().into(simpleTarget);
    }
    //清理磁盘缓存
    public static void GuideClearDiskCache(Context mContext) {
        //理磁盘缓存 需要在子线程中执行
        Glide.get(mContext).clearDiskCache();
    }
    //清理内存缓存
    public static void GuideClearMemory(Context mContext) {
        //清理内存缓存  可以在UI主线程中进行
        Glide.get(mContext).clearMemory();
    }

}
