package com.king.naiveutils.inter;

import android.content.Context;

/**
 * Created by King on 2017/11/3.
 */

public interface IBaseView {
    /**
     * 获得父Context
     *
     * @return
     */
    Context getViewContext();

    /**
     * 刷新界面操作
     */
    void updateUI();

    /**
     * 显示异常错误弹窗
     */
    void showError(String error);

    void showToastMessage(String msg);

}
