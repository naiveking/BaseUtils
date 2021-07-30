package com.king.baseutils.custom.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 带跑表TextView
 * Created by NaiveKing on 2021/03/04.
 */
public class DynamicTextView extends AppCompatTextView {

    /**
     * 延迟
     */
    private final int DELAY = 20;
    /**
     * 保留小数位数  默认2为
     */
    private final int DECIMALS_COUNT = 2;
    private final int START_RUN = 101;
    private final int STOP_RUN = 102;
    /**
     * 跑的次数
     */
    private final int RUN_COUNT = 40;
    private float speed;
    private float startNum;
    private float endNum;
    /**
     * 保留小数位数
     */
    private int decimals = DECIMALS_COUNT;
    /**
     * 每次跑的次数
     */
    private int runCount = RUN_COUNT;
    /**
     * 动画延迟
     */
    private int delayMillis = DELAY;
    private boolean isAniming;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == START_RUN) {
                if (speed == 0) {
                    if (endNum != 0) {
                        speed = getSpeed();
                        startNum = speed;
                    } else {
                        return;
                    }
                }
                isAniming = !running();
                if (isAniming) {
                    sendEmptyMessageDelayed(START_RUN, delayMillis);
                } else {
                    speed = 0;
                    startNum = 0;
                }
            }
        }

        ;
    };

    public DynamicTextView(Context context) {
        super(context);
    }

    public DynamicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 开始数字跳动动画
     *
     * @return 动画是否结束
     */
    private boolean running() {
        setText(withDEC(String.valueOf(startNum)) + "");
        startNum += speed;
        if (startNum >= endNum) {
            setText(withDEC(String.valueOf(endNum)) + "");
            return true;
        }
        return false;
    }

    /**
     * 计算速度
     *
     * @return
     */
    private float getSpeed() {
        float speedFloat = withDEC(String.valueOf(endNum / runCount)).floatValue();
        return speedFloat;
    }

    /**
     * 判断是否是非负数
     *
     * @return
     */
    private boolean isNumber(String num) {
        if ("".equals(num) || num == null)
            return false;
        Pattern pattern = Pattern.compile("^\\d+$|\\d+\\.\\d+$");
        Matcher matcher = pattern.matcher(num);
        return matcher.find();
    }

    /**
     * 取整四舍五入 保留小数
     *
     * @param num
     * @return
     */
    private BigDecimal withDEC(String num) {
        return new BigDecimal(num).setScale(decimals, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 设置显示的数字
     *
     * @param num
     */
    public void setShowNum(String num) {
        setShowNum(num, DECIMALS_COUNT);
    }

    /**
     * 设置显示的数字
     *
     * @param num
     * @param decimals 要保留的小数位
     */
    public void setShowNum(String num, int decimals) {
        if (!isNumber(num)) {
            return;
        }
        setText(num);
        setDecimals(decimals);
        startRun();
    }

    /**
     * 开始跑
     */
    public void startRun() {
        if (isAniming) {
            return;
        }
        if (isNumber(getText().toString())) {
            endNum = withDEC(getText().toString()).floatValue();
            mHandler.sendEmptyMessage(START_RUN);
        }
    }

    public int getDecimals() {
        return decimals;
    }

    /**
     * 设置保留的小数位     0:不保留小数
     *
     * @param decimals
     */
    public void setDecimals(int decimals) {
        if (decimals >= 0) {
            this.decimals = decimals;
        }
        setText(String.format("%s", withDEC(getText().toString())));
    }

    public int getRunCount() {
        return runCount;
    }

    /**
     * 设置动画跑的次数
     *
     * @param runCount
     */
    public void setRunCount(int runCount) {
        if (runCount <= 0) {
            return;
        }
        this.runCount = runCount;
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    /**
     * 设置动画延迟
     *
     * @param delayMillis
     */
    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }
}