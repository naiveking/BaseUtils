package com.king.naiveutils.custom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        menuList.add(new FloatMenuBean(FloatMenuAdapter.TYPE_OPEN_MENU, "开关"));
        changeMenu();
    }

    public void setOnItemMenuClick(FloatMenuAdapter.onItemMenuClick onItemMenuClick) {
        this.onItemMenuClick = onItemMenuClick;
    }

    public synchronized void addMenu(FloatMenuBean menuBean) {
        listData.add(0, menuBean);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, listData.size());
    }

    public synchronized void removeMenu(FloatMenuBean menuBean, int position) {
        listData.remove(menuBean);
        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, listData.size());
//        notifyItemRangeRemoved(position, 1);
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
                    if (onItemMenuClick != null) {
                        onItemMenuClick.onOpenClick();
                    }
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
        if (isOpen) {
            isOpen = false;
            for (int i = 0; i < menuList.size(); i++) {
                if (menuList.get(i).getMenuType() == TYPE_ITEM_MENU) {
                    removeMenu(menuList.get(i), i);
                }
            }
            notifyDataSetChanged();
        } else {
            isOpen = true;
            for (int i = 0; i < menuList.size(); i++) {
                if (menuList.get(i).getMenuType() == TYPE_ITEM_MENU) {
                    addMenu(menuList.get(i));
                }
            }
            boolean hasOpen = false;
            for (int i = 0; i < listData.size(); i++) {
                if (listData.get(i).getMenuType() == TYPE_OPEN_MENU) {
                    hasOpen = true;
                }
            }
            if (!hasOpen) {
                listData.add(listData.size(), new FloatMenuBean(FloatMenuAdapter.TYPE_OPEN_MENU, "开关"));
                notifyItemInserted(listData.size());
                notifyItemRangeChanged(0, listData.size());
            }
        }
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
        private LinearLayout mLlRootView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvMenu = itemView.findViewById(R.id.tv_menu_name);
            mLlRootView = itemView.findViewById(R.id.root_view);
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

        void onOpenClick();
    }
}
