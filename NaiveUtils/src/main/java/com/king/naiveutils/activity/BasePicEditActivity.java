package com.king.naiveutils.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;

import androidx.annotation.Nullable;

import com.king.naiveutils.emoji.Emoji;
import com.king.naiveutils.emoji.EmojiDrawer;
import com.king.naiveutils.emoji.IEmojiCallback;
import com.vachel.editor.PictureEditActivity;
import com.vachel.editor.bean.StickerText;
import com.vachel.editor.util.Utils;

import java.io.File;


public class BasePicEditActivity extends PictureEditActivity implements IEmojiCallback {

    public static final String RESULT_BACK_KEY = "RESULT_BACK_KEY";

    public static class IntentBuilder {
        private final Intent mIntent;

        public IntentBuilder(Context context) {
            mIntent = new Intent(context, BasePicEditActivity.class);
        }

        public BasePicEditActivity.IntentBuilder filePath(@Nullable String path) {
            assert path != null;
            Uri uri = Uri.fromFile(new File(path));
            mIntent.putExtra(EXTRA_IMAGE_URI, uri);
            return this;
        }

        public Intent build() {
            return mIntent;
        }
    }

    @Override
    public void initData() {
        mSupportEmoji = true;
    }

    @Override
    public View getStickerLayout() {
        return new EmojiDrawer(this).bindCallback(this);
    }

    @Override
    public void onEmojiClick(String emoji) {
        StickerText stickerText = new StickerText(emoji, Color.WHITE);
        onText(stickerText, false); // emoji其实也是text文本
        Utils.dismissDialog(mStickerImageDialog);
    }

    @Override
    public void onBackClick() {
        mStickerImageDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Emoji.recycleAllEmoji();
    }

    @Override
    public void onSaveSuccess(String savePath) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_BACK_KEY, savePath);
        setResult(RESULT_OK, intent);
        finish();
    }

}
