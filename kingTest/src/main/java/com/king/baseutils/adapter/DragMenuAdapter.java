package com.king.baseutils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.king.baseutils.custom.bean.DragMenu;
import com.king.baseutils.databinding.RecyclerItemDragMenuBinding;

import java.util.ArrayList;

/**
 * 悬浮菜单MenuAdapter
 * Created by NaiveKing on 2021/06/25.
 */
public class DragMenuAdapter extends BaseAdapter {

    private final Context mContext;

    private final ArrayList<DragMenu> menuList;

    public DragMenuAdapter(Context mContext, ArrayList<DragMenu> menuList) {
        this.mContext = mContext;
        this.menuList = menuList;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public DragMenu getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecyclerItemDragMenuBinding menuBinding;
        if (convertView == null) {
            menuBinding = RecyclerItemDragMenuBinding.inflate(LayoutInflater.from(mContext));
            convertView = menuBinding.getRoot();
            convertView.setTag(menuBinding);
        } else {
            menuBinding = (RecyclerItemDragMenuBinding) convertView.getTag();
        }

        menuBinding.tvMenuName.setText(mContext.getString(menuList.get(position).getMenuName()));
        return convertView;
    }

}