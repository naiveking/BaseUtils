BaseUtils

github：https://github.com/naiveking/BaseUtils

说明：

    1、将Retrofit+RxJava+OkHttp封装的一个MVP开发框架抽离出来开源，其中依赖了固定版本的retrofit、RxJava、OkHttp，
    仅供自己库类整理以及学习记录，如有不足，欢迎反馈交流学习；
    控件绑定使用ViewBinding，库中Activity、RecycleAdapter控件绑定；BaseActivity、BasePresenter以及网络请求等使用请自行查看源代码；

    2、库中使用到三方库：BGAPhotoPick-Android
    gitee：https://gitee.com/huangjy1994/BGAPhotoPicker-Android/tree/master
    github：https://github.com/bingoogolapple/BGAPhotoPicker-Android

    其他支持库：
    com.squareup.retrofit2:retrofit
    io.reactivex.rxjava2:rxjava
    com.squareup.okhttp3
    com.blankj:utilcode
    cn.bingoogolapple:bga-photopicker
    cn.bingoogolapple:bga-baseadapter
    com.github.bumptech.glide:glide
    com.tbruyelle.rxpermissions2:rxpermissions
    cat.ereza:customactivityoncrash
    com.google.code.gson:gson
    
使用：

    1.添加 Gradle 依赖
        后面的「latestVersion」指的是GitHub打开https://github.com/naiveking/BaseUtils，Code栏右边Release分栏
        里显示的Latest版本号，请自行替换。请不要再来问我「latestVersion」是什么了。
        dependencies {
           implementation com.github.naiveking:BaseUtils:latestVersion
        }
        
    2.接口说明
    
        使用三方库BGAPhotoPickerActivity - 选择图片
        /**
         * 个性集成使用BGA选择图片
         */
        @SuppressLint("CheckResult")
        private void selectImage() {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(perms).subscribe(aBoolean -> {
                if (aBoolean) {
                    Intent photoPickerIntent = new BasePhotoPickerActivity.IntentBuilder(TestActivity.this)
                            .cameraFileDir(new File(CacheUtil.photosPath)) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                            .maxChooseCount(9 - photoLayout.getItemCount()) // 图片选择张数的最大值
                            .selectedPhotos(null) // 当前已选中的图片路径集合
                            .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                            .build();
                    startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(getText(R.string.hint))
                            .setMessage(getString(R.string.str_take_photo_permission_tip))
                            .setCancelable(false)
                            .setPositiveButton(getText(R.string.confirm), (dialog, which) -> {
                                dialog.cancel();
                                selectImage();
                            })
                            .setNegativeButton(getText(R.string.cancel), (dialog, which) -> {
                                dialog.cancel();
                            })
                            .setNeutralButton(R.string.set, (dialog, which) -> {
                                dialog.cancel();
                                PermissionUtil.gotoPermission(TestActivity.this);
                            }).create().show();
                }
            });
        }
        
        调用系统相机拍照（有使用到BasePhotoHelper三方库类）
        /**
         * 相机拍照
         */
         @SuppressLint("CheckResult")
            private void takePhoto() {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(perms).subscribe(aBoolean -> {
                if (aBoolean) {
                //初始化PhotoHelper辅助类
                    if (photoHelper == null) {
                        photoHelper = new BasePhotoHelper(new File(CacheUtil.photosPath));//拍照后照片存放的目录，必传！！
                    }
                    try {
                         startActivityForResult(photoHelper.getTakePhotoIntent(), RC_TACK_PHOTO);
                    } catch (Exception e) {
                         BGAPhotoPickerUtil.show(com.king.baseutils.R.string.bga_pp_not_support_take_photo);
                    }
                } else {
                    new AlertDialog.Builder(this)
                           .setTitle(getText(R.string.hint))
                           .setMessage(getString(R.string.str_take_photo_permission_tip))
                           .setCancelable(false)
                           .setPositiveButton(getText(R.string.confirm), (dialog, which) -> {
                               dialog.cancel();
                               takePhoto();
                           })
                           .setNegativeButton(getText(R.string.cancel), (dialog, which) -> {
                               dialog.cancel();
                           })
                           .setNeutralButton(R.string.set, (dialog, which) -> {
                               dialog.cancel();
                               PermissionUtil.gotoPermission(TestActivity.this);
                           }).create().show();
                }
            });
         }
    
        BGASortableNinePhotoLayout - 宫格照片选择控件
        
        --控件使用
            <cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout
                android:id="@+id/photo_layout"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_margin="5dp"
                app:bga_snpl_deleteDrawable="@mipmap/bga_pp_ic_delete"
                app:bga_snpl_deleteDrawableOverlapQuarter="false"
                app:bga_snpl_editable="true"
                app:bga_snpl_itemCornerRadius="0dp"
                app:bga_snpl_itemSpanCount="3"
                app:bga_snpl_itemWhiteSpacing="4dp"
                app:bga_snpl_itemWidth="0dp"
                app:bga_snpl_maxItemCount="9"
                app:bga_snpl_otherWhiteSpacing="0dp"
                app:bga_snpl_placeholderDrawable="@mipmap/bga_pp_ic_holder_light"
                app:bga_snpl_plusDrawable="@mipmap/bga_pp_ic_plus"
                app:bga_snpl_plusEnable="true"
                app:bga_snpl_sortable="true" />
        
        --代码使用
        photoLayout = findViewById(R.id.photo_layout);
                //设置可选择最大图片张数
                photoLayout.setMaxItemCount(9);
                photoLayout.setDelegate(new BGASortableNinePhotoLayout.Delegate() {
                    @Override
                    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
                        //使用NinePhotoLayout控件时+号点击触发
                        Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(TestActivity.this)
                                .cameraFileDir(null) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                                .maxChooseCount(9 - photoLayout.getItemCount()) // 图片选择张数的最大值
                                .selectedPhotos(null) // 当前已选中的图片路径集合
                                .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                                .build();
                        startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
                    }
        
                    @Override
                    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
                        //Item项点击删除按钮
                        photoLayout.removeItem(position);
                    }
        
                    @Override
                    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
                        //BGAPhotoPickerPreviewActivity --查看大图预览
                        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(TestActivity.this)
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
                //获取已添加的图片信息
                ArrayList<String> arrayList = photoLayout.getData();
                
        获取已选择的照片图片集合
        
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
                //图库选择图片回调
                //使用NinePhotoLayout控件时
                photoLayout.addMoreData(BasePhotoPickerActivity.getSelectedPhotos(data));
                //自定义显示属性时
    //            ArrayList<String> photos = BasePhotoPickerActivity.getSelectedPhotos(data);
            } else if (requestCode == RC_PHOTO_PREVIEW) {
                //使用NinePhotoLayout控件时，点击图片预览回调
                photoLayout.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
            } else if (requestCode == RC_TACK_PHOTO && resultCode == RESULT_OK) {
                //拍照回调
                //使用NinePhotoLayout控件时
                ArrayList<String> photos = new ArrayList<>(Collections.singletonList(photoHelper.getCameraFilePath()));
                photoLayout.addMoreData(photos);
                //自定义承接属性时
    //            Glide.with(TestActivity.this).load(photoHelper.getCameraFilePath()).into(imageView);
            } else if (requestCode == RC_TACK_PHOTO && resultCode == RESULT_CANCELED) {
                //拍照预览取消回调，删除生成的意图文件
                photoHelper.deleteCameraFile();
            }
        }
        
        上传图片 基于巨沃仓云业务  HttpClient
        
         /**
         * 可同时提交表单，和多文件
         * 根据url和键值对，发送异步Post请求
         *
         * @param uploadUrl    上传通信url地址
         * @param map          提交的表单的每一项组成的HashMap（如用户名，key:username,value:zhangsan）
         * @param imageFileKey 文件上传Builder key
         * @param fileNames    完整的上传的文件的路径名
         * @param callback     OkHttp的回调接口
         */
        public void doPostUploadRequest(String uploadUrl, HashMap<String, String> map, String imageFileKey, List<String> fileNames, Callback callback) {
            Call call = mDownClient.newCall(getRequest(uploadUrl, map, imageFileKey, fileNames));
            call.enqueue(callback);
        }
        
        下载文件 基于巨沃仓云业务  DownloadUtil
        
        /**
         * @param url         下载连接
         * @param versionName 下载文件版本名
         * @param listener    下载监听
         */
        public void download(final String url, String versionName, String downApkPath, final OnDownloadListener listener) 

        