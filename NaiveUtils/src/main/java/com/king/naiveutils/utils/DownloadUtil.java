package com.king.naiveutils.utils;


import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.king.naiveutils.http.HttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 下载工具
 *
 * @author Gwall
 * @date 2019/12/5
 */
public class DownloadUtil {

    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = HttpClient.getDownClient();
    }

    /**
     * @param url         下载连接
     * @param versionName 下载文件版本名
     * @param listener    下载监听
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void download(String url, String versionName, String downApkPath, OnDownloadListener listener) {
        Observable.create((Observable.OnSubscribe<File>) subscriber -> {
                    String destFileName;
                    Request request = new Request.Builder().get().url(url).build();
                    destFileName = getFileName(url, versionName);
                    Call call = okHttpClient.newCall(request);
                    // 储存下载文件的目录
                    File dir = new File(downApkPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, destFileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            subscriber.onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) {
                            FileOutputStream fos = null;
                            InputStream is = null;
                            if (response.isSuccessful()) {
                                try {
                                    long total = response.body().contentLength();
                                    long sum = 0;
                                    fos = new FileOutputStream(file);
                                    is = response.body().byteStream();
                                    byte[] buf = new byte[1024 * 20];
                                    int len;
                                    while ((len = is.read(buf)) != -1) {
                                        fos.write(buf, 0, len);
                                        sum += len;
                                        int progress = (int) (sum * 1.0f / total * 100);
                                        // 下载中更新进度条
                                        listener.onDownloading(progress);
                                    }
                                    fos.flush();
                                    subscriber.onNext(file);
                                } catch (IOException e) {
                                    subscriber.onError(e);
                                } finally {
                                    try {
                                        if (is != null) {
                                            is.close();
                                        }
                                    } catch (IOException ignored) {
                                    }
                                    try {
                                        if (fos != null) {
                                            fos.close();
                                        }
                                    } catch (IOException ignored) {
                                    }
                                }
                            } else {
                                subscriber.onError(new Exception("Network exception " + response.code()));
                            }
                        }
                    });
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {

                    @Override
                    public void onNext(File file) {
                        listener.onDownloadSuccess(file);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDownloadFailed(e.getMessage());
                    }
                });
    }


    public String getFileName(String url, String versionName) {
        String fileName = null;
        if (!TextUtils.isEmpty(url)) {
            try {
                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                Request request = new Request.Builder()
                        .url(url)//请求接口。如果需要传参拼接到接口后面。
                        .build();//创建Request 对象
                Response response = client.newCall(request).execute();//得到Response 对象
                HttpUrl realUrl = response.request().url();
                if (realUrl != null) {
                    //截取逻辑规则根据不同得url而定
                    String temp = realUrl.toString();
                    fileName = temp.substring(temp.lastIndexOf("/") + 1);
                    fileName = fileName.substring(0, !fileName.contains(".apk") ? fileName.length() : fileName.indexOf(".apk") + 4);
                }
            } catch (IOException e) {
                e.printStackTrace();
                fileName = "Gp" + versionName + ".apk";
            }
        }
        if (TextUtils.isEmpty(fileName)) {
            return "Gp" + versionName + ".apk";
        }
        return fileName;
    }

    public interface OnDownloadListener {
        /**
         * @param file 下载成功后的文件
         */
        void onDownloadSuccess(File file);

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * @param error 下载异常信息
         */
        void onDownloadFailed(String error);
    }

}