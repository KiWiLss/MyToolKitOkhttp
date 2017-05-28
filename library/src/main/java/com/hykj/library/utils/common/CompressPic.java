package com.hykj.library.utils.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author: KiWi刘少帅 on 2017/5/21   16:37
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:图片处理工具类
 */

public class CompressPic {

    //压缩图片的尺寸大小
    public static Bitmap compressPicSize(String path, int newWidth, int newHight){

        //先获取图片的尺寸大小
        //获取解析bitmap的选项参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        //仅仅解析图片的边框
        options.inJustDecodeBounds=true;
        //从路径或文件中获取图片bitmap对象
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        //解析后,bitmap为空,会将长度和宽度放到options中,
        //此时取得bitmap的宽高
        int oldWidth = (int) Math.ceil(options.outWidth);//向上取整
        int oldHight = (int) Math.ceil(options.outHeight);
        //获取宽高的比例
        int sizeWidth = oldWidth / newWidth;
        int sizeHight = oldHight / newHight;

        int sampleSize=sizeHight>sizeWidth?sizeHight:sizeWidth;
        //如果超出指定的大小,就压缩图片
        if (sizeHight>1&&sizeWidth>1){
            //如同前面的步骤,向选项参数中放入新的压缩比例
            options.inSampleSize=sampleSize;
        }
        //需要完整解析整张图片
        options.inJustDecodeBounds=false;
        //按照新的缩放比例重新解析
        Bitmap bp = BitmapFactory.decodeFile(path, options);
        return bp;
    }

