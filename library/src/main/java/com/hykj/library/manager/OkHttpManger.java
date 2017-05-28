package com.hykj.library.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hykj.library.MyApp;
import com.hykj.library.config.AppBack;
import com.hykj.library.utils.common.LL;
import com.hykj.library.utils.common.MySharedPreference;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


//import com.hykj.tzzhufang.cfg.AppBack;

/**
 * OkHttp 工具类，
 * get的同步异步请求
 * post的json字符串同步异步上传
 * post的键值对同步异步上传
 * post文件异步上传，回调结果以及进度
 * 异步下载文件，回调结果以及进度
 * <p>
 * Created by Seeker on 2016/6/24.
 */
public final class OkHttpManger {

    private static final String TAG = "MMM";

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType NORM_TYPE = MediaType.parse("text/plain; charset=utf-8");

    private Handler okHttpHandler;
    private OkHttpClient mOkHttpClient;

    private OkHttpManger() {
        this.mOkHttpClient = MyApp.getInstance().getUnsafeOkHttpClient();
        this.okHttpHandler = new Handler(Looper.getMainLooper());
    }

    public static final OkHttpManger getInstance() {
        return SingleFactory.manger;
    }

    private static final class SingleFactory {
        private static final OkHttpManger manger = new OkHttpManger();
    }

    //rxjava改造
//post异步上传map
    public Disposable postRx(final String url, final OKHttpUICallback2.ResultCallback callback, final Map<String, String> params) {
        LL.e("url-->"+url);
        LL.e("param-->"+JSON.toJSONString(params));
        Disposable disposable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<String> e) throws Exception {
                //获取数据
                Response response = postSync(url, params);
                String data = response.body().string();
                e.onNext(data);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        LL.e("resultData--->" + s);
                        Object objecttemp = JSON.parseObject(s, callback.mType);
                        if (objecttemp instanceof AppBack) {
                            AppBack appBack = (AppBack) objecttemp;
                            if (appBack.unSuccess()) {
                                return;
                            }
                        }
                        callback.onSuccess(objecttemp);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        callback.onError(throwable);
                    }
                });
        return disposable;
    }
