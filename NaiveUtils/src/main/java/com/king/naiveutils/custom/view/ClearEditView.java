package com.king.naiveutils.custom.view;


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.king.naiveutils.R;

import java.util.Objects;

/**
 * 带清除功能EditView
 * Created by NaiveKing on 2022/1/13
 */
public class ClearEditView extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {

    /**
     * 删除图标引用对象
     */
    private Drawable mClearDrawable;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 是否有焦点
     */
    private boolean hasFocus;

    public ClearEditView(@NonNull Context context) {
        this(context, null);
        this.mContext = context;
    }

    public ClearEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        //复用EditView 原生属性定义
        this(context, attrs, android.R.attr.editTextStyle);
        this.mContext = context;
    }

    public ClearEditView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        //获取EditView 的DrawableRight，假如没有，我们设置默认图标
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
//            mClearDrawable = getResources().getDrawable(R.drawable.icon_button_close);
            mClearDrawable = ContextCompat.getDrawable(mContext, R.drawable.icon_button_close);
        }
        if (mClearDrawable != null) {
            mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        }
        //默认隐藏图标
        setClearIconVisible(false);
        //设置焦点变化监听事件
        setOnFocusChangeListener(this);
        //设置输入框内容变动监听事件
        addTextChangedListener(this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mClearDrawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            //判断触摸点是否在水平范围内
            boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight())) &&
                    (x < (getWidth() - getPaddingRight()));
            //获取删除图标的边界，返回一个Rect对象
            Rect rect = mClearDrawable.getBounds();
            //获取删除图标的高度
            int height = rect.height();
            int y = (int) event.getY();
            //计算图标距离控件底部的距离
            int distance = (getHeight() - height) / 2;
            //判断触摸点是否在垂直范围内（存在误差）
            //触摸点在纵坐标的distance到 distance+图标自身高度 之内，则视为点中删除图标
            boolean isInnerHeight = (y > distance) && (y < (distance + height));
            if (isInnerHeight && isInnerWidth) {
                this.setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置删除图标的显示与隐藏，通过调用setCompoundDrawables 为EditText 绘制
     *
     * @param visible 控制值
     */
    private void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        //监控焦点、字符输入长度变化，判断输入框的清楚图标、提示显示隐藏
        ClearEditView editView = (ClearEditView) v;
        this.hasFocus = hasFocus;
        if (hasFocus) {
            if (!isEnabled()) {
                return;
            }
            setClearIconVisible(Objects.requireNonNull(getText()).length() > 0);
            if (editView.getHint() != null) {
                editView.setTag(editView.getHint().toString());
            }
            editView.setHint(null);
        } else {
            setClearIconVisible(false);
            if (editView.getTag() != null) {
                editView.setHint(editView.getTag().toString());
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        onFocusChange(this, enabled);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (hasFocus) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.startAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒晃动多少下
     * @return 动画属性
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }


    public String getTextData() {
        return Objects.requireNonNull(super.getText()).toString().trim();
    }

    public void getFocus() {
        postDelayed(ClearEditView.super::requestFocus, 200);
    }
}
