package com.king.naiveutils.inter;

import android.view.View;

/**
 * 判断连续两次点击的ClickListener
 *
 * @author Gwall
 * @date 2020/1/6
 */
public abstract class GwOnNoDoubleClickListener implements View.OnClickListener {
    private int mThrottleFirstTime = 500;
    private long mLastClickTime = 0;

    public GwOnNoDoubleClickListener() {
    }

    public GwOnNoDoubleClickListener(int throttleFirstTime) {
        mThrottleFirstTime = throttleFirstTime;
    }

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime > mThrottleFirstTime) {
            mLastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    public abstract void onNoDoubleClick(View v);
}
