package com.king.naiveutils.inter;

/**
 * 网络请求回调
 * Created by NaiveKing on 2021/10/21.
 */
public interface onHttpCallBack {

    /**
     * 发起请求回调
     */
    void onStart();
    /**
     * 失败回调
     */
    void onError(String error);

    /**
     * 成功回调
     */
    void onSuccess(String json);


}
