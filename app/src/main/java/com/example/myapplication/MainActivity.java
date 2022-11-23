package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.king.naiveutils.custom.adapter.FloatMenuAdapter;
import com.king.naiveutils.custom.bean.FloatMenuBean;
import com.king.naiveutils.custom.view.ClearEditView;
import com.king.naiveutils.custom.view.FloatMenuView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    boolean isScan = true;

    ClearEditView mEtTest;

    TextView mTvResult;

    CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCheckBox = findViewById(R.id.checkbox);
        mEtTest = findViewById(R.id.etTest);
        mEtTest.requestFocus();
        mTvResult = findViewById(R.id.tvResult);

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEtTest.setEnabled(false);
                } else {
                    mEtTest.setEnabled(true);
                }
            }
        });

        mEtTest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    isScan = true;
                }
            }
        });

        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvResult.setText("");
                mEtTest.setText("");
                mEtTest.requestFocus();
            }
        });

        findViewById(R.id.btnCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtTest.getText().toString().trim())) {
                    Toast.makeText(MainActivity.this, "请输入内容", Toast.LENGTH_LONG).show();
                    return;
                }
                mTvResult.setText(isScan ? "输入结果：扫描输入" : "输入结果：监测到按键输入");
            }
        });
//        String url = "http://218.19.34.251:19011/gwms_hd_package/uploadInstallpackageDownload/Gw_release_4.5.743_53_220811.apk";
//        DownloadUtil.get().download(url, "V1.0.0", FileUtils.getDownloadPath(MainActivity.this), new DownloadUtil.OnDownloadListener() {
//            @Override
//            public void onDownloadSuccess(File file) {
//                ToastUtils.showShort("下载成功");
//            }
//
//            @Override
//            public void onDownloading(int progress) {
//
//            }
//
//            @Override
//            public void onDownloadFailed(String error) {
//                ToastUtils.showShort("下载失败" + error);
//            }
//        });

        ArrayList<FloatMenuBean> list = new ArrayList<>();
        list.add(new FloatMenuBean(FloatMenuAdapter.TYPE_ITEM_MENU, "审核"));
        list.add(new FloatMenuBean(FloatMenuAdapter.TYPE_ITEM_MENU, "装箱"));
        list.add(new FloatMenuBean(FloatMenuAdapter.TYPE_ITEM_MENU, "下班"));
        new FloatMenuView.Builder().setActivity(this).setMenuItem(list)
                .setListener(new FloatMenuView.onMenuItemClickListener() {
                    @Override
                    public void onItemClick(int position, FloatMenuBean menuBean) {
                        ToastUtils.showLong("点击：" + position + "/" + menuBean.getMenuName());
                    }
                }).build();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                    isScan = false;
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}