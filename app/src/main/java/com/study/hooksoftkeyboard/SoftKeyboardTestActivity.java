package com.study.hooksoftkeyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.study.hooksoftkeyboard.listener.SoftKeyboardListener;

public class SoftKeyboardTestActivity extends AppCompatActivity implements SoftKeyboardListener{

    public static void open(Activity activity){
        Intent intent = new Intent(activity,SoftKeyboardTestActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.softkeyboard_test_activity);

        SFKeyboardManager.getInstance().registerListener(this);
    }

    @Override
    protected void onDestroy() {
        SFKeyboardManager.getInstance().unRegisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onKeyboardShow() {
        Toast.makeText(this, "键盘显示", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onKeyboardHide() {
        Toast.makeText(this, "键盘隐藏", Toast.LENGTH_SHORT).show();
    }
}
