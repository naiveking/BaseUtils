package com.king.naiveutils.base;

import static com.king.naiveutils.activity.BasePicEditActivity.RESULT_BACK_KEY;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.king.naiveutils.R;
import com.king.naiveutils.activity.BasePicEditActivity;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

import cn.bingoogolapple.baseadapter.BGAGridDivider;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener;
import cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.adapter.BGAPhotoPickerAdapter;
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener;
import cn.bingoogolapple.photopicker.model.BGAPhotoFolderModel;
import cn.bingoogolapple.photopicker.pw.BGAPhotoFolderPw;
import cn.bingoogolapple.photopicker.util.BGAAsyncTask;
import cn.bingoogolapple.photopicker.util.BGALoadPhotoTask;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;

/**
 * 继承BGAPhotoPickerActivity进行部分修改适配
 * Created by NaiveKing on 2021/06/02.
 */
public class BasePhotoPickerActivity extends BGAPPToolbarActivity implements BGAOnItemChildClickListener
        , BGAAsyncTask.Callback<ArrayList<BGAPhotoFolderModel>> {

    private static final String EXTRA_CAMERA_FILE_DIR = "EXTRA_CAMERA_FILE_DIR";
    private static final String EXTRA_SELECTED_PHOTOS = "EXTRA_SELECTED_PHOTOS";
    private static final String EXTRA_MAX_CHOOSE_COUNT = "EXTRA_MAX_CHOOSE_COUNT";
    private static final String EXTRA_PAUSE_ON_SCROLL = "EXTRA_PAUSE_ON_SCROLL";
    private static final String STATE_SELECTED_PHOTOS = "STATE_SELECTED_PHOTOS";
    private static final String EXTRA_EDIT_PHOTOS = "EXTRA_EDIT_PHOTOS";
    private static final String EXTRA_TAKE_PHOTO_BACK = "EXTRA_TAKE_PHOTO_BACK";
    private static final String EXTRA_RIGHT_TEXT = "EXTRA_RIGHT_TEXT";

    /**
     * 拍照的请求码
     */
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    /**
     * 预览照片的请求码
     */
    private static final int RC_PREVIEW = 2;

    private static final int SPAN_COUNT = 3;

    private static final int RC_EDIT = 4;

    private TextView mTitleTv;
    private ImageView mArrowIv;
    private TextView mSubmitTv;
    private RecyclerView mContentRv;

    private BGAPhotoFolderModel mCurrentPhotoFolderModel;

    /**
     * 是否可以拍照
     */
    private boolean mTakePhotoEnabled;
    /**
     * 最多选择多少张图片，默认等于1，为单选
     */
    private int mMaxChooseCount = 1;
    /**
     * 右上角按钮文本
     */
    private String mTopRightBtnText;
    /**
     * 图片目录数据集合
     */
    private ArrayList<BGAPhotoFolderModel> mPhotoFolderModels;

    private BGAPhotoPickerAdapter mPicAdapter;

    private BasePhotoHelper mPhotoHelper;

    private BGAPhotoFolderPw mPhotoFolderPw;

    private BGALoadPhotoTask mLoadPhotoTask;
    private AppCompatDialog mLoadingDialog;

    private boolean isEditPhoto;

    private boolean takePhotoBackToAlbum;

    private final BGAOnNoDoubleClickListener mOnClickShowPhotoFolderListener = new BGAOnNoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            if (mPhotoFolderModels != null && mPhotoFolderModels.size() > 0) {
                showPhotoFolderPw();
            }
        }
    };

    public static class IntentBuilder {
        private final Intent mIntent;

        public IntentBuilder(Context context) {
            mIntent = new Intent(context, BasePhotoPickerActivity.class);
        }

        /**
         * 拍照后图片保存的目录。如果传 null 表示没有拍照功能，如果不为 null 则具有拍照功能，
         */
        public IntentBuilder cameraFileDir(@Nullable File cameraFileDir) {
            mIntent.putExtra(EXTRA_CAMERA_FILE_DIR, cameraFileDir);
            return this;
        }

        /**
         * 图片选择张数的最大值
         *
         * @param maxChooseCount 单次选择的最大张数
         * @return 构建
         */
        public IntentBuilder maxChooseCount(int maxChooseCount) {
            mIntent.putExtra(EXTRA_MAX_CHOOSE_COUNT, maxChooseCount);
            return this;
        }

        /**
         * 当前已选中的图片路径集合，可以传 null
         */
        public IntentBuilder selectedPhotos(@Nullable ArrayList<String> selectedPhotos) {
            mIntent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, selectedPhotos);
            return this;
        }

        /**
         * 滚动列表时是否暂停加载图片，默认为 false
         */
        public IntentBuilder pauseOnScroll(boolean pauseOnScroll) {
            mIntent.putExtra(EXTRA_PAUSE_ON_SCROLL, pauseOnScroll);
            return this;
        }

        /**
         * 拍照、选择单张图片后是否进行编辑图片，默认为 false
         */
        public IntentBuilder openEditPhoto(boolean openEdit) {
            mIntent.putExtra(EXTRA_EDIT_PHOTOS, openEdit);
            return this;
        }

        /**
         * 拍照后返回选择照片相册，默认为 false，返回调用界面
         */
        public IntentBuilder setTakePhotoBack(boolean takePhotoBack) {
            mIntent.putExtra(EXTRA_TAKE_PHOTO_BACK, takePhotoBack);
            return this;
        }

        /**
         * 设置右上角按钮文字
         */
        public IntentBuilder setRightText(String text) {
            mIntent.putExtra(EXTRA_RIGHT_TEXT, text);
            return this;
        }

        public Intent build() {
            return mIntent;
        }
    }


    /**
     * 获取已选择的图片集合
     *
     * @param intent 意图
     * @return 解析图片地址List
     */
    public static ArrayList<String> getSelectedPhotos(Intent intent) {
        return intent.getStringArrayListExtra(EXTRA_SELECTED_PHOTOS);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.bga_pp_activity_photo_picker);
        mContentRv = findViewById(R.id.rv_photo_picker_content);
    }

    @Override
    protected void setListener() {
        mPicAdapter = new BGAPhotoPickerAdapter(mContentRv);
        mPicAdapter.setOnItemChildClickListener(this);

        if (getIntent().getBooleanExtra(EXTRA_PAUSE_ON_SCROLL, false)) {
            mContentRv.addOnScrollListener(new BGARVOnScrollListener(this));
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        // 获取拍照图片保存目录
        File cameraFileDir = (File) getIntent().getSerializableExtra(EXTRA_CAMERA_FILE_DIR);
        if (cameraFileDir != null) {
            mTakePhotoEnabled = true;
            mPhotoHelper = new BasePhotoHelper(cameraFileDir);
        }
        // 获取图片选择的最大张数
        mMaxChooseCount = getIntent().getIntExtra(EXTRA_MAX_CHOOSE_COUNT, 1);
        if (mMaxChooseCount < 1) {
            mMaxChooseCount = 1;
        }

        isEditPhoto = getIntent().getBooleanExtra(EXTRA_EDIT_PHOTOS, false);
        takePhotoBackToAlbum = getIntent().getBooleanExtra(EXTRA_TAKE_PHOTO_BACK, false);
        mTopRightBtnText = getIntent().getStringExtra(EXTRA_RIGHT_TEXT);
        if (TextUtils.isEmpty(mTopRightBtnText)) {
            // 获取右上角按钮文本
            mTopRightBtnText = getString(R.string.bga_pp_confirm);
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, SPAN_COUNT, LinearLayoutManager.VERTICAL, false);
        mContentRv.setLayoutManager(layoutManager);
        mContentRv.addItemDecoration(BGAGridDivider.newInstanceWithSpaceRes(R.dimen.bga_pp_size_photo_divider));

        ArrayList<String> selectedPhotos = getIntent().getStringArrayListExtra(EXTRA_SELECTED_PHOTOS);
        if (selectedPhotos != null && selectedPhotos.size() > mMaxChooseCount) {
            String selectedPhoto = selectedPhotos.get(0);
            selectedPhotos.clear();
            selectedPhotos.add(selectedPhoto);
        }

        mContentRv.setAdapter(mPicAdapter);
        mPicAdapter.setSelectedPhotos(selectedPhotos);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLoadingDialog();
        mLoadPhotoTask = new BGALoadPhotoTask(this, this, mTakePhotoEnabled).perform();
    }

    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new AppCompatDialog(this);
            mLoadingDialog.setContentView(R.layout.bga_pp_dialog_loading);
            mLoadingDialog.setCancelable(false);
        }
        mLoadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_pp_menu_photo_picker, menu);
        MenuItem menuItem = menu.findItem(R.id.item_photo_picker_title);
        View actionView = menuItem.getActionView();

        mTitleTv = actionView.findViewById(R.id.tv_photo_picker_title);
        mArrowIv = actionView.findViewById(R.id.iv_photo_picker_arrow);
        mSubmitTv = actionView.findViewById(R.id.tv_photo_picker_submit);

        mTitleTv.setOnClickListener(mOnClickShowPhotoFolderListener);
        mArrowIv.setOnClickListener(mOnClickShowPhotoFolderListener);
        mSubmitTv.setOnClickListener(new BGAOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                returnSelectedPhotos(mPicAdapter.getSelectedPhotos());
            }
        });

        mTitleTv.setText(R.string.bga_pp_all_image);
        if (mCurrentPhotoFolderModel != null) {
            mTitleTv.setText(mCurrentPhotoFolderModel.name);
        }

        renderTopRightBtn();
        return true;
    }

    /**
     * 返回已选中的图片集合
     *
     * @param selectedPhotos 图片地址集合
     */
    private void returnSelectedPhotos(ArrayList<String> selectedPhotos) {
        if (selectedPhotos.size() == 1 && isEditPhoto) {
            Intent intent = new BasePicEditActivity.IntentBuilder(this)
                    .filePath(selectedPhotos.get(0)).build();
            startActivityForResult(intent, RC_EDIT);
            return;
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, selectedPhotos);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void returnEditPhoto(String savePath) {
        ArrayList<String> editPhoto = new ArrayList<>();
        editPhoto.add(savePath);
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, editPhoto);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showPhotoFolderPw() {
        if (mArrowIv == null) {
            return;
        }

        if (mPhotoFolderPw == null) {
            mPhotoFolderPw = new BGAPhotoFolderPw(this, mToolbar, new BGAPhotoFolderPw.Delegate() {
                @Override
                public void onSelectedFolder(int position) {
                    reloadPhotos(position);
                }

                @Override
                public void executeDismissAnim() {
                    ViewCompat.animate(mArrowIv).setDuration(BGAPhotoFolderPw.ANIM_DURATION).rotation(0).start();
                }
            });
        }
        mPhotoFolderPw.setData(mPhotoFolderModels);
        mPhotoFolderPw.show();

        ViewCompat.animate(mArrowIv).setDuration(BGAPhotoFolderPw.ANIM_DURATION).rotation(-180).start();
    }

    /**
     * 显示只能选择 mMaxChooseCount 张图的提示
     */
    private void toastMaxCountTip() {
        BGAPhotoPickerUtil.show(getString(R.string.bga_pp_toast_photo_picker_max, mMaxChooseCount));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            ArrayList<String> photos = new ArrayList<>(Collections.singletonList(mPhotoHelper.getCameraFilePath()));
            Intent photoPickerPreview = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                    .isFromTakePhoto(true)
                    .maxChooseCount(1)
                    .previewPhotos(photos)
                    .selectedPhotos(photos)
                    .currentPosition(0)
                    .build();
            startActivityForResult(photoPickerPreview, RC_PREVIEW);
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_CANCELED) {
            //拍照预览取消回调，删除生成的意图文件
            mPhotoHelper.deleteCameraFile();
        } else if (requestCode == RC_PREVIEW && resultCode == RESULT_OK) {
            if (BGAPhotoPickerPreviewActivity.getIsFromTakePhoto(data)) {
                // 从拍照预览界面返回，刷新图库
                mPhotoHelper.refreshGallery(this);
            }
            if (!takePhotoBackToAlbum) {
                returnSelectedPhotos(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
            }
        } else if (requestCode == RC_PREVIEW && resultCode == RESULT_CANCELED) {
            if (BGAPhotoPickerPreviewActivity.getIsFromTakePhoto(data)) {
                // 从拍照预览界面返回，删除之前拍的照片
                mPhotoHelper.deleteCameraFile();
            } else {
                mPicAdapter.setSelectedPhotos(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
                renderTopRightBtn();
            }
        } else if (requestCode == RC_EDIT && resultCode == RESULT_OK) {
            returnEditPhoto(data.getStringExtra(RESULT_BACK_KEY));
        }
    }

    /**
     * 渲染右上角按钮
     */
    private void renderTopRightBtn() {
        if (mSubmitTv == null) {
            return;
        }

        if (mPicAdapter.getSelectedCount() == 0) {
            mSubmitTv.setEnabled(false);
            mSubmitTv.setText(mTopRightBtnText);
        } else {
            mSubmitTv.setEnabled(true);
            mSubmitTv.setText(MessageFormat.format("{0}({1}/{2})", mTopRightBtnText, mPicAdapter.getSelectedCount(), mMaxChooseCount));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        BasePhotoHelper.onSaveInstanceState(mPhotoHelper, outState);
        outState.putStringArrayList(STATE_SELECTED_PHOTOS, mPicAdapter.getSelectedPhotos());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        BasePhotoHelper.onRestoreInstanceState(mPhotoHelper, savedInstanceState);
        mPicAdapter.setSelectedPhotos(savedInstanceState.getStringArrayList(STATE_SELECTED_PHOTOS));
    }

    @Override
    public void onItemChildClick(ViewGroup viewGroup, View view, int position) {
        if (view.getId() == R.id.iv_item_photo_camera_camera) {
            handleTakePhoto();
        } else if (view.getId() == R.id.iv_item_photo_picker_photo) {
            changeToPreview(position);
        } else if (view.getId() == R.id.iv_item_photo_picker_flag) {
            handleClickSelectFlagIv(position);
        }
    }

    /**
     * 处理拍照
     */
    private void handleTakePhoto() {
        if (mMaxChooseCount == 1) {
            // 单选
            takePhoto();
        } else if (mPicAdapter.getSelectedCount() == mMaxChooseCount) {
            toastMaxCountTip();
        } else {
            takePhoto();
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        try {
            startActivityForResult(mPhotoHelper.getTakePhotoIntent(), REQUEST_CODE_TAKE_PHOTO);
        } catch (Exception e) {
            LogUtils.d(e.getMessage());
            mPhotoHelper.deleteCameraFile();
            BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_take_photo);
        }
    }

    /**
     * 跳转到图片选择预览界面
     *
     * @param position 当前点击的item的索引位置
     */
    private void changeToPreview(int position) {
        int currentPosition = position;
        if (mCurrentPhotoFolderModel.isTakePhotoEnabled()) {
            currentPosition--;
        }
        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                .previewPhotos((ArrayList<String>) mPicAdapter.getData())
                .selectedPhotos(mPicAdapter.getSelectedPhotos())
                .maxChooseCount(mMaxChooseCount)
                .currentPosition(currentPosition)
                .isFromTakePhoto(false)
                .build();
        startActivityForResult(photoPickerPreviewIntent, RC_PREVIEW);
    }

    /**
     * 处理点击选择按钮事件
     *
     * @param position 当前点击的item的索引位置
     */
    private void handleClickSelectFlagIv(int position) {
        String currentPhoto = mPicAdapter.getItem(position);
        if (mMaxChooseCount == 1) {
            // 单选
            if (mPicAdapter.getSelectedCount() > 0) {
                String selectedPhoto = mPicAdapter.getSelectedPhotos().remove(0);
                if (!TextUtils.equals(selectedPhoto, currentPhoto)) {
                    int preSelectedPhotoPosition = mPicAdapter.getData().indexOf(selectedPhoto);
                    mPicAdapter.notifyItemChanged(preSelectedPhotoPosition);
                    mPicAdapter.getSelectedPhotos().add(currentPhoto);
                }
            } else {
                mPicAdapter.getSelectedPhotos().add(currentPhoto);
            }
            mPicAdapter.notifyItemChanged(position);
            renderTopRightBtn();
        } else {
            // 多选
            if (!mPicAdapter.getSelectedPhotos().contains(currentPhoto) && mPicAdapter.getSelectedCount() == mMaxChooseCount) {
                toastMaxCountTip();
            } else {
                if (mPicAdapter.getSelectedPhotos().contains(currentPhoto)) {
                    mPicAdapter.getSelectedPhotos().remove(currentPhoto);
                } else {
                    mPicAdapter.getSelectedPhotos().add(currentPhoto);
                }
                mPicAdapter.notifyItemChanged(position);

                renderTopRightBtn();
            }
        }
    }

    private void reloadPhotos(int position) {
        if (position < mPhotoFolderModels.size()) {
            mCurrentPhotoFolderModel = mPhotoFolderModels.get(position);
            if (mTitleTv != null) {
                mTitleTv.setText(mCurrentPhotoFolderModel.name);
            }
            mPicAdapter.setPhotoFolderModel(mCurrentPhotoFolderModel);
        }
    }

    @Override
    public void onPostExecute(ArrayList<BGAPhotoFolderModel> photoFolderModels) {
        dismissLoadingDialog();
        mLoadPhotoTask = null;
        mPhotoFolderModels = photoFolderModels;
        reloadPhotos(mPhotoFolderPw == null ? 0 : mPhotoFolderPw.getCurrentPosition());
    }

    @Override
    public void onTaskCancelled() {
        dismissLoadingDialog();
        mLoadPhotoTask = null;
    }

    private void cancelLoadPhotoTask() {
        if (mLoadPhotoTask != null) {
            mLoadPhotoTask.cancelTask();
            mLoadPhotoTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        cancelLoadPhotoTask();
        super.onDestroy();
    }
}
