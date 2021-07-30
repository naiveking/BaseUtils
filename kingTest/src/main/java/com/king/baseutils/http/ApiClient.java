package com.king.baseutils.http;

import com.king.baseutils.BaseUtils;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Gwall
 * @date 2019/11/10
 */
public class ApiClient {

    public static Retrofit mRetrofit;

    public static Retrofit getHttp() {
        if (mRetrofit == null) {
            synchronized (ApiClient.class) {
                if (mRetrofit == null) {
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(BaseUtils.getDefaultUrl())
                            .client(HttpClient.getClient())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
//                            .addConverterFactory(JsonConverterFactory.create())//自定义解密Factory
                            .build();
                }
            }
        }
        return mRetrofit;
    }


}
