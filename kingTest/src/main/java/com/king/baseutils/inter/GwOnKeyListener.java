package com.king.baseutils.inter;

import android.view.KeyEvent;
import android.view.View;

/**
 * 判断联系两次出发的回车、下一步Key操作
 *
 * @author Gwall
 * @CreateDate 2020/1/14 18:03
 */
public abstract class GwOnKeyListener implements View.OnKeyListener {

    private long mLastClickTime = 0;

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
            long currentTime = System.currentTimeMillis();
            int mThrottleFirstTime = 400;
            if (currentTime - mLastClickTime > mThrottleFirstTime) {
                mLastClickTime = currentTime;
                onGwKey(v, keyCode, event);
                return true;
            }
        }
        return false;
    }

    public abstract void onGwKey(View v, int keyCode, KeyEvent event);
}
