package com.king.naiveutils.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.LogUtils;
import com.king.naiveutils.R;
import com.king.naiveutils.databinding.RecyclerItemEmptyBinding;
import com.king.naiveutils.databinding.RecyclerItemFooterBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2017/12/27.
 */

public abstract class BaseRecyclerAdapter<T, V extends ViewBinding> extends RecyclerView.Adapter {

    /**
     * 空数据标识
     */
    public final int VIEW_TYPE_EMPTY = -1;
    /**
     * 尾部局
     */
    public final int VIEW_TYPE_FOOTER = -2;
    /**
     * 加载中状态
     */
    public static final int STATE_LOADING = 1;
    /**
     * 加载完成状态
     */
    public static final int STATE_COMPLETE = 2;
    /**
     * 加载完毕状态
     */
    public static final int STATE_LOADING_END = 3;
    /**
     * 数据源
     */
    private final List<T> mDataList = new ArrayList<>();

    /**
     * 标识当前加载状态，默认加载完成
     */
    private int loadState = 1;
    /**
     * 一页数据阈值(默认值)
     */
    private int pageDefaultSize = 20;

    /**
     * 标记是否显示尾布局
     */
    private boolean showFooter = false;

    /**
     * Item点击事件接口
     */
    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * Item长按事件接口
     */
    public interface onItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    /**
     * Item子View点击事件接口
     */
    public interface onItemViewClickListener {
        void onItemViewClick(View view, int position);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            return new EmptyHolder(RecyclerItemEmptyBinding.inflate((LayoutInflater.from(parent.getContext())), parent, false));
        } else if (viewType == VIEW_TYPE_FOOTER) {
            return new FooterHolder(RecyclerItemFooterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return onCreateHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindHolder((ViewHolder<T, V>) holder, position);
    }

    private void onBindHolder(ViewHolder<T, V> holder, int position) {
        if (position < mDataList.size()) {
            T t = mDataList.get(position);
            holder.bindView(t, holder.getViewBinding(), position);
        } else if (position + 1 == getItemCount()) {
            holder.bindView(null, null, position);
        }
    }

    public abstract RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType);


    /**
     * 获取数据大小值
     *
     * @return 数据源大小
     */
    @Override
    public int getItemCount() {
        if (mDataList.size() == 0) {
            return 1;
        } else if (getLoadState() == STATE_LOADING_END) {
            return mDataList.size() + 1;
        }
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ((getItemCount() == 1 || loadState == STATE_COMPLETE) && mDataList.size() == 0) {
            return VIEW_TYPE_EMPTY;
        } else if (position + 1 == getItemCount() && loadState == STATE_LOADING_END) {
            return VIEW_TYPE_FOOTER;
        }
        return super.getItemViewType(position);
    }

    /**
     * 适配器数据赋值初始化；
     *
     * @param lists 绑定适配器数据源
     */
    public void setData(List<T> lists) {
        mDataList.clear();
        mDataList.addAll(lists);
        if (lists.size() == 0) {
            setLoadState(STATE_COMPLETE);
        } else if (lists.size() < pageDefaultSize) {
            //加载完全部数据
            setLoadState(STATE_LOADING_END);
        } else {
            //加载中
            setLoadState(STATE_LOADING);
        }
        this.notifyDataSetChanged();
    }

    /**
     * 适配器数据添加更多
     *
     * @param lists 添加更多适配器的数据源
     */
    public void addMoreData(List<T> lists) {
        if (lists != null) {
            mDataList.addAll(lists);
            if (mDataList.size() == 0) {
                //加载完成
                setLoadState(STATE_COMPLETE);
            } else if (lists.size() < pageDefaultSize) {
                //加载完全部数据
                setLoadState(STATE_LOADING_END);
            } else {
                //加载完成
                setLoadState(STATE_LOADING);
            }
            this.notifyDataSetChanged();
        }
    }


    /**
     * 获取适配器position的对象
     *
     * @param position 适配器下标
     * @return 适配器下标对应数据对象
     */
    public T getItemData(int position) {
        return mDataList.get(position);
    }

