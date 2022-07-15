package com.king.naiveutils.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.blankj.utilcode.util.ToastUtils
import com.king.naiveutils.base.BaseActivity
import com.king.naiveutils.databinding.ActivityCameraScanBinding
import com.king.naiveutils.inter.GwOnNoDoubleClickListener
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * 手机摄像头扫描界面
 * @author NaiveKing
 * @date 2022/7/14
 */
class CameraScanActivity : BaseActivity<ActivityCameraScanBinding>(),
    ICameraScanView, QRCodeView.Delegate {


    companion object {
        @JvmStatic
        val RESULT_BACK_KEY = "RESULT_BACK_KEY"
    }

    class IntentBuilder(context: Context?) {
        private val mIntent: Intent
        fun build(): Intent {
            return mIntent
        }

        init {
            mIntent = Intent(context, CameraScanActivity::class.java)
        }
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        hideToolbar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val rxPermissions = RxPermissions(this)
            rxPermissions.request(Manifest.permission.CAMERA).subscribe(object : Observer<Boolean> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(aBoolean: Boolean) {
                    if (aBoolean) {
                        init()
                    } else {
                        finish()
                    }
                }

                override fun onError(e: Throwable) {
                    ToastUtils.showLong(e.message)
                }

                override fun onComplete() {}
            })
        } else {
            init()
        }
    }

    private var isOpenLight = false

    private fun init() {
        binding.zxingView.setDelegate(this)
        onStart()
        binding.tvOpenLight.setOnClickListener(object : GwOnNoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                isOpenLight = if (!isOpenLight) {
                    binding.zxingView.openFlashlight()
                    true
                } else {
                    binding.zxingView.closeFlashlight()
                    false
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // 打开后置摄像头开始预览，但是并未开始识别
        binding.zxingView.startCamera()
        // 打开前置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        // 显示扫描框，并开始识别
        binding.zxingView.startSpotAndShowRect()
    }

    override fun onStop() {
        binding.zxingView.stopCamera() // 关闭摄像头预览，并且隐藏扫描框
        super.onStop()
    }

    override fun onDestroy() {
        binding.zxingView.onDestroy()
        super.onDestroy()
    }

    override fun onBack() {
        finish()
    }

    override fun updateView() {

    }

    override fun onToolBarRightClick(id: Int) {

    }

    override fun showError(error: String?) {

    }

    override fun showToastMessage(msg: String?) {
        ToastUtils.showLong(msg)
    }

    override fun onScanQRCodeSuccess(result: String) {
        vibrate(this, 200)
        val intent = Intent()
        intent.putExtra(RESULT_BACK_KEY, result)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

    }

    override fun onScanQRCodeOpenCameraError() {
        showToastMessage("开启摄像头失败，请检查设备！")
    }

    /**
     * 振动
     *
     * @param activity     Activity实例
     * @param milliseconds 震动的时长，单位是毫秒
     */
    fun vibrate(activity: Activity, milliseconds: Long) {
        val vib = activity.getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(
                VibrationEffect.createOneShot(
                    milliseconds,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vib.vibrate(milliseconds)
        }
    }

}