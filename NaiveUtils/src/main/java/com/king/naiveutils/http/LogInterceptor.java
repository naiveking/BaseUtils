package com.king.naiveutils.http;

import android.text.TextUtils;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * OkHttp日志拦截输出
 * Created by NaiveKing on 2022/1/19
 */
public class LogInterceptor implements Interceptor {

    private static final String TAG = "OkHttpLog";

    public LogInterceptor() {
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        //添加到责任链中
        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    /**
     * 打印响应日志
     *
     * @param response Response
     * @return Response
     */
    private Response logForResponse(Response response) {
        Log.e(TAG, "********响应日志开始*******");
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        Log.d(TAG, "url:" + clone.request().url());
        Log.d(TAG, "code:" + clone.code());
        if (!TextUtils.isEmpty(clone.message())) {
            Log.e(TAG, "message:" + clone.message());
        }
        ResponseBody body = clone.body();
        if (body != null) {
            MediaType mediaType = body.contentType();
            if (mediaType != null) {
                if (isText(mediaType)) {
                    String resp = null;
                    try {
                        resp = body.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "响应:" + resp);
                    Log.e(TAG, "********响应日志结束*******");
                    assert resp != null;
                    body = ResponseBody.create(mediaType, resp);
                    return response.newBuilder().body(body).build();
                } else {
                    Log.e(TAG, "响应内容 : " + "发生错误-非文本类型");
                }
            }
        }
        Log.e(TAG, "********响应日志结束*******");
        return response;
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            return mediaType.subtype().equals("json")
                    || mediaType.subtype().equals("xml")
                    || mediaType.subtype().equals("html")
                    || mediaType.subtype().equals("webviewhtml")
                    || mediaType.subtype().equals("x-www-form-urlencoded");
        }
        return false;
    }

    /**
     * 打印请求日志
     *
     * @param request Request
     */
    private void logForRequest(Request request) {
        String url = request.url().toString();
        Log.e(TAG, "========请求日志开始=======");
        Log.d(TAG, "请求方式 : " + request.method());
        Log.d(TAG, "url : " + url);
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            MediaType mediaType = requestBody.contentType();
            if (mediaType != null) {
                Log.d(TAG, "请求内容类别 : " + mediaType);
                if (isText(mediaType)) {
                    Log.d(TAG, "请求内容 : " + bodyToString(request));
                } else {
                    Log.d(TAG, "请求内容 : " + " 无法识别。");
                }
            }
        }
        Log.e(TAG, "========请求日志结束=======");
    }

    private String bodyToString(Request request) {
        Request req = request.newBuilder().build();
        String urlSub;
        Buffer buffer = new Buffer();
        try {
            req.body().writeTo(buffer);
            String message = buffer.readUtf8();
            message = message.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            message = message.replaceAll("\\+", "%2B");
            urlSub = URLDecoder.decode(message, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return "在解析请求内容时候发生了异常-非字符串";
        }
        return urlSub;
    }
}
