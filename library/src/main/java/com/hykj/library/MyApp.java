package com.hykj.library;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;



import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Author: KiWi刘少帅 on 2017/5/21   16:17
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:
 */

public class MyApp extends Application {
    private static MyApp instance;
    private static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//针对7.0以上
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

    }

    public static MyApp getInstance(){
        if (instance==null){
            synchronized (MyApp.class){
                if (instance==null) {
                    instance=new MyApp();
                }
            }
        }
        return instance;
    }

    public static Context getContext(){
        return applicationContext;
    }
    //获取系统的语言环境
    public boolean isEnglish(){
        String language = Locale.getDefault().getLanguage();
        if (language.endsWith("en")){
            return true;
        }
        return false;
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            //加入自定义拦截器,可以在这里加全局的头,在拦截器里加
            //builder.addInterceptor(new LoggingInterceptor());
            OkHttpClient okHttpClient = builder.connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS).build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