    /**
     * 获取适配器数据集合
     *
     * @return 适配器数据源
     */
    public List<T> getDataList() {
        return mDataList;
    }

    /**
     * 清空适配器所有数据
     */
    public void clearData() {
        mDataList.clear();
        setLoadState(STATE_COMPLETE);
    }


    /**
     * 常规数据Holder
     *
     * @param <T>
     */
    public static class ViewHolder<T, V extends ViewBinding> extends RecyclerView.ViewHolder {

        private final V viewBinding;

        public ViewHolder(V binding) {
            super(binding.getRoot());
            viewBinding = binding;
        }

        public void bindView(T itemData, V itemBinding, int position) {

        }

        public V getViewBinding() {
            return viewBinding;
        }
    }

    /**
     * 空数据Holder
     */
    class EmptyHolder extends ViewHolder {
        TextView mTvEmpty;

        EmptyHolder(ViewBinding itemView) {
            super(itemView);
        }

        @Override
        public void bindView(Object o, ViewBinding viewBinding, int position) {
            super.bindView(o, viewBinding, position);
            mTvEmpty = itemView.findViewById(R.id.tv_empty);
            if (loadState == STATE_LOADING) {
                mTvEmpty.setVisibility(View.GONE);
            } else {
                mTvEmpty.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * 尾部局Holder
     */
    class FooterHolder extends ViewHolder {
        ProgressBar pbLoading;
        TextView tvLoading;
        TextView mTvEndText;
        LinearLayout llEnd;

        FooterHolder(ViewBinding itemView) {
            super(itemView);
        }

        @Override
        public void bindView(Object o, ViewBinding viewBinding, int position) {
            //FooterView 点击事件
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(itemView.getContext(), "这是Footer", Toast.LENGTH_SHORT).show();
//                }
//            });
            pbLoading = itemView.findViewById(R.id.pb_loading);
            tvLoading = itemView.findViewById(R.id.tv_loading);
            llEnd = itemView.findViewById(R.id.ll_end);
            mTvEndText = itemView.findViewById(R.id.tv_end_text);
            switch (loadState) {
                case STATE_LOADING: // 正在加载
                    if (showFooter) {
                        pbLoading.setVisibility(View.VISIBLE);
                        tvLoading.setVisibility(View.VISIBLE);
                    } else {
                        pbLoading.setVisibility(View.GONE);
                        tvLoading.setVisibility(View.GONE);
                    }
                    llEnd.setVisibility(View.GONE);
                    break;
                case STATE_COMPLETE: // 加载完成
                    pbLoading.setVisibility(View.GONE);
                    tvLoading.setVisibility(View.GONE);
                    llEnd.setVisibility(View.GONE);
                    break;
                case STATE_LOADING_END: // 加载到底
                    pbLoading.setVisibility(View.GONE);
                    tvLoading.setVisibility(View.GONE);
                    if (showFooter) {
                        llEnd.setVisibility(View.VISIBLE);
                    } else {
                        llEnd.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果当前是footer的位置，那么该item占据2个单元格，正常情况下占据1个单元格
                    if (getItemViewType(position) == VIEW_TYPE_EMPTY) {
                        return gridManager.getSpanCount();
                    } else if (getItemViewType(position) == VIEW_TYPE_FOOTER) {
                        return gridManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    /**
     * 暴露：设置加载完毕
     */
    public void isAllLoaded() {
        this.loadState = STATE_LOADING_END;
        notifyDataSetChanged();
    }

    /**
     * 暴露：设置状态
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    /**
     * 暴露：获取加载状态
     */
    public int getLoadState() {
        return loadState;
    }

    /**
     * 设置默认PageSize
     *
     * @param pageSize 页码数量
     */
    public void setDefaultPageSize(int pageSize) {
        this.pageDefaultSize = pageSize;
    }

    /**
     * 暴露：判断是否可加载更多
     */
    public boolean canLoadMore() {
        return loadState != STATE_LOADING && loadState != STATE_LOADING_END;
    }


    /**
     * 设置是否显示Footer布局
     *
     * @param showFooter 是否显示
     */
    public void showFooterEnd(boolean showFooter) {
        this.showFooter = showFooter;
    }
}