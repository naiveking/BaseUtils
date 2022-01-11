package com.king.naiveutils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;

/**
 * Created by NaiveKing on 2021/05/28.
 */
@SuppressLint("PrivateApi")
public class BaseUtils {

    private static final Application sApp;

    static {
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (app == null)
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (final Exception e) {
            Log.e(BGABaseAdapterUtil.class.getSimpleName(), "Failed to get current application from AppGlobals." + e.getMessage());
            try {
                app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                Log.e(BGABaseAdapterUtil.class.getSimpleName(), "Failed to get current application from ActivityThread." + e.getMessage());
            }
        } finally {
            sApp = app;
        }
    }

    public static void init(Context context) {
        debug = (isApkInDebug(context));
    }

    public static Application getApp() {
        return sApp;
    }

    /**
     * debug模式，默认debug
     */
    public static boolean debug = false;
    /**
     * Retrofit 默认的通信域名
     */
    private static String defaultUrl = "http://192.168.0.1:8080/";

    public static void setDefaultUrl(String defaultUrl) {
        BaseUtils.defaultUrl = defaultUrl;
    }

    public static String getDefaultUrl() {
        return defaultUrl;
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    
}
