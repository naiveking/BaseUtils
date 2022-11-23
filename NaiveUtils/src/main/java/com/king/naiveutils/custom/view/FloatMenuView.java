package com.king.naiveutils.custom.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.king.naiveutils.custom.adapter.FloatMenuAdapter;
import com.king.naiveutils.custom.bean.FloatMenuBean;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @author NaiveKing
 * @date 2022/11/23
 */
public class FloatMenuView implements View.OnTouchListener {

    private final FloatMenuView.Builder mBuilder;

    private int mStatusBarHeight, mScreenWidth, mScreenHeight;

    //手指按下位置
    private int mStartX, mStartY, mLastX, mLastY;
    private boolean mTouchResult = false;

    private FloatMenuView(FloatMenuView.Builder builder) {
        mBuilder = builder;
        initDragView();
    }

    public RecyclerView getDragView() {
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
//        FrameLayout.LayoutParams layoutParams = createLayoutParams(mScreenWidth - mBuilder.size - mBuilder.defaultRight,
//                mScreenHeight - mBuilder.size - mBuilder.defaultBottom, 0, 0);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0,
                mScreenHeight - mBuilder.size, 0, 0);

        //将控件填充进入依赖的Activity
        FrameLayout rootLayout = (FrameLayout) getActivity().getWindow().getDecorView();
        rootLayout.addView(getDragView(), layoutParams);
//        getDragView().setOnTouchListener(this);
    }

    private static FloatMenuView createDragView(FloatMenuView.Builder builder) {
        if (null == builder) {
            throw new NullPointerException("the param builder is null when execute method createDragView");
        }
        if (null == builder.activity) {
            throw new NullPointerException("the activity is null");
        }
        if (null == builder.view) {
            throw new NullPointerException("the view is null");
        }
        return new FloatMenuView(builder);
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
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    public static class Builder {

        private Activity activity;

        private int size = 100;

        private int defaultTop = 30;

        private int defaultLeft = 0;

        private int defaultRight = 0;

        private int defaultBottom = 60;

        private boolean needNearEdge = false;

        private ArrayList<FloatMenuBean> listData;

        private DragMenuDialog.Builder mBuilder;

        private RecyclerView view;

        private FloatMenuView.onMenuItemClickListener listener;

        public FloatMenuView.Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public FloatMenuView.Builder setDefaultTop(int top) {
            this.defaultTop = top;
            return this;
        }

        public FloatMenuView.Builder setDefaultLeft(int left) {
            this.defaultLeft = left;
            return this;
        }

        public FloatMenuView.Builder setDefaultRight(int defaultRight) {
            this.defaultRight = defaultRight;
            return this;
        }

        public FloatMenuView.Builder setDefaultBottom(int defaultBottom) {
            this.defaultBottom = defaultBottom;
            return this;
        }

        public FloatMenuView.Builder setMenuItem(ArrayList<FloatMenuBean> listData) {
            this.listData = listData;
            return this;
        }

        public FloatMenuView.Builder setListener(onMenuItemClickListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 设置是否贴边参数
         *
         * @param needNearEdge 是否
         * @return Builder
         */
        public FloatMenuView.Builder setNeedNearEdge(boolean needNearEdge) {
            this.needNearEdge = needNearEdge;
            return this;
        }


        /**
         * 开始构建
         *
         * @return DragView
         */
        public FloatMenuView build() {
            if (this.view == null) {
                //如无手动添加View，则默认初始化FloatingActionButton
                RecyclerView recyclerView = new RecyclerView(activity);
                LinearLayoutManager manager = new LinearLayoutManager(activity);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(manager);
                if (listData == null) {
                    listData = new ArrayList<>();
                }
                FloatMenuAdapter menuAdapter = new FloatMenuAdapter(listData);
                menuAdapter.setOnItemMenuClick(new FloatMenuAdapter.onItemMenuClick() {
                    @Override
                    public void onItemClick(FloatMenuBean menuBean, int position) {
                        if (listener != null) {
                            listener.onItemClick(position, menuBean);
                        }
                    }
                });
                recyclerView.setAdapter(menuAdapter);
                menuAdapter.notifyDataSetChanged();
                this.view = recyclerView;
            }
            return createDragView(this);
        }
    }

    public interface onMenuItemClickListener {
        void onItemClick(int position, FloatMenuBean menuBean);
    }
}