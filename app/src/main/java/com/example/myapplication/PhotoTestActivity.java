package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.king.naiveutils.base.BasePhotoHelper;
import com.king.naiveutils.base.BasePhotoPickerActivity;
import com.king.naiveutils.utils.FileUtils;
import com.king.naiveutils.utils.PermissionUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author NaiveKing
 * @date 2022/7/14
 */
public class PhotoTestActivity extends AppCompatActivity {

    private final int RC_CHOOSE_PHOTO = 10001;

    private final int RC_TACK_PHOTO = 10002;

    private final int RC_PHOTO_PREVIEW = 10003;

    private final int RC_EDIT_PHOTO = 10004;

    private BGASortableNinePhotoLayout photoLayout;

    /**
     * 图片管理类
     */
    private BasePhotoHelper photoHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_test);
        photoLayout = findViewById(R.id.photo_layout);

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    init();
                } else {
                    new AlertDialog.Builder(PhotoTestActivity.this)
                            .setTitle("提示")
                            .setMessage("No permission to take photos")
                            .setCancelable(false)
                            .setPositiveButton("确定", (dialog, which) -> {
                                dialog.cancel();
                                selectImage();
                            })
                            .setNegativeButton("取消", (dialog, which) -> dialog.cancel())
                            .setNeutralButton("设置", (dialog, which) -> {
                                dialog.cancel();
                                PermissionUtil.gotoPermission(PhotoTestActivity.this);
                            }).create().show();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                ToastUtils.showShort(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });


    }

    private void init() {
        photoLayout.setMaxItemCount(9);
        photoLayout.setDelegate(new BGASortableNinePhotoLayout.Delegate() {
            @Override
            public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
                //使用NinePhotoLayout控件时+号点击触发
                selectImage();
//                takePhoto();
            }

            @Override
            public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
                //Item项点击删除按钮
                photoLayout.removeItem(position);
            }

            @Override
            public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
                //BGAPhotoPickerPreviewActivity --查看大图预览
                Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(PhotoTestActivity.this)
                        .previewPhotos(models) // 当前预览的图片路径集合
                        .selectedPhotos(models) // 当前已选中的图片路径集合
                        .maxChooseCount(photoLayout.getMaxItemCount()) // 图片选择张数的最大值
                        .currentPosition(position) // 当前预览图片的索引
                        .isFromTakePhoto(false) // 是否是拍完照后跳转过来
                        .build();
                startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW);
            }

            @Override
            public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout, int fromPosition, int toPosition, ArrayList<String> models) {
                //拖动修改位置回调
                //                Toast.makeText(TestActivity.this, "排序发生变化", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 个性集成使用BGA选择图片
     */
    @SuppressLint("CheckResult")
    private void selectImage() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    //初始化PhotoHelper辅助类
                    if (photoHelper == null) {
                        photoHelper = new BasePhotoHelper(new File(FileUtils.getPhotosPath(PhotoTestActivity.this)));//拍照后照片存放的目录，必传！！
                    }
                    Intent photoPickerIntent = new BasePhotoPickerActivity.IntentBuilder(PhotoTestActivity.this)
                            .cameraFileDir(new File(FileUtils.getPhotosPath(PhotoTestActivity.this))) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                            .maxChooseCount(9 - photoLayout.getItemCount()) // 图片选择张数的最大值
                            .selectedPhotos(photoLayout.getData()) // 当前已选中的图片路径集合
                            .pauseOnScroll(true) // 滚动列表时是否暂停加载图片
                            .openEditPhoto(true)
                            .setTakePhotoBack(true)
                            .setRightText("上传")
                            .build();
                    startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
                } else {
                    new AlertDialog.Builder(PhotoTestActivity.this)
                            .setTitle("提示")
                            .setMessage("No permission to take photos")
                            .setCancelable(false)
                            .setPositiveButton("确定", (dialog, which) -> {
                                dialog.cancel();
                                selectImage();
                            })
                            .setNegativeButton("取消", (dialog, which) -> dialog.cancel())
                            .setNeutralButton("设置", (dialog, which) -> {
                                dialog.cancel();
                                PermissionUtil.gotoPermission(PhotoTestActivity.this);
                            }).create().show();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                ToastUtils.showShort(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 相机拍照
     */
    private void takePhoto() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    //初始化PhotoHelper辅助类
                    if (photoHelper == null) {
                        photoHelper = new BasePhotoHelper(new File(FileUtils.getPhotosPath(PhotoTestActivity.this)));//拍照后照片存放的目录，必传！！
                    }
                    try {
                        startActivityForResult(photoHelper.getTakePhotoIntent(), RC_TACK_PHOTO);
                    } catch (Exception e) {
                        LogUtils.d(e.getMessage());
                        BGAPhotoPickerUtil.show("Unable to take photos");
                    }
                } else {
                    new AlertDialog.Builder(PhotoTestActivity.this)
                            .setTitle("提示")
                            .setMessage("No permission to take photos")
                            .setCancelable(false)
                            .setPositiveButton("确定", (dialog, which) -> {
                                dialog.cancel();
                                selectImage();
                            })
                            .setNegativeButton("取消", (dialog, which) -> dialog.cancel())
                            .setNeutralButton("设置", (dialog, which) -> {
                                dialog.cancel();
                                PermissionUtil.gotoPermission(PhotoTestActivity.this);
                            }).create().show();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                ToastUtils.showShort(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO && data != null) {
            //图库选择图片回调
            //使用NinePhotoLayout控件时
            photoLayout.addMoreData(BasePhotoPickerActivity.getSelectedPhotos(data));
            //自定义显示属性时
            //            ArrayList<String> photos = BasePhotoPickerActivity.getSelectedPhotos(data);
        } else if (requestCode == RC_PHOTO_PREVIEW && data != null) {
            //使用NinePhotoLayout控件时，点击图片预览回调
            photoLayout.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
        } else if (requestCode == RC_TACK_PHOTO && resultCode == RESULT_OK) {
            //拍照回调
            //使用NinePhotoLayout控件时
            ArrayList<String> photos = new ArrayList<>(Collections.singletonList(photoHelper.getCameraFilePath()));
            photoLayout.addMoreData(photos);
        } else if (requestCode == RC_TACK_PHOTO && resultCode == RESULT_CANCELED) {
            //拍照预览取消回调，删除生成的意图文件
            photoHelper.deleteCameraFile();
        }
    }
}
