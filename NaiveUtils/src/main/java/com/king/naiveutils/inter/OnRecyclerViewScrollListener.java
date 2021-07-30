package com.king.naiveutils.inter;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public abstract class OnRecyclerViewScrollListener extends RecyclerView.OnScrollListener implements OnBottomListener {

    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }

    /* LayoutManager的类型（枚举）*/
    protected LAYOUT_MANAGER_TYPE mLayoutManagerType;
    /*存储瀑布流每一列最下面的那个item的位置*/
    private int[] mLastPositions;
    /*最后一个可见的item的位置*/
    private int mLastVisibleItemPosition;
    /*记录当前滑动的状态*/
    private int mCurrentScrollState = 0;
    /*用于判断滑动方向，dy等于0，表示RecyclerView没有滑动，比如条目没充满RecyclerView的话，手势不管哪个方向滑都是0；dy大于0，RecyclerView向上滑动；dy小于0 ，RecyclerView向下滑动*/
    private int mDy;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        this.mDy = dy;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (mLayoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                mLayoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
            } else if (layoutManager instanceof GridLayoutManager) {
                mLayoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                mLayoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
            } else {
                throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (mLayoutManagerType) {
            case LINEAR:
                mLastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GRID:
                mLastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (mLastPositions == null) {
                    mLastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(mLastPositions);
                mLastVisibleItemPosition = findMax(mLastPositions);
                break;
        }

    }


    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        mCurrentScrollState = newState;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if (visibleItemCount > 0
                && mCurrentScrollState == RecyclerView.SCROLL_STATE_IDLE
                && mLastVisibleItemPosition >= totalItemCount - 1 && mDy > 0) {
            onBottom();
        }
    }


    @Override
    public void onBottom() {

    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
