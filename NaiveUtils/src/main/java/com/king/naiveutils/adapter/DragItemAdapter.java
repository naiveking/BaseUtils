package com.king.naiveutils.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.king.naiveutils.R;
import com.king.naiveutils.custom.bean.DragMenu;
import com.king.naiveutils.databinding.RecyclerItemMenuBinding;
import com.king.naiveutils.inter.GwOnNoDoubleClickListener;


/**
 * 悬浮子菜单适配器
 * Created by NaiveKing on 2021/07/23.
 */
public class DragItemAdapter extends BaseRecyclerAdapter<DragMenu, RecyclerItemMenuBinding> {

    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(BaseRecyclerAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new DragMenuViewHolder(RecyclerItemMenuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    class DragMenuViewHolder extends ViewHolder<DragMenu, RecyclerItemMenuBinding> {

        public DragMenuViewHolder(RecyclerItemMenuBinding binding) {
            super(binding);
        }

        @Override
        public void bindView(DragMenu itemData, RecyclerItemMenuBinding itemBinding, int position) {
            super.bindView(itemData, itemBinding, position);
            if (position == 0) {
                itemBinding.vLine.setVisibility(View.GONE);
            } else {
                itemBinding.vLine.setVisibility(View.VISIBLE);
            }

            itemBinding.tvMenuName.setText(itemView.getContext().getString(itemData.getMenuName()));
            if (itemData.getMenuIcon() == -1) {
                itemBinding.imgMenuIcon.setImageResource(R.drawable.icon_menu_item_object);
            } else {
                try {
                    itemBinding.imgMenuIcon.setImageResource(itemData.getMenuIcon());
                } catch (Exception e) {
                    itemBinding.imgMenuIcon.setImageResource(R.drawable.icon_menu_item_object);
                }
            }
            itemView.setOnClickListener(new GwOnNoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }
    }

}