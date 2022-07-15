package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.king.naiveutils.activity.CameraScanActivity;
import com.king.naiveutils.inter.GwOnNoDoubleClickListener;

/**
 * @author NaiveKing
 * @date 2022/7/14
 */
public class TestMenuActivity extends AppCompatActivity {

    ImageView mImgShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mImgShow = findViewById(R.id.img_show);
        Glide.with(this).load("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimages.h128.com%2Fupload%2F201908%2F24%2F201908240016412842.jpg%3Fx-oss-process%3Dimage%2Fresize%2Cm_lfit%2Cw_1421%2Fquality%2Cq_100%2Fformat%2Cjpg&refer=http%3A%2F%2Fimages.h128.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660384709&t=048e2d1efc7d3135a2b2bc6e37f4ced9")
                .into(mImgShow);

        findViewById(R.id.btn_input_test).setOnClickListener(new GwOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                startActivity(new Intent(TestMenuActivity.this, MainActivity.class));
            }
        });

        findViewById(R.id.btn_photo_test).setOnClickListener(new GwOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                startActivity(new Intent(TestMenuActivity.this, PhotoTestActivity.class));
            }
        });

        findViewById(R.id.btn_camera_test).setOnClickListener(new GwOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new CameraScanActivity.IntentBuilder(TestMenuActivity.this).build();
                startActivityForResult(intent, 100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            ToastUtils.showShort("扫描：" + data.getStringExtra(CameraScanActivity.Companion.getRESULT_BACK_KEY()));
        }
    }
}
