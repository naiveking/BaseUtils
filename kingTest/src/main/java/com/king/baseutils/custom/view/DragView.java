package com.king.baseutils.custom.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.king.baseutils.R;
import com.king.baseutils.custom.bean.DragMenu;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 可拖动的悬浮按钮
 * Created by linqs on 2017/12/21.
 */

public class DragView implements View.OnTouchListener {

    private final Builder mBuilder;

    private int mStatusBarHeight, mScreenWidth, mScreenHeight;

    //手指按下位置
    private int mStartX, mStartY, mLastX, mLastY;
    private boolean mTouchResult = false;

    private DragView(Builder builder) {
        mBuilder = builder;
        initDragView();
    }

    public View getDragView() {
        return mBuilder.view;
    }

    public Activity getActivity() {
        return mBuilder.activity;
    }

    public boolean getNeedNearEdge() {
        return mBuilder.needNearEdge;
    }

    public void setNeedNearEdge(boolean needNearEdge) {
        mBuilder.needNearEdge = needNearEdge;
        if (mBuilder.needNearEdge) {
            moveNearEdge();
        }
    }

    private void initDragView() {
        if (null == getActivity()) {
            throw new NullPointerException("the activity is null");
        }
        if (null == mBuilder.view) {
            throw new NullPointerException("the dragView is null");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mBuilder.activity.isDestroyed()) {
            return;
        }

        //屏幕宽高
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        if (null != windowManager) {
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            mScreenWidth = displayMetrics.widthPixels;
            mScreenHeight = displayMetrics.heightPixels;
        }

        //状态栏高度
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        mStatusBarHeight = frame.top;
        if (mStatusBarHeight <= 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                mStatusBarHeight = getActivity().getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        mBuilder.size = mScreenWidth / 6;
//        int left = mBuilder.needNearEdge ? 0 : mBuilder.defaultLeft;
        //demo 默认初始化在左上角
//        FrameLayout.LayoutParams layoutParams = createLayoutParams(left, mStatusBarHeight + mBuilder.defaultTop, 0, 0);
        //需求初始化右下角
        FrameLayout.LayoutParams layoutParams = createLayoutParams(mScreenWidth - mBuilder.size - mBuilder.defaultRight,
                mScreenHeight - mBuilder.size - mBuilder.defaultBottom, 0, 0);

//        Log.e("DragView", "width_value" + mScreenWidth + "/" + mBuilder.size + "/" + mBuilder.defaultRight);
//        Log.e("DragView", "height_value" + mScreenHeight + "/" + mBuilder.size + "/" + mBuilder.defaultBottom);

        FrameLayout rootLayout = (FrameLayout) getActivity().getWindow().getDecorView();
        rootLayout.addView(getDragView(), layoutParams);
        getDragView().setOnTouchListener(this);
    }

    private static DragView createDragView(Builder builder) {
        if (null == builder) {
            throw new NullPointerException("the param builder is null when execute method createDragView");
        }
        if (null == builder.activity) {
            throw new NullPointerException("the activity is null");
        }
        if (null == builder.view) {
            throw new NullPointerException("the view is null");
        }
        return new DragView(builder);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchResult = false;
                mStartX = mLastX = (int) event.getRawX();
                mStartY = mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int left, top, right, bottom;
                int dx = (int) event.getRawX() - mLastX;
                int dy = (int) event.getRawY() - mLastY;
                left = v.getLeft() + dx;
                if (left < 0) {
                    left = 0;
                }
                right = left + v.getWidth();
                if (right > mScreenWidth) {
                    right = mScreenWidth;
                    left = right - v.getWidth();
                }
                top = v.getTop() + dy;
                if (top < mStatusBarHeight + 2) {
                    top = mStatusBarHeight + 2;
                }
                bottom = top + v.getHeight();
                if (bottom > mScreenHeight) {
                    bottom = mScreenHeight;
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                //这里需设置LayoutParams，不然按home后回再到页面等view会回到原来的地方
                v.setLayoutParams(createLayoutParams(v.getLeft(), v.getTop(), 0, 0));
                float endX = event.getRawX();
                float endY = event.getRawY();
                if (Math.abs(endX - mStartX) > 20 || Math.abs(endY - mStartY) > 20) {
                    //防止点击的时候稍微有点移动点击事件被拦截了
                    mTouchResult = true;
                }
                if (mTouchResult && mBuilder.needNearEdge) {
                    //是否每次都移至屏幕边沿
                    moveNearEdge();
                }
                break;
        }
        return mTouchResult;
    }

    /**
     * 移至最近的边沿
     */
    private void moveNearEdge() {
        int left = getDragView().getLeft();
        int lastX;
        if (left + getDragView().getWidth() / 2 <= mScreenWidth / 2) {
            //贴合左边
            lastX = mBuilder.defaultLeft;
        } else {
            //贴合右边
            lastX = mScreenWidth - getDragView().getWidth() - mBuilder.defaultRight;
        }
        //此处加入一个弹动动画
        ValueAnimator valueAnimator = ValueAnimator.ofInt(left, lastX);
        valueAnimator.setDuration(300);
        valueAnimator.setRepeatCount(0);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int left = (int) animation.getAnimatedValue();
                getDragView().setLayoutParams(createLayoutParams(left, getDragView().getTop(), 0, 0));
            }
        });
        valueAnimator.start();
    }

    /**
     * 创建菜单属性
     *
     * @return 菜单样式
     */
    private FrameLayout.LayoutParams createLayoutParams(int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mBuilder.size, mBuilder.size);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    public static class Builder {

        private Activity activity;

        private int size = 100;

        private int defaultTop = 30;

        private int defaultLeft = 30;

        private int defaultRight = 30;

        private int defaultBottom = 60;

        private boolean needNearEdge = false;

        private AlertDialog.Builder builder;

        private DragMenuDialog.Builder mBuilder;

        private Dialog menuDialog;

        private View view;

        private int background = 0;

        private int menuIcon = 0;

        private onMenuItemClickListener listener;

        private ArrayList<DragMenu> dragMenus;
        /**
         * 底部弹出Dialog标记值
         */
        private boolean showDialogBottom = true;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setDefaultTop(int top) {
            this.defaultTop = top;
            return this;
        }

        public Builder setDefaultLeft(int left) {
            this.defaultLeft = left;
            return this;
        }

        public Builder setDefaultRight(int defaultRight) {
            this.defaultRight = defaultRight;
            return this;
        }

        public Builder setDefaultBottom(int defaultBottom) {
            this.defaultBottom = defaultBottom;
            return this;
        }

        /**
         * 设置是否贴边参数
         *
         * @param needNearEdge 是否
         * @return Builder
         */
        public Builder setNeedNearEdge(boolean needNearEdge) {
            this.needNearEdge = needNearEdge;
            return this;
        }

        /**
         * 默认FloatingActionButton的设置背景色
         *
         * @param background 颜色id
         * @return Builder
         */
        public Builder setBackground(int background) {
            this.background = background;
            return this;
        }

        /**
         * 设置按钮图标
         *
         * @param menuIcon 资源ID
         */
        public Builder setMenuIcon(int menuIcon) {
            this.menuIcon = menuIcon;
            return this;
        }

        /**
         * 设置Dialog显示模式（居中Dialog或底部弹出）
         * 此方法必须在setMenuItem 方法前调用设置，此处影响仅仅影响Dialog布局的底部圆角问题
         *
         * @param isBottom 是否底部弹出
         */
        public Builder setShowDialogBottom(boolean isBottom) {
            this.showDialogBottom = isBottom;
            return this;
        }

        /**
         * 设置菜单按钮数组（ArrayList加载）
         *
         * @param menu 菜单列表
         * @return Builder
         */
        public Builder setMenuItem(final ArrayList<DragMenu> menu) {
            dragMenus = menu;
            if (builder == null && menuDialog == null) {
                mBuilder = new DragMenuDialog.Builder(activity);
                mBuilder.setShowDialogBottom(showDialogBottom);
                mBuilder.setMenuList(dragMenus);
                mBuilder.setOnItemMenuClickListener(new DragMenuDialog.OnMenuItemClickListener() {
                    @Override
                    public void onItemMenuClick(int position) {
                        if (listener != null) {
                            listener.onItemClick(position, dragMenus.get(position).getMenuName());
                            menuDialog.cancel();
                        }
                    }
                });
                mBuilder.create();
                menuDialog = mBuilder.create();
            }
            return this;
        }


        /**
         * 设置自定义菜单图标
         *
         * @param menuView 菜单
         * @return Builder
         */
        public Builder setMenuView(View menuView) {
            if (menuView != null) {
                this.view = menuView;
            }
            return this;
        }

        /**
         * 设置菜单项点击事件
         *
         * @param listener 监听事件
         * @return Builder
         */
        public Builder setOnMenuClickListener(onMenuItemClickListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 开始构建
         *
         * @return DragView
         */
        public DragView build() {
            if (this.view == null) {
                //如无手动添加View，则默认初始化FloatingActionButton
                FloatingActionButton menuButton = new FloatingActionButton(activity);
                menuButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                if (menuIcon != 0) {
                    menuButton.setImageResource(menuIcon);
                } else {
                    menuButton.setImageResource(R.drawable.icon_menu_add);
                }
                if (background != 0) {
                    ColorStateList colorStateList = ContextCompat.getColorStateList(activity, background);
                    menuButton.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
                    menuButton.setBackgroundTintList(colorStateList);
                }
                menuButton.setRippleColor(ContextCompat.getColor(this.activity, R.color.transparent));
                menuButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dragMenus.size() == 1) {
                            listener.onItemClick(0, dragMenus.get(0).getMenuName());
                        } else {
                            menuDialog.show();
                            WindowManager.LayoutParams layoutParams = menuDialog.getWindow().getAttributes();
                            if (showDialogBottom) {
                                //底部弹出
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    Point point = new Point();
                                    activity.getWindowManager().getDefaultDisplay().getRealSize(point);
                                    layoutParams.width = point.x;//铺满
                                } else {
                                    layoutParams.width = activity.getWindowManager().getDefaultDisplay().getWidth();
                                }
                                menuDialog.getWindow().setAttributes(layoutParams);
                                menuDialog.getWindow().setGravity(Gravity.BOTTOM);
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    Point point = new Point();
                                    activity.getWindowManager().getDefaultDisplay().getRealSize(point);
                                    layoutParams.width = point.x - point.x / 6;//自定义宽度
                                } else {
                                    layoutParams.width = activity.getWindowManager().getDefaultDisplay().getWidth()
                                            - activity.getWindowManager().getDefaultDisplay().getWidth() / 6;
                                }
                                menuDialog.getWindow().setAttributes(layoutParams);
                            }
                        }
                    }
                });
                menuButton.setFocusable(false);
                this.view = menuButton;
            }
            return createDragView(this);
        }
    }

    /**
     * 菜单项点击接口
     */
    public interface onMenuItemClickListener {
        void onItemClick(int position, int menuNameId);
    }
}
