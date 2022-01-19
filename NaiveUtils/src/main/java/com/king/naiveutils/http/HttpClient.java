package com.king.naiveutils.http;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

import com.king.naiveutils.BaseUtils;
import com.king.naiveutils.inter.onHttpCallBack;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Gwall - 2019/11/10
 */
public class HttpClient {

    private static OkHttpClient mClient;

    private static OkHttpClient mDownClient;


    public static OkHttpClient getDownClient() {
        if (mDownClient == null) {
            synchronized (HttpClient.class) {
                if (mDownClient == null) {
//                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//                    if (BaseUtils.debug) {
//                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//                    } else {
//                        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
//                    }
                    mDownClient = new OkHttpClient().newBuilder()
                            //错误重连
                            .retryOnConnectionFailure(false)
                            //超时重连
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .connectTimeout(10, TimeUnit.SECONDS)
                            //日志拦截输出，发布正式包禁用；
//                            .addInterceptor(interceptor)
                            .build();
                }
            }
        }
        return mDownClient;
    }


    public static OkHttpClient getClient() {
        if (mClient == null) {
            synchronized (HttpClient.class) {
                if (mClient == null) {
                    //日志拦截输出，发布正式包禁用；
//                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//                    if (BaseUtils.debug) {
//                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//                    } else {
//                        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
//                    }
                    mClient = RetrofitUrlManager.getInstance()
                            .with(new OkHttpClient.Builder())
                            //错误重连
                            .retryOnConnectionFailure(false)
                            //超时重连
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .connectTimeout(10, TimeUnit.SECONDS)
                            //日志拦截输出，发布正式包禁用；
                            .addInterceptor(BaseUtils.debug ? new LogInterceptor() : null)
                            .build();
                }
            }
        }

//        Interceptor cacheInterceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//
//                int maxAge = 60 * 60; // 有网络时 设置缓存超时时间1个小时
//                int maxStale = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
//                Request request = chain.request();
//                if (NetworkUtils.isAvailableByPing()) {
//                    request = request.newBuilder()
//                            .cacheControl(CacheControl.FORCE_NETWORK)//有网络时只从网络获取
//                            .build();
//                } else {
//                    request = request.newBuilder()
//                            .cacheControl(CacheControl.FORCE_CACHE)//无网络时只从缓存中读取
//                            .build();
//                }
//                Response response = chain.proceed(request);
//                if (NetworkUtils.isAvailableByPing()) {
//                    response = response.newBuilder()
//                            .removeHeader("Pragma")
//                            .header("Cache-Control", "public, max-age=" + maxAge)
//                            .build();
//                } else {
//                    response = response.newBuilder()
//                            .removeHeader("Pragma")
//                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                            .build();
//                }
//                return response;
//            }
//        };
//
////        File cacheFile = new File(Environment.getExternalStorageDirectory(), Constans.CACHE_PAHT);
////        Cache cache = new Cache(cacheFile, 1024 * 1024 * 10);//缓存文件为10MB、
////        ClearableCookieJar cookieJar =
////                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(App.getInstance()));
//
//        OkHttpClient client = new OkHttpClient.Builder()
////                .cookieJar(cookieJar)
////                .addInterceptor(new LogInterceptor())
////                .cache(cache)
//                .retryOnConnectionFailure(true)
//                .connectTimeout(15, TimeUnit.SECONDS)
////                .addInterceptor(cacheInterceptor)
//                .build();
        return mClient;
    }


    /**
     * 可同时提交表单，和多文件
     * 根据url和键值对，发送异步Post请求
     *
     * @param uploadUrl    上传通信url地址
     * @param map          提交的表单的每一项组成的HashMap（如用户名，key:username,value:zhangsan）
     * @param imageFileKey 文件上传Builder key
     * @param fileNames    完整的上传的文件的路径名
     * @param callback     OkHttp的回调接口
     */
    public static void doPostUploadRequest(String uploadUrl, HashMap<String, String> map, String imageFileKey, List<String> fileNames, onHttpCallBack callback) {
        callback.onStart();
        Observable.just(getRequest(uploadUrl, map, imageFileKey, fileNames))
                .map(new Func1<Request, String>() {
                    @Override
                    public String call(Request request) {
                        Call call = getDownClient().newCall(request);
                        String json;
                        try {
                            json = Objects.requireNonNull(call.execute().body()).string();
                        } catch (IOException e) {
                            json = e.getMessage();
                        }
                        return json;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onError(throwable.getMessage());
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String message;
                        if (e instanceof HttpException || e instanceof UnknownHostException) {
                            message = "网络异常";
                        } else if (e instanceof RuntimeException) {
                            message = "网络请求出现异常:请检查";
                        } else if (e instanceof ConnectException) {
                            message = "连接失败";
                        } else {
                            message = "网络请求失败,请检查您的网络";
                        }
                        callback.onError(message);
                    }

                    @Override
                    public void onNext(String s) {
                        callback.onSuccess(s);
                    }
                });
    }

    /**
     * 通过Url地址和表单的键值对来创建Request实例
     *
     * @param url       上传表单数据到服务器的地址
     * @param map       由提交的表单的每一项组成的HashMap
     *                  （如用户名，key:username,value:zhangsan）
     * @param fileNames 完整的文件路径名
     * @return
     */
    private static Request getRequest(String url, HashMap<String, String> map, String imageFileKey, List<String> fileNames) {
        Request.Builder builder = new Request.Builder();
        builder.url(url).post(getRequestBody(map, imageFileKey, fileNames)).tag(url); //设置请求的标记，可在取消时使用
        return builder.build();
    }

    /**
     * 根据表单的键值对和上传的文件生成RequestBody
     *
     * @param map       由提交的表单的每一项组成的HashMap
     *                  （如用户名，key:username,value:zhangsan）
     * @param fileNames 完整的文件路径名
     * @return
     */
    private static RequestBody getRequestBody(HashMap<String, String> map, String imageFileKey, List<String> fileNames) {
        MultipartBody.Builder builder = new MultipartBody.Builder(); //创建MultipartBody.Builder，用于添加请求的数据
        for (HashMap.Entry<String, String> entry : map.entrySet()) { //对键值对进行遍历
            builder.addFormDataPart(entry.getKey(), entry.getValue()); //把键值对添加到Builder中
        }
        for (int i = 0; i < fileNames.size(); i++) { //对文件进行遍历
            File file = new File(fileNames.get(i)); //生成文件
            String fileType = getMimeType(file.getName()); //根据文件的后缀名，获得文件类型
            builder.addFormDataPart( //给Builder添加上传的文件
                    imageFileKey,  //请求的名字
                    file.getName(), //文件的文字，服务器端用来解析的
                    RequestBody.create(MediaType.parse(fileType), file) //创建RequestBody，把上传的文件放入
            );
        }
        return builder.build(); //根据Builder创建请求
    }

    /**
     * 获取文件MimeType
     *
     * @param filename 文件名
     * @return 返回文件类型
     */
    private static String getMimeType(String filename) {
        FileNameMap filenameMap = URLConnection.getFileNameMap();
        String contentType = filenameMap.getContentTypeFor(filename);
        if (contentType == null) {
            contentType = "application/octet-stream"; //* exe,所有的可执行程序
        }
        return contentType;
    }


}
