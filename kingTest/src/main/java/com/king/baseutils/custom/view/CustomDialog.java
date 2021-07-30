package com.king.baseutils.custom.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.king.baseutils.R;


/**
 * @author Gwall
 * @date 2019/11/14
 */
public class CustomDialog extends Dialog {


    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context mContext;
        private String mTitle;
        private String mMessage;
        private String mPositiveButtonText;
        private String mNegativeButtonText;
        private View mContentView;
        private OnClickListener mPositiveButtonClickListener;
        private OnClickListener mNegativeButtonClickListener;
        private boolean cancelable = true;
        private int animations = R.style.dialogWindowAnim;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setTitle(int title) {
            this.mTitle = (String) mContext.getText(title);
            return this;
        }

        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.mMessage = (String) mContext.getText(message);
            return this;
        }

        public Builder setContentView(View view) {
            this.mContentView = view;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;

            return this;
        }

        public Builder setAnimations(int animations){
            this.animations = animations;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.mNegativeButtonText = (String) mContext.getText(negativeButtonText);
            this.mNegativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.mNegativeButtonText = negativeButtonText;
            this.mNegativeButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.mPositiveButtonText = (String) mContext.getText(positiveButtonText);
            this.mPositiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.mPositiveButtonText = positiveButtonText;
            this.mPositiveButtonClickListener = listener;
            return this;
        }

        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog dialog = new CustomDialog(mContext, R.style.CustomDialog);
            dialog.setCancelable(cancelable);
            if (animations!= -1){
                dialog.getWindow().setWindowAnimations(animations);
            }
            View layout = inflater.inflate(R.layout.dialog_normal_layout, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT));
            if (TextUtils.isEmpty(mTitle)) {
                ((TextView) layout.findViewById(R.id.tv_dialog_title)).setText(mContext.getString(R.string.base_utils_tips));
            } else {
                ((TextView) layout.findViewById(R.id.tv_dialog_title)).setText(mTitle);
            }
            if (mPositiveButtonText != null) {
                ((TextView) layout.findViewById(R.id.tv_positive)).setText(mPositiveButtonText);
                if (mPositiveButtonClickListener != null) {
                    layout.findViewById(R.id.tv_positive).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPositiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.tv_positive).setVisibility(View.GONE);
                layout.findViewById(R.id.v_line).setVisibility(View.GONE);
            }

            if (mNegativeButtonText != null) {
                ((TextView) layout.findViewById(R.id.tv_negative)).setText(mNegativeButtonText);
                if (mPositiveButtonClickListener != null) {
                    layout.findViewById(R.id.tv_negative).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNegativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.tv_negative).setVisibility(View.GONE);
                layout.findViewById(R.id.v_line).setVisibility(View.GONE);
            }
            if (mMessage != null) {
                ((TextView) layout.findViewById(R.id.tv_message)).setText(mMessage);
            } else {
                layout.findViewById(R.id.tv_message).setVisibility(View.GONE);
            }
            if (mContentView != null) {
                ((LinearLayout) layout.findViewById(R.id.ll_content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.ll_content)).addView(mContentView,
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
//            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//            dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            return dialog;
        }
    }
}