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

import com.king.naiveutils.custom.view.ClearEditView;

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
                    Toast.makeText(MainActivity.this, "???????????????", Toast.LENGTH_LONG).show();
                    return;
                }
                mTvResult.setText(isScan ? "???????????????????????????" : "????????????????????????????????????");
            }
        });
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