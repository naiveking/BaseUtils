package com.king.naiveutils.custom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.king.naiveutils.R;
import com.king.naiveutils.custom.bean.FloatMenuBean;
import com.king.naiveutils.inter.GwOnNoDoubleClickListener;

import java.util.ArrayList;


/**
 * @author NaiveKing
 * @date 2022/11/23
 */
public class FloatMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_OPEN_MENU = 1;

    public static final int TYPE_ITEM_MENU = 2;

    private boolean isOpen = false;

    private ArrayList<FloatMenuBean> listData;

    private ArrayList<FloatMenuBean> menuList;

    private onItemMenuClick onItemMenuClick;

    public FloatMenuAdapter(ArrayList<FloatMenuBean> listData) {
        this.listData = new ArrayList<>();
        this.menuList = listData;
        changeMenu();
    }

    public void setOnItemMenuClick(FloatMenuAdapter.onItemMenuClick onItemMenuClick) {
        this.onItemMenuClick = onItemMenuClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_ITEM_MENU) {
            viewHolder = new MenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_menu_item, parent, false));
        } else {
            viewHolder = new MenuButtonHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_menu_button, parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MenuButtonHolder) {
            ((MenuButtonHolder) holder).mImgButton.setImageResource(isOpen ? R.drawable.icon_to_left : R.drawable.icon_to_right);
            ((MenuButtonHolder) holder).mImgButton.setOnClickListener(new GwOnNoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    changeMenu();
                }
            });
        } else if (holder instanceof MenuViewHolder) {
            ((MenuViewHolder) holder).mTvMenu.setText(listData.get(position).getMenuName());
            ((MenuViewHolder) holder).mTvMenu.setOnClickListener(new GwOnNoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if (onItemMenuClick != null) {
                        onItemMenuClick.onItemClick(listData.get(holder.getAdapterPosition())
                                , holder.getAdapterPosition());
                    }
                }
            });
        }
    }

    public void changeMenu() {
        listData.clear();
        if (isOpen) {
            isOpen = false;
        } else {
            isOpen = true;
            listData.addAll(menuList);
        }
        listData.add(new FloatMenuBean(FloatMenuAdapter.TYPE_OPEN_MENU, ""));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listData.get(position).getMenuType();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvMenu;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvMenu = itemView.findViewById(R.id.tv_menu_name);
        }
    }

    class MenuButtonHolder extends RecyclerView.ViewHolder {
        private ImageView mImgButton;

        public MenuButtonHolder(@NonNull View itemView) {
            super(itemView);
            mImgButton = itemView.findViewById(R.id.img_button);
        }
    }

    public interface onItemMenuClick {
        void onItemClick(FloatMenuBean menuBean, int position);
    }
}
