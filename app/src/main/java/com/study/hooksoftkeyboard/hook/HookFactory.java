package com.study.hooksoftkeyboard.hook;

import android.content.Context;
import android.os.Build;

import com.study.hooksoftkeyboard.helper.Log;
import com.study.hooksoftkeyboard.hook.binder.IInputMethodManagerBinderHook;
import com.study.hooksoftkeyboard.hook.proxy.IActivityManagerHook;

import java.util.ArrayList;
import java.util.List;

public class HookFactory {
    private static final String TAG = HookFactory.class.getSimpleName();
    private static HookFactory sInstance = null;

    private List<Hook> mHookList = new ArrayList<>(3);

    private HookFactory() {
    }

    public static HookFactory getInstance() {
        synchronized (HookFactory.class) {
            if (sInstance == null) {
                sInstance = new HookFactory();
            }
        }
        return sInstance;
    }

    public void setHookEnable(boolean enable) {
        synchronized (mHookList) {
            for (Hook hook : mHookList) {
                hook.setEnable(enable);
            }
        }
    }

    public void setHookEnable(boolean enable, boolean reinstallHook) {
        synchronized (mHookList) {
            for (Hook hook : mHookList) {
                hook.setEnable(enable, reinstallHook);
            }
        }
    }

    public void setHookEnable(Class hookClass, boolean enable) {
        synchronized (mHookList) {
            for (Hook hook : mHookList) {
                if (hookClass.isInstance(hook)) {
                    hook.setEnable(enable);
                }
            }
        }
    }

    public void installHook(Hook hook, ClassLoader cl) {
        try {
            hook.onInstall(cl);
            synchronized (mHookList) {
                mHookList.add(hook);
            }
        } catch (Throwable throwable) {
            Log.e("xiaoyu", "installHook %s error", throwable, hook);
        }
    }

    //初始化所有的hook
    public final void installHook(Context context, ClassLoader classLoader) throws Throwable {
        installHook(new IActivityManagerHook(context), classLoader);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            installHook(new IInputMethodManagerBinderHook(context), classLoader);
        }
    }
}