//k;;;;0jjjku
    //klllllllllllllllll
    public String getStringFromUrl(String url,Map<String,String>map){
        if (map==null||map.isEmpty()){
            return url+"?";
        }
        StringBuffer sb=new StringBuffer();
        for (String s :
                map.keySet()) {
            //s,是Key
            sb.append("&"+s+"="+map.get(s));
        }
        sb.replace(0,1,"?");
        return url+sb.toString();
    }
    //get普通的异步请求,传入map
    //get异步请求
    public Disposable getMapRx(final String url, final Map<String,String>map, final OKHttpUICallback2.ResultCallback callback) {
        Disposable disposable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<String> e) throws Exception {
                String mapUrl = getStringFromUrl(url, map);
                Response response = getSync(mapUrl);
                String data = response.body().string();
                e.onNext(data);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        LL.e( "data " + s);
                        Object objecttemp = JSON.parseObject(s, callback.mType);
                        if (objecttemp instanceof AppBack) {
                            AppBack appBack = (AppBack) objecttemp;
                            if (appBack.unSuccess()) {
                                return;
                            }
                        }
                        callback.onSuccess(objecttemp);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                            callback.onError(throwable);
                    }
                });
            return disposable;
       /* Observer<String> observer = new Observer<String>() {
            @Override
            public void onNext(String data) {
                LogUtils.e( "data " + data);
                Object objecttemp = JSON.parseObject(data, callback.mType);
                if (objecttemp instanceof AppBack) {
                    AppBack appBack = (AppBack) objecttemp;
                    if (appBack.unSuccess()) {
                        return;
                    }
                }
                callback.onSuccess(objecttemp);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }
        };

        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                LogUtils.i("boservable_start"+url);
                try {
                    //Response response = postSync(url, params);
                    //LogUtils.i("boservable_start22");
                    String mapUrl = getStringFromUrl(Constant.url+url,map);
                    Response response = getSync(mapUrl);
                    //LogUtils.e("getsync"+response.body().string());
                    String data = response.body().string();
                    //LogUtils.i("observable data "+data);//TODO  不清楚有无错误

                    subscriber.onNext(data);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    //LogUtils.i("boservable_start"+e.toString());
                    subscriber.onError(e);
                }
            }
        });
        Subscription subscription = observable
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(observer);*/
    }
    //get普通的异步请求,自己拼接
    public Disposable getRx(final String url, final OKHttpUICallback2.ResultCallback callback) {
        LL.e("url-->"+url);
        Disposable disposable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<String> e) throws Exception {
                Response response = getSync(url);
                String data = response.body().string();
                e.onNext(data);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        LL.e("data-->" + s);
                        Object objecttemp = JSON.parseObject(s, callback.mType);
                        if (objecttemp instanceof AppBack) {
                            AppBack appBack = (AppBack) objecttemp;
                            if (appBack.unSuccess()) {
                                return;
                            }
                        }
                        callback.onSuccess(objecttemp);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        callback.onError(throwable);
                    }
                });
        return disposable;
    }

    //post异步提交json数据请求
    public Disposable postJsonRx(final String url, final OKHttpUICallback2.ResultCallback callback, final Map<String, String> params) {
        LL.e("url-->"+url);
        LL.e("params-->"+JSON.toJSONString(params));
        Disposable disposable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<String> e) throws Exception {
                String jsonString = JSON.toJSONString(params);
                Response response = postSyncJson(url, jsonString);
                String data = response.body().string();
                e.onNext(data);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        LL.e("data -->" + s);
                        Object objecttemp = JSON.parseObject(s, callback.mType);
                        if (objecttemp instanceof AppBack) {
                            AppBack appBack = (AppBack) objecttemp;
                            if (appBack.unSuccess()) {
                                return;
                            }
                        }
                        callback.onSuccess(objecttemp);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        callback.onError(throwable);
                    }
                });
        return disposable;

      /*  Log.e(TAG, "params " + params.toString());
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onNext(String data) {
                Log.e(TAG, "data " + data);
                Object objecttemp = JSON.parseObject(data, callback.mType);
                if (objecttemp instanceof AppBack) {
                    AppBack appBack = (AppBack) objecttemp;
                    if (appBack.unSuccess()) {
                        return;
                    }
                }
                callback.onSuccess(objecttemp);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }
        };
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    //Response response = postSync(url, params);
                    String jsonString = JSON.toJSONString(params);
                    Response response = postSyncJson(url, jsonString);
                    String data = response.body().string();
                    //           Log.e(TAG, "observable data " + data);

                    subscriber.onNext(data);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }

            }
        });
        Subscription subscription = observable
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(observer);*/
    }

    /**
     * 同步get请求
     *
     * @param url 地址
     * @return Response 返回数据
     */
    public Response getSync(final String url) throws IOException {
        final Request request = new Request.Builder().url(url).build();
        final Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    /**
     * 同步post请求,键值对
     *
     * @param url    地址
     * @param params 参数
     *               Request.Builder().url(url).post(builder.build()).build();
     */
    public Response postSync(String url, Param... params) throws IOException {
        final Request request = buildPostRequst(url, params);
        final Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    /**
     * 同步post请求,键值对
     *
     * @param url    地址
     * @param params 参数
     */
    public Response postSync(String url, Map<String, String> params) throws IOException {
        final Request request = buildPostRequst(url, params);
        final Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    /**
     * post同步请求，提交Json数据
     *
     * @param url  地址
     * @param json json格式的字符串
     * @return Response
     */
    public Response postSyncJson(String url, String json) throws IOException {
        final RequestBody requestBody = RequestBody.create(JSON_TYPE, json);
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的文件上传
     *
     * @param url     地址
     * @param file    提交的文件
     * @param fileKey 提交的文件key
     * @return Response
     */
    public Response uploadSync(String url, File file, String fileKey) throws IOException {
        return uploadSync(url, new File[]{file}, new String[]{fileKey}, new Param[0]);
    }


    /**
     * post异步上传,键值对 图片
     *
     * @param url      :地址
     * @param callback :回调
     */
    public void postAsyncUpFile(String url, File[] files, String[] fileKeys, Param[] params1, OKHttpUICallback.ResultCallback callback) {
        //TODO TEST

        final RequestBody requestBody = buildMultipartFormRequestBody(files, fileKeys, params1);
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        final Call call = mOkHttpClient.newCall(request);
        deliveryResult(call, callback);
    }


    /**
     * 同步基于post的文件上传
     *
     * @param url      地址
     * @param files    提交的文件数组
     * @param fileKeys 提交的文件数组key
     * @param params   提交的键值对
     * @return Response
     */
    public Response uploadSync(String url, File[] files, String[] fileKeys, Param[] params) throws IOException {
        final RequestBody requestBody = buildMultipartFormRequestBody(files, fileKeys, params);
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        return mOkHttpClient.newCall(request).execute();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 异步get请求
     *
     * @param url      地址
     * @param callback 回调
     */
    public void getAsync(final String url, final OKHttpUICallback.ResultCallback callback) {
        final Request request = new Request.Builder().url(url).build();
        final Call call = mOkHttpClient.newCall(request);
        deliveryResult(call, callback);
    }


    /**
     * post异步请求,键值对 图片
     *
     * @param url      :地址
     * @param params   :参数
     * @param callback :回调
     */
    public void postAsyncFile(String url, OKHttpUICallback.ResultCallback callback, Param... params) {
        final Call call = mOkHttpClient.newCall(buildPostRequst(url, params));
        deliveryResultFile(call, callback);
    }


    /**
     * post异步请求,键值对
     *
     * @param url      :地址
     * @param params   :参数
     * @param callback :回调
     */
    public void postAsync(String url, OKHttpUICallback.ResultCallback callback, Param... params) {


        final Call call = mOkHttpClient.newCall(buildPostRequst(url, params));
        deliveryResult(call, callback);
    }

    /**
     * post异步请求,键值对
     *
     * @param url      :地址
     * @param params   :参数
     * @param callback :回调
     */
    public void postAsync(String url, OKHttpUICallback.ResultCallback callback, Map<String, String> params) {
        final Call call = mOkHttpClient.newCall(buildPostRequst(url, params));
        deliveryResult(call, callback);
    }


    /**
     * post异步请求，提交Json数据
     *
     * @param url  地址
     * @param json json格式的字符串
     */
    public void postAsyncJson(String url, String json, OKHttpUICallback.ResultCallback callback) throws IOException {
        final RequestBody requestBody = RequestBody.create(JSON_TYPE, json);
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        deliveryResult(mOkHttpClient.newCall(request), callback);
    }


    interface MyCallback {
        void onSuccess(String result);

        void onFailture();
    }

    public void postAsyncJsonn(String url, String json, MyCallback mCallback) throws IOException {
        final RequestBody requestBody = RequestBody.create(JSON_TYPE, json);
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        deliveryResult(mOkHttpClient.newCall(request), mCallback);
    }

    private void deliveryResult(final Call call, final MyCallback mCallback) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                okHttpHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onFailture();
                        }
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseStr = response.body().string();

                okHttpHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onSuccess(responseStr);
                        }
                    }
                });

            }
        });
    }

    /**
     * 生成request
     *
     * @param url
     * @param params
     * @return
     */
    private Request buildPostRequst(String url, Param... params) {
        if (params == null) {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        return new Request.Builder().url(url).post(builder.build()).build();
    }

    /**
     * 生成request
     *
     * @param url
     * @param params
     * @return
     */
    private Request buildPostRequst(String url, Map<String, String> params) {
        Request request = null;
        if (params == null) {
            params = new HashMap<>();
        }
        if (params != null) {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : entries) {
                builder.add(entry.getKey(), entry.getValue());
            }
            request = new Request.Builder().url(url).post(builder.build()).build();
        }
        return request;
    }

    /**
     * 生成post提交时的分块request
     *
     * @param files
     * @param fileKeys
     * @param params
     * @return
     */
    private RequestBody buildMultipartFormRequestBody(File[] files, String[] fileKeys, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }
        if (files == null) {
            files = new File[0];
        }
        if (fileKeys == null) {
            fileKeys = new String[0];
        }

        if (fileKeys.length != files.length) {
            throw new ArrayStoreException("fileKeys.length != files.length");
        }
        RequestBody fileBody = null;
        int length = files.length;
        for (int i = 0; i < length; i++) {
            File file = files[i];
            String fileName = file.getName();
            fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
            //TODO 根据文件名设置contentType
            builder.addPart(Headers.of("Content-Disposition",
                    "form-data; name=\"" + fileKeys[i] + "\"; fileName=\"" + fileName + "\""),
                    fileBody);
            Log.e("form-data; name=", fileKeys[i]);
        }
        return builder.build();
    }


    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(path);
        if (type == null) {
            type = "application/octet-stream";
        }
        return type;
    }

    /**
     * 获取文件名
     *
     * @param path
     */
    private String getFileName(String path) {
        int lastSeparaorIndex = path.lastIndexOf("/");
        return (lastSeparaorIndex < 0) ? path : path.substring(lastSeparaorIndex + 1, path.length());
    }

    /**
     * 数据请求并处理
     *
     * @param call
     * @param callback
     */
    private void deliveryResult(final Call call, final OKHttpUICallback.ResultCallback callback) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                okHttpHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onError(call, e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (callback != null) {


                    Object objecttemp;
                    try {

                        String data = response.body().string();
                        //Log.e(TAG,data);********************************
                        objecttemp = JSON.parseObject(data, callback.mType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    final Object object = objecttemp;
                    okHttpHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(object);
                        }
                    });

                }
            }
        });
    }


    /**
     * 数据请求并处理 图片
     *
     * @param call
     * @param callback
     */
    private void deliveryResultFile(final Call call, final OKHttpUICallback.ResultCallback callback) {

        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                okHttpHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onError(call, e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                if (callback != null) {
                    if (response.isSuccessful()) {
                        final Object object = response.body().bytes();
                        okHttpHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(object);
                            }
                        });
                    } else {
                        okHttpHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onError(call, new IOException("传输失败"));
                                }
                            }
                        });
                    }
                }
            }
        });
    }


    public static final class Param {
        private String key;
        private String value;

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }


    public void uploadMultiFile(String upload, String url, File file, OKHttpUICallback.ResultCallback callback) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(upload, file.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        final Call call = mOkHttpClient.newCall(request);
        deliveryResult(call, callback);

    }


    public void uploadMultiFile2(String url, File file, OKHttpUICallback.ResultCallback callback) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);


        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + "op" + "\""),
                RequestBody.create(null, "UploadIDCardPhoto"));


        RequestBody requestBody = builder
                .setType(MultipartBody.FORM)
                .addFormDataPart("pic", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        final Call call = mOkHttpClient.newCall(request);
        deliveryResult(call, callback);

    }


    public void uploadMultiFile3(String url, File file, OKHttpUICallback.ResultCallback callback) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);


        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + "op" + "\""),
                RequestBody.create(null, "UploadHeadPhoto"));

        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + "userId" + "\""),
                RequestBody.create(null, MySharedPreference.get("userId", "", MyApp.getInstance().getBaseContext())));


        RequestBody requestBody = builder
                .setType(MultipartBody.FORM)
                .addFormDataPart("pic", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        final Call call = mOkHttpClient.newCall(request);
        deliveryResult(call, callback);

    }
    //上传图片,同时上传参数
    public void uploadFileAndParams(String upload, String url, File file, OKHttpUICallback.ResultCallback callback,Map<String,Object>params){

        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        requestBody.addFormDataPart(upload,file.getName(),fileBody);
        for (String key :
                params.keySet()) {
            requestBody.addFormDataPart(key, String.valueOf(params.get(key)));
        }
        //MultipartBody build = requestBody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody.build())
                .build();
        final Call call = mOkHttpClient.newCall(request);
        deliveryResult(call, callback);

    }


    /**
     * post异步请求，提交Json数据
     *
     * @param url  地址
     * @param json json格式的字符串
     */
    public void postAsyncShenfz(String url, String json, OKHttpUICallback.ResultCallback callback) {
        final RequestBody requestBody = RequestBody.create(NORM_TYPE, json);


        Request request = new Request.Builder().url(url).addHeader("Authorization", "APPCODE fdf6b3977fce48d981b9c21b27566e6a")
                .post(requestBody).build();
        deliveryResult(mOkHttpClient.newCall(request), callback);
    }


}

