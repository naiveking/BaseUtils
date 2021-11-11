package com.king.naiveutils.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;
import com.king.naiveutils.R;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Activity-基类
 *
 * @author Gwall -- 2020/1/6
 */
public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {

    protected Context mContext;

    protected LinearLayout mLlBaseHeadRoot;

    protected RelativeLayout mRlBaseHead;

    protected ImageButton mImgBtnBaseHeadLeft;

    protected ImageButton mImgBtnBaseHeadRight;

    protected TextView mTvBaseHeadTitle;

    protected TextView mTvBaseHeadRight;

    protected CheckBox mCheckInput;

    protected FrameLayout mFrameBaseViewRoot;

    protected T binding;

    /**
     * 异常提示Dialog
     */
    private Dialog mErrorDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.mContext = this;
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        initBseView();

        Type superclass = getClass().getGenericSuperclass();
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
            binding = (T) method.invoke(null, getLayoutInflater());
            setContentView(binding.getRoot());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        setStateBar(true, R.color.blue_user);//后续自定义Activity风格是可在activity单独设置

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
//            window.setStatusBarColor(getResources().getColor(R.color.theme_blue_color));// Resources中的getColor(int)已过时
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.theme_blue_color));
        }
//        setStatusBar();

        onViewCreate(savedInstanceState);

        AppManager.getInstance().addActivity(this);
    }


    public Activity getViewContext() {
        return this;
    }

    /**
     * 初始化BaseView
     */
    private void initBseView() {

        mLlBaseHeadRoot = findViewById(R.id.ll_base_head_root);
        mRlBaseHead = findViewById(R.id.rl_base_head);
        mImgBtnBaseHeadLeft = findViewById(R.id.img_btn_base_head_left);
        mImgBtnBaseHeadRight = findViewById(R.id.img_btn_base_head_right);
        mTvBaseHeadTitle = findViewById(R.id.tv_base_head_title);
        mTvBaseHeadRight = findViewById(R.id.tv_base_head_right);
        mFrameBaseViewRoot = findViewById(R.id.frame_base_view_root);
        mCheckInput = findViewById(R.id.checkbox_input);

        mImgBtnBaseHeadLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        mImgBtnBaseHeadRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolBarRightClick(v.getId());
            }
        });
        mTvBaseHeadRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolBarRightClick(v.getId());
            }
        });
    }

    /**
     * 设置沉浸式颜色以及主题色
     *
     * @param isDark true:暗主题白色字 or false:亮主题黑色字
     * @param color  颜色资源ID
     */
    public void setStateBar(boolean isDark, int color) {
//        StatusBarUtil.setColor(this, getResources().getColor(color));
//        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(color));
//        if (isDark) {
//            StatusBarUtil.setDarkMode(this);
//        } else {
//            StatusBarUtil.setLightMode(this);
//        }
    }

    /**
     * 设置右边
     *
     * @param s 字符内容
     */
    protected void setRightText(String s) {
        mTvBaseHeadRight.setVisibility(View.VISIBLE);
        if (s != null) {
            mTvBaseHeadRight.setText(s);
        }
    }

    /**
     * 设置右边
     *
     * @param id 字符资源ID
     */
    protected void setRightText(int id) {
        mTvBaseHeadRight.setVisibility(View.VISIBLE);
        mTvBaseHeadRight.setText(getString(id));
    }

    /**
     * 设置右边图标
     *
     * @param id 资源ID
     */
    public void setRightImage(int id) {
        mImgBtnBaseHeadRight.setVisibility(View.VISIBLE);
        mImgBtnBaseHeadRight.setImageResource(id);
    }


    @Override
    public void setContentView(int resId) {
        if (resId != -1) {
            View view = getLayoutInflater().inflate(resId, null);
            if (mFrameBaseViewRoot != null)
                mFrameBaseViewRoot.addView(view);
        }
    }

    @Override
    public void setContentView(View view) {
        if (mFrameBaseViewRoot != null) {
            mFrameBaseViewRoot.addView(view);
        }
    }

    /**
     * 设置标题栏背景色
     *
     * @param id ColorID
     */
    protected void setTitleToolBackground(int id) {
        mRlBaseHead.setBackgroundColor(id);
    }

    /**
     * 设置title
     */
    public void setTitle(String title) {
        mTvBaseHeadTitle.setText(title);
    }

    public String getBarTitle() {
        return mTvBaseHeadTitle.getText().toString();
    }

    /**
     * 设置title
     */
    public void setTitle(int title) {
        mTvBaseHeadTitle.setText(getString(title));
    }

    /**
     * 隐藏toolbear
     */
    public void hideToolbar() {
        mLlBaseHeadRoot.setVisibility(View.GONE);
    }

    /**
     * 隐藏back
     */
    public void hideBack() {
        mImgBtnBaseHeadLeft.setVisibility(View.GONE);
    }

    /**
     * 显示软键盘开关
     */
    public void showCheckInput() {
        mCheckInput.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏软键盘开关
     */
    public void hideCheckInput() {
        mCheckInput.setVisibility(View.GONE);
    }

    /**
     * 初始化view
     */
    protected abstract void onViewCreate(Bundle savedInstanceState);

//    /**
//     * 设置layout
//     *
//     * @return layout
//     */
//    protected abstract View setRootView();

    /**
     * 返回按钮
     */
    protected abstract void onBack();

    /**
     * 标题栏右边控件点击事件
     */
    protected abstract void onToolBarRightClick(int id);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
    }


    //========================================Intent 模块 ===============================================

    /**
     * 跳转到指定的Activity
     *
     * @param targetActivity 要跳转的目标Activity
     */
    public final void startActivity(@NonNull Class<?> targetActivity) {
        startActivity(new Intent(this, targetActivity));
    }

    /**
     * 跳转到指定的Activity
     *
     * @param flags          intent flags
     * @param targetActivity 要跳转的目标Activity
     */
    public final void startActivity(int flags, @NonNull Class<?> targetActivity) {
        final Intent intent = new Intent(this, targetActivity);
        intent.setFlags(flags);
        startActivity(new Intent(this, targetActivity));
    }

    /**
     * 跳转到指定的Activity
     *
     * @param data           Activity之间传递数据，Intent的Extra key为Constant.EXTRA_NAME.DATA
     * @param targetActivity 要跳转的目标Activity
     */
    public final void startActivity(@NonNull String key, @NonNull Bundle data, @NonNull Class<?> targetActivity) {
        final Intent intent = new Intent();
        intent.putExtra(key, data);
        intent.setClass(this, targetActivity);
        startActivity(intent);
    }

    /**
     * 跳转到指定的Activity
     *
     * @param data           Activity之间传递数据，Intent的Extra key为Constant.EXTRA_NAME.DATA
     * @param targetActivity 要跳转的目标Activity
     */
    public final void startActivity(@NonNull Bundle data, @NonNull Class<?> targetActivity) {
        final Intent intent = new Intent();
        intent.putExtras(data);
        intent.setClass(this, targetActivity);
        startActivity(intent);
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz    开启Activity
     * @param bundle 带参
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);

    }

    //========================================Intent 模块 ===============================================

}