    //压缩图片的内存大小,使文件占据更小的空间
    public static String compressPicFileSize(String path,String newFileName,int size){//size单位kb
        //先获取图片bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        //将图片压缩到想要的内存以内
        //第一次,不压缩图片,先判断图片内存是否符合要求
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int compressQuality=100;

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        while (baos.toByteArray().length/1024>size){
            //清空已经写入的流
            baos.reset();
            //大于想要的尺寸继续压缩
            compressQuality-=10;
            bitmap.compress(Bitmap.CompressFormat.JPEG,compressQuality,baos);
        }
        //跳出循环,表示图片尺寸已经小于size,往文件中写
        FileOutputStream fos = null;

        try {
            fos=new FileOutputStream(new File(path.replace(".jpg",newFileName+".jpg")));
            byte[] bytes = baos.toByteArray();
            fos.write(bytes,0,bytes.length);
            fos.flush();
            return path.replace(".jpg",newFileName+".jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (baos!=null){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return path;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image,int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        int bytes = baos.toByteArray().length;
        while ((bytes / 1024 > size) && (options >= 20)) {  //循环判断如果压缩后图片是否大于10kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            bytes = baos.toByteArray().length;
        }
        image.recycle();
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**对bitmap图片进行渲染
     * @param inBitmap
     * @param tintColor
     * @return
     */
    public static Bitmap tintBitmap(Bitmap inBitmap , int tintColor) {
        if (inBitmap == null) {
            return null;
        }
        Bitmap outBitmap = Bitmap.createBitmap (inBitmap.getWidth(), inBitmap.getHeight() , inBitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColorFilter( new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN)) ;
        canvas.drawBitmap(inBitmap , 0, 0, paint) ;
        return outBitmap ;
    }


    public  static void saveBpImageToGallery(Context context, Bitmap bmp) {//保存bitmap图片
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

    public  static void saveFileImageToGallery(Context context, String imgFile) {//保存file图片
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

    //对图片进行圆形处理
    public static Bitmap drawCircleView01(Bitmap bitmap) {
        //这里可能需要调整一下图片的大小来让你的图片能在圆里面充分显示
        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        //构建一个位图对象，画布绘制出来的图片将会绘制到此bitmap对象上
        Bitmap bm = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        //构建一个画布,
        Canvas canvas = new Canvas(bm);
        //获得一个画笔对象，并设置为抗锯齿
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //获得一种渲染方式对象
        //BitmapShader的作用是使用一张位图作为纹理来对某一区域进行填充。
        //可以想象成在一块区域内铺瓷砖，只是这里的瓷砖是一张张位图而已。
        Shader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //设置画笔的渲染方式
        paint.setShader(shader);
        //通过画布的画圆方法将渲染后的图片绘制出来
        canvas.drawCircle(100, 100, 100, paint);
        //返回的就是一个圆形的bitmap对象
        return bm;
    }
    //对图片进行圆角处理
    public static Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels)
    {
        //创建一个和原始图片一样大小位图
        Bitmap roundConcerImage = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //创建带有位图roundConcerImage的画布
        Canvas canvas = new Canvas(roundConcerImage);
        //创建画笔
        Paint paint = new Paint();
        //创建一个和原始图片一样大小的矩形
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        // 去锯齿
        paint.setAntiAlias(true);
        //画一个和原始图片一样大小的圆角矩形
        canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
        //设置相交模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //把图片画到矩形去
        canvas.drawBitmap(bitmap, null, rect, paint);
        return roundConcerImage;
    }
    //图片灰化处理
    public static Bitmap getGrayBitmap(Bitmap mBitmap) {
        //Bitmap mBitmap = BitmapFactory.decodeResource(res,resources);
        Bitmap mGrayBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mGrayBitmap);
        Paint mPaint = new Paint();

        //创建颜色变换矩阵
        ColorMatrix mColorMatrix = new ColorMatrix();
        //设置灰度影响范围
        mColorMatrix.setSaturation(0);
        //创建颜色过滤矩阵
        ColorMatrixColorFilter mColorFilter = new ColorMatrixColorFilter(mColorMatrix);
        //设置画笔的颜色过滤矩阵
        mPaint.setColorFilter(mColorFilter);
        //使用处理后的画笔绘制图像
        mCanvas.drawBitmap(mBitmap, 0, 0, mPaint);

        return mGrayBitmap;
    }

    //对图片进行倒影处理
    public static Bitmap getReflectedBitmap(Bitmap mBitmap) {
                 /*  BitmapDrawable mBitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.pet);
                  Bitmap mBitmap = mBitmapDrawable.getBitmap();*/
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        Matrix matrix = new Matrix();
        // 图片缩放，x轴变为原来的1倍，y轴为-1倍,实现图片的反转
        matrix.preScale(1, -1);

        //创建反转后的图片Bitmap对象，图片高是原图的一半。
        //Bitmap mInverseBitmap = Bitmap.createBitmap(mBitmap, 0, height/2, width, height/2, matrix, false);
        //创建标准的Bitmap对象，宽和原图一致，高是原图的1.5倍。
        //注意两种createBitmap的不同
        //Bitmap mReflectedBitmap = Bitmap.createBitmap(width, height*3/2, Config.ARGB_8888);

        Bitmap mInverseBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
        Bitmap mReflectedBitmap = Bitmap.createBitmap(width, height*2, Bitmap.Config.ARGB_8888);

        // 把新建的位图作为画板
        Canvas mCanvas = new Canvas(mReflectedBitmap);
        //绘制图片
        mCanvas.drawBitmap(mBitmap, 0, 0, null);
        mCanvas.drawBitmap(mInverseBitmap, 0, height, null);

        //添加倒影的渐变效果
        Paint mPaint = new Paint();
        Shader mShader = new LinearGradient(0, height, 0, mReflectedBitmap.getHeight(), 0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);
        mPaint.setShader(mShader);
        //设置叠加模式
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //绘制遮罩效果
        mCanvas.drawRect(0, height, width, mReflectedBitmap.getHeight(), mPaint);

        return mReflectedBitmap;
    }

    //对图片进行倾斜处理
    public static Bitmap getScrewBitmap(Bitmap mBitmap) {
//            BitmapDrawable mBitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.pet);
//         Bitmap mBitmap = mBitmapDrawable.getBitmap();
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preSkew(1.0f, 0.15f);
        Bitmap mScrewBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, true);

        return mScrewBitmap;
    }
    /**
     * 将给定图片维持宽高比缩放后，截取正中间的正方形部分。
     * @param bitmap      原图
     * @param edgeLength  希望得到的正方形部分的边长
     * @return  缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
    {
        if(null == bitmap || edgeLength <= 0)
        {
            return  null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if(widthOrg > edgeLength && heightOrg > edgeLength)
        {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try{
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            }
            catch(Exception e){
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try{
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            }
            catch(Exception e){
                return null;
            }
        }

        return result;
    }


    // 保存 bitmap 到SD卡F 同时保存到相簿
    public static boolean saveBitmapToSDCard(Bitmap bitmap, String filePath,
                                             String fileName, Context context) {
        boolean flag = false;
        if (null != bitmap) {
            try {
                fileName = fileName + ".jpg";
                File file = new File(filePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File f = new File(filePath + fileName);
                if (f.exists()) {
                    f.delete();
                }
                BufferedOutputStream outputStream = new BufferedOutputStream(
                        new FileOutputStream(f));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath+fileName, fileName, null);
                flag = true;
            } catch (FileNotFoundException e) {
                flag = false;
            } catch (IOException e) {
                flag = false;
            }
        }
        return flag;

    }

    /**
     *
     * @param drawable
     * @return bitmap
     */
    public static Bitmap drawableToBitmap2(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    /**
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmapTodrawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);
        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        return drawable;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 根据文字获取图片
     *
     * @param text
     * @return
     */
    public static Bitmap getIndustry(Context context, String text, int drawableId) {
        String color = "#ffeeeade";

        Bitmap src = BitmapFactory.decodeResource(context.getResources(),
                drawableId);
        int x = src.getWidth();
        int y = src.getHeight();
        Bitmap bmp = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        Canvas canvasTemp = new Canvas(bmp);
        canvasTemp.drawColor(Color.parseColor(color));
        Paint p = new Paint(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.parseColor("#ff4e0a13"));
        p.setAlpha(45);
        p.setFilterBitmap(true);
        int size = (int) (18 * context.getResources().getDisplayMetrics().density);
        p.setTextSize(size);
        float tX = (x - getFontlength(p, text)) / 2;
        float tY = (y - getFontHeight(p)) / 2 + getFontLeading(p);
        canvasTemp.drawText(text, tX, tY, p);
        return getRoundCornerImage(bmp, 2);
    }

    /**
     * @return 返回指定笔和指定字符串的长度
     */
    public static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    /**
     * @return 返回指定笔的文字高度
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     * @return 返回指定笔离文字顶部的基准距离
     */
    public static float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }

}
