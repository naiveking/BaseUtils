package com.king.naiveutils.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.king.naiveutils.R;
import com.king.naiveutils.adapter.BaseRecyclerAdapter;
import com.king.naiveutils.adapter.DragItemAdapter;
import com.king.naiveutils.custom.bean.DragMenu;

import java.util.List;

/**
 * 配合DragView 子菜单显示Dialog
 * Created by NaiveKing on 2021/07/23.
 */
public class DragMenuDialog extends Dialog {

    public DragMenuDialog(Context context) {
        super(context);
    }

    public DragMenuDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context mContext;

        private boolean cancelable = true;

        private int animations = R.style.dialogWindowAnim;

        private DragItemAdapter menuAdapter;

        private List<DragMenu> menuList;

        private boolean showDialogBottom = true;

        private OnMenuItemClickListener onMenuItemClickListener;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setAnimations(int animations) {
            this.animations = animations;
            return this;
        }

        public Builder setShowDialogBottom(boolean showBottom) {
            this.showDialogBottom = showBottom;
            return this;
        }

        public Builder setMenuList(List<DragMenu> menuList) {
            this.menuList = menuList;
            return this;
        }

        public Builder setOnItemMenuClickListener(OnMenuItemClickListener listener) {
            this.onMenuItemClickListener = listener;
            return this;
        }

        public DragMenuDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final DragMenuDialog dialog = new DragMenuDialog(mContext, R.style.CustomDialog);
            dialog.setCancelable(cancelable);
            if (animations != -1) {
                dialog.getWindow().setWindowAnimations(animations);
            }
            View layout = inflater.inflate(R.layout.dialog_drag_menu_layout, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout mLlMenuRoot = layout.findViewById(R.id.ll_menu_root);
            if (showDialogBottom) {
                mLlMenuRoot.setBackgroundResource(R.drawable.bg_drag_menu_dialog_bottom);
            } else {
                mLlMenuRoot.setBackgroundResource(R.drawable.bg_drag_menu_dialog);
            }

            RecyclerView mRlvMenu = layout.findViewById(R.id.rlv_drag_menu);
            mRlvMenu.setLayoutManager(new LinearLayoutManager(mContext));
            mRlvMenu.setOverScrollMode(View.OVER_SCROLL_NEVER);
            if (menuAdapter == null) {
                menuAdapter = new DragItemAdapter();
                menuAdapter.setOnItemClickListener(new BaseRecyclerAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (onMenuItemClickListener != null) {
                            onMenuItemClickListener.onItemMenuClick(position);
                        }
                    }
                });
            }
            mRlvMenu.setAdapter(menuAdapter);
            menuAdapter.setData(menuList);
            menuAdapter.notifyDataSetChanged();
            dialog.setContentView(layout);
            return dialog;
        }
    }

    public interface OnMenuItemClickListener {
        void onItemMenuClick(int position);
    }
} 