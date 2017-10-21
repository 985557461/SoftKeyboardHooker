package com.study.hooksoftkeyboard;

import android.app.Application;

import com.study.hooksoftkeyboard.hook.HookFactory;

/**
 * Created by xiaoyu on 2017/8/4.
 */
public class HookApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            HookFactory.getInstance().installHook(this,null);
            HookFactory.getInstance().setHookEnable(true);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
