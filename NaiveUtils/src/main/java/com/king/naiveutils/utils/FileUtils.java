package com.king.naiveutils.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;

import java.io.File;

/**
 * Created by NaiveKing on 2021/07/01.
 */
public class FileUtils {

    private static String TAG = "FileUtil";


    /**
     * 下载更新apk的储存目录
     *
     * @return 目录
     */
    public static String getDownloadPath(Context context) {
//        String path = context.getFilesDir().getAbsolutePath() + File.separator + "Download";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + AppUtils.getAppName() + "-Download";
        return newDir(path).getPath();
    }

    /**
     * 拍照图片照片的储存目录
     *
     * @return 目录
     */
    public static String getPhotosPath(Context context) {
//        String path = context.getCacheDir().getAbsolutePath() + File.separator + "Photos";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + AppUtils.getAppName() + "-Photos";
        return newDir(path).getPath();
    }


    /**
     * @param path 路径
     * @return 新建目录
     */
    public static File newDir(String path) {
        File dir = new File(path);
        boolean wasSuccessful = true;
        if (!dir.exists()) {
            wasSuccessful = dir.mkdirs();
        }
        if (!wasSuccessful) {
            Log.w(TAG, "mkdirs was not successful.");
        }
        return dir;
    }
} 